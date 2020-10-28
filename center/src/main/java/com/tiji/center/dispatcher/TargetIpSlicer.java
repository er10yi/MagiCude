package com.tiji.center.dispatcher;

import com.tiji.center.pojo.Assetip;
import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Task;
import com.tiji.center.service.AssetipService;
import com.tiji.center.service.AssetportService;
import com.tiji.center.service.TaskService;
import com.tiji.center.service.TaskipService;
import com.tiji.center.util.TijiHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import util.NameSuffix;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 目标ip分组进redis
 */

public class TargetIpSlicer {

    public static String slice(String targetId, Map<String, Object> taskInfo, AssetipService assetipService, TaskipService taskipService, TaskService taskService, AssetportService assetportService, RedisTemplate<String, String> redisTemplate, RabbitMessagingTemplate rabbitMessagingTemplate, double agentCount) throws InterruptedException {
        String nameSuffix = "_" + NameSuffix.gen();
        //Redis
        String rawIPSet = "rawIPSet" + nameSuffix;//原始目标IP
        String excludeIPSet = "excludeIPSet" + nameSuffix;//排除的IP
        String targetIPSet = "targetIPSet" + nameSuffix; //目标IP
        String sliceIPList = "sliceIPList" + nameSuffix;//分组IP
        Task task = taskService.findById(targetId);
        //ip无端口，全端口扫描任务
        String targetip = task.getTargetip();
        if ("ipNoPort".equals(targetip)) {
            List<String> allAssetipNoPort = taskipService.findAllAssetipNoPort();
            System.out.println("allAssetipNoPort " + allAssetipNoPort);
            if (allAssetipNoPort.isEmpty())
                return null;
            TijiHelper.target2Redis(allAssetipNoPort.toString().replaceAll("[\\[\\]\\s]", ""), redisTemplate, rawIPSet);
        } else if ("unknownPortSerVer".equals(targetip) || "ipAllPort".equals(targetip)) {
            //ip有端口，端口服务未知
            //未知服务/版本的端口 => mass2Nmap模式的二次扫描 => 丢给mq
            //获取所有asset service的ip和端口：为空、null、tcpwrapped、unknown、包含?
            List<Assetport> assetportList = new ArrayList<>();

            Map<String, Object> searchMap = new HashMap<>();
            searchMap.put("state", "open");
            searchMap.put("checkwhitelist", false);
            searchMap.put("downtime", null);

            if ("unknownPortSerVer".equals(targetip)) {
                searchMap.put("service", "tcpwrapped");
                assetportList.addAll(assetportService.findSearch(searchMap));
                searchMap.put("service", "unknown");
                assetportList.addAll(assetportService.findSearch(searchMap));
                searchMap.put("service", "?");
                assetportList.addAll(assetportService.findSearch(searchMap));
                searchMap.put("service", "null");
                assetportList.addAll(assetportService.findSearch(searchMap));
                //为空的service
                assetportList.addAll(assetportService.findAllByServiceAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(null));

                //获取所有asset verison：为空、null
                //version
                searchMap.put("version", "null");
                //为空的version
                assetportList.addAll(assetportService.findAllByVersionAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(null));
            }
            if ("ipAllPort".equals(targetip)) {
                assetportList.addAll(assetportService.findSearch(searchMap));
            }

            if (assetportList.isEmpty())
                return null;
            List<String> resultAssetportList = new ArrayList<>();
            assetportList.parallelStream().forEach(assetport -> {
                String assetipid = assetport.getAssetipid();
                String port = assetport.getPort();
                Assetip assetip = assetipService.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(assetipid);
                if (!Objects.isNull(assetip)) {
                    String ipaddressv4 = assetip.getIpaddressv4();
                    resultAssetportList.add(ipaddressv4 + "," + port);
                }
            });
            //得到未知服务的ip -port
            sliceIPList = "sliceIPList_" + targetId;
            Map<String, Set<String>> ipAndPortRTempMap = new LinkedHashMap<>();
            BlockingQueue<String> ipAndPortQueue = new LinkedBlockingQueue<>();
            Map<String, Set<String>> ipAndPortResultMap = TijiHelper.ipAndPortList2Map(resultAssetportList);
            TijiHelper.iPWithSamePorts2OneGroup(ipAndPortRTempMap, ipAndPortResultMap, ipAndPortQueue);

            for (Map.Entry<String, Set<String>> entry : ipAndPortRTempMap.entrySet()) {
                String ips = StringUtils.join(entry.getValue(), ",");
                if (ips != null) {
                    ipAndPortQueue.put(ips.replaceAll(",", " ") + " -p" + entry.getKey());
                }
            }
            for (String ipAndPort : ipAndPortQueue) {
                redisTemplate.opsForList().leftPush(sliceIPList, ipAndPort);
            }

            //设置当前分组大小
            redisTemplate.opsForValue().set("sliceIPListSize_" + targetId, String.valueOf(redisTemplate.opsForList().size(sliceIPList)));
            Map<String, String> taskConfig = new HashMap<>();
            taskConfig.put("status", "start");
            taskConfig.put("taskId", targetId);
            taskConfig.put("workType", task.getWorktype());
            taskConfig.put("sliceIPList", sliceIPList);
            taskConfig.put("threadNumber", task.getThreadnumber());
            taskConfig.put("singleIpScanTime", task.getSingleipscantime());
            taskConfig.put("additionOption", task.getAdditionoption());

            task.setStarttime(new Date());
            taskService.update(task);

            //分组数存入redis
            double sliceIPListSize = redisTemplate.opsForList().size(sliceIPList);
            //向上取整，确保所有的agent取出的数量大于等于总数
            double maxSliceSize = Math.ceil(sliceIPListSize / agentCount);

            taskConfig.put("maxSliceSize", String.valueOf(maxSliceSize));

            rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
            return sliceIPList;
        } else if (task.getAdditionoption().contains("-sn")) {
            for (String targetIp : targetip.split(",")) {
                redisTemplate.opsForList().leftPush(sliceIPList, targetIp);
            }
            return sliceIPList;
        } else {
            TijiHelper.target2Redis(task.getTargetip(), redisTemplate, rawIPSet);
        }

        //去除excludeIP中redis目标key
        String srcKey;
        //保存数据库中的ipv4
        List<String> dbIpv4List = new LinkedList<>();
        boolean dBIPAsExcludeIP = false;
        if (task.getDbipisexcludeip().equals(true)) {
            //获取所有ip
            dbIpv4List = assetipService.findAllDistinctIpaddressv4ListAndPassivetimeIsNull();
            dBIPAsExcludeIP = true;
        }
        long temp = 0;
        //已知资产IP作为白名单
        if (dBIPAsExcludeIP && dbIpv4List.size() != 0) {
            //存入excludeIPSet中
            for (String ip : dbIpv4List) {
                redisTemplate.opsForSet().add(excludeIPSet, ip);
            }
            //for test
            temp = redisTemplate.opsForSet().size(excludeIPSet);
        }
        String excludeip = task.getExcludeip();
        //task.getExcludeip()不为空，Excludeip有IP白名单
        if (!Objects.isNull(excludeip)) {
            //ExcludeIP存入redis Set, excludeIPSet
            TijiHelper.target2Redis(excludeip, redisTemplate, excludeIPSet);
            //去除rawIPSet中的excludeIPSet，并保存到targetIPSet
            redisTemplate.opsForSet().differenceAndStore(rawIPSet, excludeIPSet, targetIPSet);
            srcKey = targetIPSet;
        } else {//没有需要排除的IP，rawIP就是targetIP
            srcKey = rawIPSet;
        }

        //除sliceIPList外，其他设置10秒过期
        //sliceIPList会被pop掉
        redisTemplate.expire(excludeIPSet, 15, TimeUnit.SECONDS);
        redisTemplate.expire(rawIPSet, 15, TimeUnit.SECONDS);
        redisTemplate.expire(targetIPSet, 15, TimeUnit.SECONDS);

        //将targetIPSet分组，存入sliceIPList


        if (redisTemplate.opsForSet().size(srcKey) != 0) {
            TijiHelper.targetIPSet2SliceIPSet(task.getWorktype(), task.getTargetport(), sliceIPList, srcKey, redisTemplate, task.getIpslicesize(), task.getPortslicesize());
            //for test
            System.out.println("总IP：" + redisTemplate.opsForSet().size(rawIPSet));
            if (dBIPAsExcludeIP) {
                System.out.println("exclude.ip排除IP：" + "DB中assetIp");
            } else {
                System.out.println("exclude.ip排除IP：" + redisTemplate.opsForSet().members(excludeIPSet));
            }

            System.out.println("DB中排除IP数：" + temp);
            System.out.println("剩余目标IP：" + redisTemplate.opsForSet().size(srcKey));

            taskInfo.put("总IP数", redisTemplate.opsForSet().size(rawIPSet).toString());
            if (dBIPAsExcludeIP) {
                taskInfo.put("排除IP", "DB中assetIp");
            } else {
                taskInfo.put("排除IP", redisTemplate.opsForSet().members(excludeIPSet).toString());
            }
            taskInfo.put("DB中排除IP数", String.valueOf(temp));
            taskInfo.put("剩余目标IP数", redisTemplate.opsForSet().size(srcKey).toString());
            return sliceIPList;
        }
        return null;
    }
}

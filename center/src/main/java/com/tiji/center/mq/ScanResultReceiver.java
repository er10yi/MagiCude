package com.tiji.center.mq;

import com.tiji.center.pojo.Agent;
import com.tiji.center.pojo.Nmapconfig;
import com.tiji.center.pojo.Task;
import com.tiji.center.service.*;
import com.tiji.center.util.TijiHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import util.ExcpUtil;
import util.IdWorker;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author 贰拾壹
 * @create 2019-07-03 15:30
 */

@Component
@RabbitListener(queues = "scanresult")
public class ScanResultReceiver {

    private final static Logger logger = LoggerFactory.getLogger(ScanResultReceiver.class);
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskipService taskipService;
    @Autowired
    private TaskportService taskportService;
    @Autowired
    private AssetipService assetipService;
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PluginconfigService pluginconfigService;
    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private CheckresultVulnService checkresultVulnService;
    @Autowired
    private VulnpluginconfigService vulnpluginconfigService;
    @Autowired
    private VulnkeywordService vulnkeywordService;
    @Autowired
    private WebinfoService webinfoService;
    @Autowired
    private TitlewhitelistService titlewhitelistService;
    @Autowired
    private DomainwhitelistService domainwhitelistService;
    @Autowired
    private UrlService urlService;
    @Autowired
    private NmapconfigService nmapconfigService;
    @Autowired
    private ImvulnnotifyService imvulnnotifyService;
    @Autowired
    private AgentService agentService;

    @RabbitHandler
    public void getMessage(Map<String, String> resultMap) {
        try {
            if (!Objects.isNull(resultMap) && !resultMap.isEmpty()) {

                String workType = resultMap.get("workType");
                String taskId = resultMap.get("taskId");
                String scanResult = resultMap.get("scanResult");


                //用于清除redis缓存、runningTaskMap已完成任务
                //监控当前任务信息
                //总任务数
                String totalTaskListName = "totalTaskList_" + taskId;
                //已完成任务数
                String accomplishTaskListName = "accomplishTaskList_" + taskId;
                //正在进行的，没有操作到这个..
                String workingTaskListName = "workingTaskList_" + taskId;
                //分组大小
                String sliceIPListSizeName = "sliceIPListSize_" + taskId;
                //任务状态标志
                String taskStatusName = "taskStatus_" + taskId;

                String task_status = redisTemplate.opsForValue().get(taskStatusName);

                long totalTaskListSize = redisTemplate.opsForList().size(totalTaskListName);
                long accomplishTaskListSize = redisTemplate.opsForList().size(accomplishTaskListName);
                long workingTaskListSize = redisTemplate.opsForList().size(workingTaskListName);
                //计算working的任务
                //working = total - accomplish
                //redisTemplate.opsForSet().differenceAndStore(totalTaskListName, accomplishTaskListName, workingTaskListName);


                List<String> totalTaskList = redisTemplate.opsForList().range(totalTaskListName, 0, -1);
                //System.out.println("totalTaskList: " + totalTaskList);
                List<String> accomplishTaskList = redisTemplate.opsForList().range(accomplishTaskListName, 0, -1);
                totalTaskList.removeAll(accomplishTaskList);
                if (!totalTaskList.isEmpty()) {
                    System.out.println("working: " + totalTaskList);
                }

                boolean secondTaskFlag = false;

                //进度监控
                if (redisTemplate.hasKey(sliceIPListSizeName)) {
                    long sliceIPListSize = Long.parseLong(redisTemplate.opsForValue().get(sliceIPListSizeName));
                    double taskPercentStatus = (double) accomplishTaskListSize / sliceIPListSize;
                    //System.out.println("sliceIPListSize:" + sliceIPListSize);
                    System.out.printf("task %s progress %f\n", taskId, taskPercentStatus);
                    //if (accomplishTaskListSize == totalTaskListSize && accomplishTaskListSize != 0 && workingTaskListSize == 0) {
                    //accomplishTaskListSize等于总分组数sliceIPList，任务结束
                    if (accomplishTaskListSize == sliceIPListSize) {
                        System.out.printf("task %s finished\n", taskId);
                        if (!workType.equals("mass2Nmap")) {
                            //记录任务结束时间
                            Task task = taskService.findById(taskId);
                            task.setEndtime(new Date());
                            taskService.update(task);
                        }
//                        redisTemplate.delete("ip_" + taskId);
//                        redisTemplate.delete("port_" + taskId);
//                        redisTemplate.delete("cmd_" + taskId);
//                        redisTemplate.delete("nginxRawVersion_"+taskId + taskId);
//                        redisTemplate.delete("timeout_" + taskId);
                        redisTemplate.delete(totalTaskListName);
                        //redisTemplate.delete(accomplishTaskListName);
                        if (workType.equals("mass2Nmap")) {
                            redisTemplate.delete(accomplishTaskListName);
                        } else {
                            redisTemplate.expire(accomplishTaskListName, 15, TimeUnit.SECONDS);
                        }
                        //redisTemplate.delete(workingTaskListName);
                        redisTemplate.delete(sliceIPListSizeName);

                        //如果任务结束，给agent发结束信息，runningTaskMap删除已完成任务
                        //redisTemplate.delete(accomplishTaskListName);
                        if (!workType.equals("mass2Nmap")) {
                            Map<String, String> taskConfig = new HashMap<>();
                            taskConfig.put("status", "removeAccomplishTask");
                            taskConfig.put("taskId", taskId);
                            rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
                        }
                        if (workType.equals("mass2Nmap")) {
                            secondTaskFlag = true;
                        }
                    }
                }


                //mass2Nmap模式下，当前任务被终止
                if (!Objects.isNull(task_status) && task_status.equals("exit")) {
                    secondTaskFlag = false;
                    //remove flag
                    //redisTemplate.delete(taskStatusName);
                    //mass2Nmap下，删除退出的进程
                    //好像agent可以删掉....
                    //redisTemplate.expire(accomplishTaskListName, 15, TimeUnit.SECONDS);

                    redisTemplate.expire(taskStatusName, 15, TimeUnit.SECONDS);
                }
                if (!Objects.isNull(scanResult) && Objects.isNull(task_status)) {
                    switch (workType) {
                        case "mass": {
                            Map<String, Set<String>> massResultMap = TijiHelper.ipAndPortList2Map(scanResult);
                            //mysql入库
                            TijiHelper.massScanResult2DB(massResultMap, taskipService, taskportService, idWorker, taskId);
                            //任务结果，合并到资产库
                            if (taskService.findById(taskId).getMerge2asset()) {
                                TijiHelper.massScanResult2AssetDB(assetipService, assetportService, idWorker, massResultMap);
                            }
                            break;
                        }
                        case "nmap": {
                            Map<String, Set<String>> nmapResultMap = TijiHelper.nmapResult2Map(scanResult);
                            //mysql入库
                            TijiHelper.nmapScanResult2DB(nmapResultMap, taskipService, taskportService, idWorker, taskId);
                            //任务结果，合并到资产库
                            if (taskService.findById(taskId).getMerge2asset()) {
                                TijiHelper.nmapScanResult2AssetDB(assetipService, assetportService, idWorker, nmapResultMap);
                            }
                            break;
                        }
                        case "mass2Nmap":
                            //mass
                            //第一次用mass扫
                            Map<String, Set<String>> massResultMap1 = TijiHelper.ipAndPortList2Map(scanResult);
                            //mysql入库
                            TijiHelper.massScanResult2DB(massResultMap1, taskipService, taskportService, idWorker, taskId);
                            //任务结果，合并到资产库
                            if (taskService.findById(taskId).getMerge2asset()) {
                                TijiHelper.massScanResult2AssetDB(assetipService, assetportService, idWorker, massResultMap1);
                            }
                            //当前任务已经结束或未被终止，发送第二次任务
                            if (secondTaskFlag) {
                                List<Agent> onlineAgentList = agentService.findAllByOnline(true);
                                int agentCount;
                                if (onlineAgentList.isEmpty()) {
                                    break;
                                } else {
                                    agentCount = onlineAgentList.size();
                                }
                                Map<String, String> taskConfig = new HashMap<>();
                                String sliceIPList = "sliceIPList_" + taskId;
                                //taskip数据库获取当前任务的ip和端口，即masscan扫描之后的ip和端口
                                List<String> ipAndPortList = taskipService.findTaskIpAndPort(taskId);

                                //System.out.println("ipAndPortList:" + ipAndPortList.size());
                                //ipAndPortList.forEach(System.out::println);
                                if (ipAndPortList.isEmpty()) {
                                    //没有扫到端口，结束任务
                                    Task task = taskService.findById(taskId);
                                    redisTemplate.delete(totalTaskListName);
                                    redisTemplate.delete(sliceIPListSizeName);
                                    task.setEndtime(new Date());

                                    taskConfig.put("status", "removeAccomplishTask");
                                    taskConfig.put("taskId", taskId);
                                    rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
                                    taskService.update(task);
                                    break;
                                }
                                Map<String, Set<String>> ipAndPortResultMap;
                                Map<String, Set<String>> ipAndPortRTempMap = new LinkedHashMap<>();
                                ipAndPortResultMap = TijiHelper.ipAndPortList2Map(ipAndPortList);
                                BlockingQueue<String> ipAndPortQueue = new LinkedBlockingQueue<>();
                                TijiHelper.iPWithSamePorts2OneGroup(ipAndPortRTempMap, ipAndPortResultMap, ipAndPortQueue);
                                for (Map.Entry<String, Set<String>> entry : ipAndPortRTempMap.entrySet()) {
                                    String ips = StringUtils.join(entry.getValue(), ",");
                                    ipAndPortQueue.put(ips.replaceAll(",", " ") + " -p" + entry.getKey());
                                }
                                for (String ipAndPort : ipAndPortQueue) {
                                    redisTemplate.opsForList().leftPush(sliceIPList, ipAndPort);
                                }

                                //设置当前分组大小
                                redisTemplate.opsForValue().set("sliceIPListSize_" + taskId, String.valueOf(redisTemplate.opsForList().size(sliceIPList)));

                                taskConfig.put("status", "start");
                                taskConfig.put("taskId", taskId);
                                taskConfig.put("workType", "nmap");
                                taskConfig.put("sliceIPList", sliceIPList);


                                Task task = taskService.findById(taskId);
                                String taskParentId = task.getTaskparentid();
                                String nmapConfigId = taskId;
                                if (!Objects.isNull(taskParentId) && !taskParentId.isEmpty()) {
                                    nmapConfigId = taskParentId;
                                }
                                Nmapconfig nmapconfig = nmapconfigService.findByTaskid(nmapConfigId);
                                taskConfig.put("threadNumber", nmapconfig.getThreadnumber());
                                taskConfig.put("singleIpScanTime", nmapconfig.getSingleipscantime());
                                taskConfig.put("additionOption", nmapconfig.getAdditionoption());

                                //分组数存入redis
                                double sliceIPListSize = redisTemplate.opsForList().size(sliceIPList);
                                //向上取整，确保所有的agent取出的数量大于等于总数
                                double maxSliceSize = Math.ceil(sliceIPListSize / agentCount);
                                taskConfig.put("maxSliceSize", String.valueOf(maxSliceSize));

                                //任务丢给MQ
                                rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);

                            }
                            break;
                        case "nse":
                            if (scanResult.contains("-p") && scanResult.contains("Starting")) {
                                TijiHelper.nseResultParser(assetipService, assetportService, idWorker, pluginconfigService, checkresultService, vulnkeywordService, checkresultVulnService, vulnpluginconfigService, imvulnnotifyService, redisTemplate, rabbitMessagingTemplate, scanResult);
                            }
                            break;
                        case "selfd":
                            TijiHelper.selfdResultParser(assetipService, assetportService, idWorker, pluginconfigService, checkresultService, vulnkeywordService, checkresultVulnService, vulnpluginconfigService, imvulnnotifyService, redisTemplate, rabbitMessagingTemplate, resultMap, scanResult);
                            break;
                        case "httpp":
                            TijiHelper.httppResult2Db(webinfoService, urlService, idWorker, titlewhitelistService, domainwhitelistService, resultMap, scanResult);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("ScanResultReceiver Exception here: " + ExcpUtil.buildErrorMessage(e));
        }

    }
}
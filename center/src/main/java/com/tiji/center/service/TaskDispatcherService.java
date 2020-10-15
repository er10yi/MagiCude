package com.tiji.center.service;

import com.tiji.center.dispatcher.TargetIpSlicer;
import com.tiji.center.pojo.*;
import com.tiji.center.schedule.quartz.QuartzJobService;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import util.IdWorker;

import java.util.*;

/**
 * @author 贰拾壹
 * @create 2019-09-18 10:53
 */
@Service
public class TaskDispatcherService {
    @Autowired
    private TaskService taskService;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private UseragentService useragentService;

    @Autowired
    private AssetipService assetipService;
    @Autowired
    private AssetportService assetportService;

    @Autowired
    private PluginconfigService pluginconfigService;

    @Autowired
    private PluginassetserviceService pluginassetserviceService;
    @Autowired
    private PluginassetversionService pluginassetversionService;

    @Autowired
    private TaskipService taskipService;
    @Autowired
    private QuartzJobService quartzJobService;

    @Autowired
    private AgentService agentService;
    /**
     * 根据taskId对资产库执行check任务
     * synchronized 防止定时任务同时启动时丢任务
     *
     * @param oldTaskId
     * @return
     */
    @Autowired
    private TaskpluginconfigService taskpluginconfigService;
    /**
     * 根据taskId重新开始任务
     *
     * @param taskId
     * @return
     */
    @Autowired
    private NmapconfigService nmapconfigService;

    /**
     * 根据taskId执行任务
     * synchronized 防止定时任务同时启动时丢任务
     *
     * @param taskId
     * @return
     */
    public synchronized Map<String, Object> executeWork(String taskId) throws InterruptedException {
        Map<String, Object> taskInfo = new LinkedHashMap<>();
        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        int agentCount;
        if (onlineAgentList.isEmpty()) {
            return null;
        } else {
            agentCount = onlineAgentList.size();
        }

        Task task = taskService.findById(taskId);
        //任务未执行过，执行新任务
        if (Objects.isNull(task.getStarttime()) && Objects.isNull(task.getEndtime())) {
            Map<String, String> taskConfig;
            String taskWorkType = task.getWorktype();
            //mass、mass2Nmap、nmap
            if ("mass".equals(taskWorkType) || "mass2Nmap".equals(taskWorkType) || "nmap".equals(taskWorkType)) {
                //ip分组，进redis
                String sliceIPList = TargetIpSlicer.slice(taskId, taskInfo, assetipService, taskipService, taskService, assetportService, redisTemplate, rabbitMessagingTemplate, agentCount);
                if (!Objects.isNull(sliceIPList)) {
                    //分组数存入redis
                    double sliceIPListSize = redisTemplate.opsForList().size(sliceIPList);
                    //向上取整，确保所有的agent取出的数量大于等于总数
                    double maxSliceSize = Math.ceil(sliceIPListSize / agentCount);

                    redisTemplate.opsForValue().set("sliceIPListSize_" + taskId, String.valueOf(redisTemplate.opsForList().size(sliceIPList)));
                    taskConfig = pojoTask2TaskConfigMap(taskId, sliceIPList, task);
                    //记录任务开始时间
                    task.setStarttime(new Date());
                    taskService.update(task);
                    //任务丢给MQ
                    taskConfig.put("maxSliceSize", String.valueOf(maxSliceSize));
                    rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
                    //rabbitMessagingTemplate.convertAndSend(taskWorkType + "fanout", "", taskConfig);
                } else {
                    taskInfo.put("目标ip为空", "目标ip为空");
                }
            }
        } else {
            //已执行过，重复任务
            taskInfo = repeat(taskId);
        }

        return taskInfo;
    }

    /**
     * work任务结束，发送check任务
     * 根据taskId执行check任务
     * synchronized 防止定时任务同时启动时丢任务
     *
     * @param taskId
     * @return
     */
    public synchronized Map<String, Object> executeCheck(String taskId) {
        Map<String, Object> taskInfo = new LinkedHashMap<>();

        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        int agentCount;
        if (onlineAgentList.isEmpty()) {
            return null;
        } else {
            agentCount = onlineAgentList.size();
        }

        Task task = taskService.findById(taskId);
        String taskWorkType = task.getWorktype();
        //结束时间不为空，任务已完成
        if (("mass2Nmap".equals(taskWorkType) || "nmap".equals(taskWorkType)) && (!Objects.isNull(task.getStarttime()) && !Objects.isNull(task.getEndtime()))) {
            //进行check任务
            // nse,selfd,httpp
            String taskCheckType = task.getChecktype();
            Set<String> nseSet = new TreeSet<>();
            Set<String> selfdSet = new TreeSet<>();
            if (taskCheckType.contains("nse") || taskCheckType.contains("selfd")) {
                //获取所有插件
                //根据插件的service确定ip和端口
                List<Pluginassetservice> pluginAssetServiceList;
                List<Taskpluginconfig> taskidInTaskpluginConfig = taskpluginconfigService.findAllByTaskid(taskId);
                if (taskidInTaskpluginConfig.isEmpty()) {
                    pluginAssetServiceList = pluginassetserviceService.findAll();
                } else {
                    pluginAssetServiceList = pluginassetserviceService.findPluginassetserviceByTaskid(taskId);
                }
                //List<Pluginassetservice> pluginAssetServiceList = pluginassetserviceService.findAll();
                if (!pluginAssetServiceList.isEmpty()) {
                    pluginAssetServiceList.forEach(pluginassetservice -> {
                        String pluginConfigId = pluginassetservice.getPluginconfigid();
                        //根据插件的service确定插件
                        String serviceName = pluginassetservice.getAssetservice();
                        //根据service获取开放端口列表
                        plugin2NseAndSelfdSet(nseSet, selfdSet, pluginconfigService, pluginConfigId, taskId, "service", serviceName);
                    });
                }
                //根据插件的version确定ip和端口
                List<Pluginassetversion> pluginAssetVersionList;
                taskidInTaskpluginConfig = taskpluginconfigService.findAllByTaskid(taskId);
                if (taskidInTaskpluginConfig.isEmpty()) {
                    pluginAssetVersionList = pluginassetversionService.findAll();
                } else {
                    pluginAssetVersionList = pluginassetversionService.findPluginassetversionByTaskid(taskId);
                }


                //List<Pluginassetversion> pluginAssetVersionList = pluginassetversionService.findAll();
                if (!pluginAssetVersionList.isEmpty()) {
                    pluginAssetVersionList.forEach(pluginassetversion -> {
                        String pluginConfigId = pluginassetversion.getPluginconfigid();
                        String versionName = pluginassetversion.getAssetversion();
                        //根据version获取开放端口列表
                        plugin2NseAndSelfdSet(nseSet, selfdSet, pluginconfigService, pluginConfigId, taskId, "version", versionName);
                    });
                }
            }

            //有nse
            if (taskCheckType.contains("nse") && !nseSet.isEmpty()) {
                sendNewTask(task, "nse", redisTemplate, taskService, nseSet, agentCount);
            }
            //有selfd
            if (taskCheckType.contains("selfd") && !selfdSet.isEmpty()) {
                sendNewTask(task, "selfd", redisTemplate, taskService, selfdSet, agentCount);
            }
            //有httpp
            if (taskCheckType.contains("httpp")) {
                Set<String> httpLikeOrUnsureServiceSet = getTaskPortHttpLikeOrUnsureService(taskId, taskipService);
                if (httpLikeOrUnsureServiceSet.isEmpty())
                    return null;
                sendNewTask(task, "httpp", redisTemplate, taskService, httpLikeOrUnsureServiceSet, agentCount);
            }
            taskInfo.put(taskWorkType, "成功开始");
            return taskInfo;
        }
        return null;
    }

    public void plugin2NseAndSelfdSet(Set<String> nseSet, Set<String> selfdSet, PluginconfigService pluginconfigService, String pluginConfigId, String taskId, String searchKey, String searchValue) {
        List<String> ipAndPortList;
        if ("service".equals(searchKey)) {
            ipAndPortList = taskipService.findByTaskidAndServiceLikeAndCheckwhitelistIsFalse(taskId, searchValue);
        } else {
            ipAndPortList = taskipService.findByTaskidAndVersionLikeAndCheckwhitelistIsFalse(taskId, searchValue);
        }

        Pluginconfig pluginconfig = pluginconfigService.findById(pluginConfigId);
        String pluginConfigName = pluginconfig.getName();
        String pluginConfigType = pluginconfig.getType();
        String pluginConfigArgs = pluginconfig.getArgs();
        String pluginConfigTimeout = pluginconfig.getTimeout();
        String pluginConfigPlugincode = pluginconfig.getPlugincode();
        String pluginConfigValidateType = pluginconfig.getValidatetype();

        if (!ipAndPortList.isEmpty()) {
            ipAndPortList.forEach(ipAndPort -> {
                String ipv4 = ipAndPort.split(",")[0];
                String taskPortName = ipAndPort.split(",")[1];
                if ("selfd".equals(pluginConfigType)) {
                    //貌似service没啥用
                    String taskServiceName = ipAndPort.split(",")[2];
                    String taskVersionName = ipAndPort.split(",")[3];

                    //ip|port|pluginName|args|timeout|service|version
                    selfdSet.add(ipv4 + "<=-=>" + taskPortName + "<=-=>" + pluginConfigName + "<=-=>" + pluginConfigArgs + "<=-=>" + pluginConfigTimeout + "<=-=>" + pluginConfigPlugincode + "<=-=>" + pluginConfigValidateType + "<=-=>" + taskServiceName + "<=-=>" + taskVersionName);
                }
                //nse插件
                if ("nse".equals(pluginConfigType)) {
                    //192.168.1.1 -p21 --script ftp-anon --script-args ftp-anon.maxlist=0
                    if (Objects.isNull(pluginConfigArgs) || pluginConfigArgs.isEmpty()) {
                        nseSet.add(ipv4 + " -p" + taskPortName + " --script " + pluginConfigName);
                    } else {
                        nseSet.add(ipv4 + " -p" + taskPortName + " --script " + pluginConfigName + " --script-args " + pluginConfigArgs);
                    }
                }
            });
        }
    }

    public void sendNewTask(Task task, String workType, RedisTemplate<String, String> redisTemplate, TaskService taskService, Collection<String> target, int agentCount) {
        Task newTask = getNewTask(task, workType);
        taskService.add(newTask);
        String newTaskId = newTask.getId();
        String sliceIPListName = workType + "SliceIPList_" + newTaskId;
        target.forEach(singleCommand -> {
            redisTemplate.opsForList().leftPush(sliceIPListName, singleCommand);
        });
        //设置当前分组大小
        double sliceIPListSize = redisTemplate.opsForList().size(sliceIPListName);
        redisTemplate.opsForValue().set("sliceIPListSize_" + newTaskId, String.valueOf(redisTemplate.opsForList().size(sliceIPListName)));
        Map<String, String> taskConfig = pojoTask2TaskConfigMap(newTaskId, sliceIPListName, newTask);
        //记录任务开始时间
        newTask.setStarttime(new Date());
        taskService.update(newTask);
        //任务丢给MQ

        //向上取整，确保所有的agent取出的数量大于等于总数
        double maxSliceSize = Math.ceil(sliceIPListSize / agentCount);

        taskConfig.put("maxSliceSize", String.valueOf(maxSliceSize));
        rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
        //rabbitMessagingTemplate.convertAndSend(workType + "fanout", "", taskConfig);
    }

    public Task getNewTask(Task oldTask, String workType) {
        String newTaskId = idWorker.nextId() + "";
        Task newTask = new Task(newTaskId, oldTask.getId(), oldTask.getProjectid(), oldTask.getName(), oldTask.getDescription(), oldTask.getCronexpression(), oldTask.getCrontask(), null, null,
                workType, workType, oldTask.getThreadnumber(), oldTask.getSingleipscantime(), oldTask.getAdditionoption(), oldTask.getRate(), oldTask.getTargetip(),
                oldTask.getTargetport(), oldTask.getExcludeip(), oldTask.getIpslicesize(), oldTask.getPortslicesize(), oldTask.getDbipisexcludeip(), oldTask.getMerge2asset());
        newTask.setId(newTaskId);
        //taskplugin复制一份旧的插件数据到新的task
        if ("nse".equals(oldTask.getWorktype()) || "selfd".equals(oldTask.getWorktype())) {
            List<Taskpluginconfig> taskpluginconfigList = new ArrayList<>();
            List<Taskpluginconfig> allByTaskid = taskpluginconfigService.findAllByTaskid(oldTask.getId());
            allByTaskid.forEach(taskpluginconfig -> {
                taskpluginconfigList.add(new Taskpluginconfig(idWorker.nextId() + "", newTaskId, taskpluginconfig.getPluginconfigid()));
            });
            taskpluginconfigService.batchAdd(taskpluginconfigList);
        }

        return newTask;
    }

    //获取所有asset service的ip和端口：like http(http、https、httpx)、为空、null、tcpwrapped、unknown、包含?
    private Set<String> getAssetPortHttpLikeOrUnsureService(AssetipService assetipService, AssetportService assetportService) {
        List<Assetport> assetportList = new ArrayList<>();

        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("state", "open");
        searchMap.put("checkwhitelist", false);
        searchMap.put("downtime", null);

        searchMap.put("service", "http");
        assetportList.addAll(assetportService.findSearch(searchMap));
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

        Set<String> assetportSet = new HashSet<>();
        assetportList.parallelStream().forEach(assetport -> {
            String assetipid = assetport.getAssetipid();
            String port = assetport.getPort();
            String service = assetport.getService();
            //Assetip assetip = assetipService.findById(assetipid);
            Assetip assetip = assetipService.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(assetipid);
            if (!Objects.isNull(assetip)) {
                String ipaddressv4 = assetip.getIpaddressv4();
                String assetportId = assetport.getId();
                if (!Objects.isNull(service)) {
                    if (service.contains("https") || service.contains("ssl/http")) {
                        //https or https?
                        assetportSet.add(assetportId + "|" + "https://" + ipaddressv4 + ":" + port);
                    } else {
                        //others
                        assetportSet.add(assetportId + "|" + "http://" + ipaddressv4 + ":" + port);
                    }
                } else {
                    //service 为null
                    assetportSet.add(assetportId + "|" + "http://" + ipaddressv4 + ":" + port);
                }
            }
        });
        return assetportSet;
    }

    //获取所有task service的ip和端口：like http(http、https、httpx)、为空、null、tcpwrapped、unknown、包含?
    private Set<String> getTaskPortHttpLikeOrUnsureService(String taskId, TaskipService taskipService) {
        List<String> taskIdAndIpAndPortList = taskipService.findAllByServiceLikeAndCheckwhitelistIsFalse(taskId, "http");
        taskIdAndIpAndPortList.addAll(taskipService.findAllByServiceLikeAndCheckwhitelistIsFalse(taskId, "tcpwrapped"));
        taskIdAndIpAndPortList.addAll(taskipService.findAllByServiceLikeAndCheckwhitelistIsFalse(taskId, "unknown"));
        taskIdAndIpAndPortList.addAll(taskipService.findAllByServiceLikeAndCheckwhitelistIsFalse(taskId, "?"));
        taskIdAndIpAndPortList.addAll(taskipService.findAllByServiceLikeAndCheckwhitelistIsFalse(taskId, "null"));
        //为空的service
        taskIdAndIpAndPortList.addAll(taskipService.findByTaskidAndServiceIsNullAndCheckwhitelistIsFalse(taskId));

        Set<String> taskPortSet = new HashSet<>();
        taskIdAndIpAndPortList.parallelStream().forEach(taskIdAndIpAndPort -> {
            String taskid = taskIdAndIpAndPort.split(",")[0];
            String taskip = taskIdAndIpAndPort.split(",")[1];
            String port = taskIdAndIpAndPort.split(",")[2];
            String service = taskIdAndIpAndPort.split(",")[3];
            //String taskPortId = taskPort.getId();
            //如果是taskPortId，则记录进webinfo时，端口的id是task的
            //改成assetPortId
            //TODO 后续新增task的webinfo、checkresult之类的表？貌似没必要
            Assetip assetip = assetipService.findByIpaddressv4AndPassivetimeIsNull(taskip);
            Assetport assetport = assetportService.findByAssetipidAndPortAndDowntimeIsNull(assetip.getId(), port);
            if (!Objects.isNull(assetport)) {
                String assetPortId = assetport.getId();
                if (!Objects.isNull(service)) {
                    if (service.contains("https") || service.contains("ssl/http")) {
                        //https or https?
                        taskPortSet.add(assetPortId + "|" + "https://" + taskip + ":" + port);
                    } else {
                        //others
                        taskPortSet.add(assetPortId + "|" + "http://" + taskip + ":" + port);
                    }
                } else {
                    taskPortSet.add(assetPortId + "|" + "http://" + taskip + ":" + port);
                }
            }
        });
        return taskPortSet;
    }

    public Map<String, String> pojoTask2TaskConfigMap(String taskId, String sliceIPList, Task task) {
        String taskWorkType = task.getWorktype();
        Map<String, String> taskConfig = new LinkedHashMap<>();
        taskConfig.put("status", "start");
        taskConfig.put("taskId", taskId);
        taskConfig.put("workType", taskWorkType);
        taskConfig.put("threadNumber", task.getThreadnumber());
        taskConfig.put("sliceIPList", sliceIPList);
        taskConfig.put("singleIpScanTime", task.getSingleipscantime());
        taskConfig.put("additionOption", task.getAdditionoption());
        if ("mass".equals(taskWorkType) || "nmap".equals(taskWorkType) || "mass2Nmap".equals(taskWorkType)) {
            taskConfig.put("targetPort", task.getTargetport());
            taskConfig.put("rate", task.getRate());
        }
//        if (taskWorkType.equals("httpp")) {
//            String userAgent;
//            //每次任务随机UA
//            List<String> userAgentList = useragentService.findAllDistinctUserAgentList();
//            if (userAgentList.isEmpty()) {
//                userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";
//            } else {
//                Collections.shuffle(userAgentList);
//                userAgent = userAgentList.get(0);
//            }
//            taskConfig.put("userAgent", userAgent);
//        }
        return taskConfig;
    }

    public synchronized void executeTotalCheck(String oldTaskId) {
        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        int agentCount;
        if (onlineAgentList.isEmpty()) {
            return;
        } else {
            agentCount = onlineAgentList.size();
        }
        double sliceIPListSize;
        Map<String, String> taskConfig;
        Task oldTask = taskService.findById(oldTaskId);
        String taskWorkType = oldTask.getWorktype();
        Task task;
        String taskId;
        if (Objects.isNull(oldTask.getStarttime())) {
            task = oldTask;
            taskId = oldTaskId;
        } else {
            task = getNewTask(oldTask, taskWorkType);
            task.setCrontask(false);
            task.setChecktype(null);
            taskId = task.getId();
        }
        switch (taskWorkType) {
            case "nse":
                //对资产库所有ip进行nse
                Set<String> nseSet = new TreeSet<>();
                //获取所有插件
                //根据插件的service确定插件
                List<Pluginassetservice> assetserviceList;

                //taskId 不在taskpluginconfig中，则没有配置插件，启用所有插件
                List<Taskpluginconfig> taskidInTaskpluginConfig = taskpluginconfigService.findAllByTaskid(taskId);
                if (taskidInTaskpluginConfig.isEmpty()) {
                    assetserviceList = pluginassetserviceService.findAll();
                } else {
                    assetserviceList = pluginassetserviceService.findPluginassetserviceByTaskid(taskId);
                }

                //List<Pluginassetservice> assetserviceList = pluginassetserviceService.findAll();
                if (!assetserviceList.isEmpty()) {
                    assetserviceList.forEach(assetservice -> {
                        String pluginconfigid = assetservice.getPluginconfigid();
                        Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                        String pluginconfigName = pluginconfig.getName();
                        String pluginconfigType = pluginconfig.getType();
                        String pluginconfigArgs = pluginconfig.getArgs();
                        String serviceName = assetservice.getAssetservice();
                        //根据service获取未下线且安全检测白名单为false的端口列表
                        Map<String, Object> searchMap = new HashMap<>();
                        searchMap.put("service", serviceName);
                        searchMap.put("state", "open");
                        searchMap.put("checkwhitelist", false);
                        searchMap.put("downtime", null);
                        List<Assetport> assetportList = assetportService.findSearch(searchMap);
                        //List<Assetport> assetportList = assetportService.findByServiceLikeAndDowntimeIsNullAndWhitelistIsFalse(serviceName);
                        if (!assetportList.isEmpty()) {
                            assetportList.parallelStream().forEach(assetport -> {
                                String assetipid = assetport.getAssetipid();
                                //Assetip assetip = assetipService.findById(assetipid);
                                Assetip assetip = assetipService.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(assetipid);
                                if (!Objects.isNull(assetip)) {
                                    String ipv4 = assetip.getIpaddressv4();
                                    String assetPortName = assetport.getPort();
                                    //nse插件
                                    if ("nse".equals(pluginconfigType)) {
                                        //192.168.1.1 -p21 --script ftp-anon --script-args ftp-anon.maxlist=0
                                        if (Objects.isNull(pluginconfigArgs) || pluginconfigArgs.isEmpty()) {
                                            nseSet.add(ipv4 + " -p" + assetPortName + " --script " + pluginconfigName);
                                        } else {
                                            nseSet.add(ipv4 + " -p" + assetPortName + " --script " + pluginconfigName + " --script-args " + pluginconfigArgs);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
                List<Pluginassetversion> assetversionList;

                taskidInTaskpluginConfig = taskpluginconfigService.findAllByTaskid(taskId);
                if (taskidInTaskpluginConfig.isEmpty()) {
                    assetversionList = pluginassetversionService.findAll();
                } else {
                    assetversionList = pluginassetversionService.findPluginassetversionByTaskid(taskId);
                }

                //根据插件的version确定ip和端口
                //List<Pluginassetversion> assetversionList = pluginassetversionService.findAll();
                if (!assetversionList.isEmpty()) {
                    assetversionList.forEach(assetversion -> {
                        String pluginconfigid = assetversion.getPluginconfigid();
                        Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                        String pluginconfigName = pluginconfig.getName();
                        String pluginconfigType = pluginconfig.getType();
                        String pluginconfigArgs = pluginconfig.getArgs();
                        String versionName = assetversion.getAssetversion();
                        //根据version获取未下线端口列表
                        Map<String, Object> searchMap = new HashMap<>();
                        searchMap.put("version", versionName);
                        searchMap.put("state", "open");
                        searchMap.put("checkwhitelist", false);
                        searchMap.put("downtime", null);
                        List<Assetport> assetportList = assetportService.findSearch(searchMap);
                        //List<Assetport> assetportList = assetportService.findByVersionLikeAndDowntimeIsNullAndWhitelistIsFalse(versionName);
                        if (!assetportList.isEmpty()) {
                            assetportList.parallelStream().forEach(assetport -> {
                                String assetipid = assetport.getAssetipid();
                                //Assetip assetip = assetipService.findById(assetipid);
                                Assetip assetip = assetipService.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(assetipid);
                                if (!Objects.isNull(assetip)) {
                                    String ipv4 = assetip.getIpaddressv4();
                                    String assetPortName = assetport.getPort();
                                    //nse插件
                                    if ("nse".equals(pluginconfigType)) {
                                        //192.168.1.1 -p21 --script ftp-anon --script-args ftp-anon.maxlist=0
                                        if (Objects.isNull(pluginconfigArgs) || pluginconfigArgs.isEmpty()) {
                                            nseSet.add(ipv4 + " -p" + assetPortName + " --script " + pluginconfigName);
                                        } else {
                                            nseSet.add(ipv4 + " -p" + assetPortName + " --script " + pluginconfigName + " --script-args " + pluginconfigArgs);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
                if (nseSet.isEmpty()) {
                    return;
                }
                //写到redis
                String nseSliceIPList = "nseSliceIPList_" + taskId;
                nseSet.forEach(nseCommand -> {
                    redisTemplate.opsForList().leftPush(nseSliceIPList, nseCommand);
                });
                //设置当前分组大小
                sliceIPListSize = redisTemplate.opsForList().size(nseSliceIPList);
                redisTemplate.opsForValue().set("sliceIPListSize_" + taskId, String.valueOf(redisTemplate.opsForList().size(nseSliceIPList)));
                taskConfig = pojoTask2TaskConfigMap(taskId, nseSliceIPList, task);
                break;
            case "selfd":
                //查询资产库中所有未下线的ip
                Set<String> selfdSet = new TreeSet<>();

                //taskId 不在taskpluginconfig中，则没有配置插件，启用所有插件
                taskidInTaskpluginConfig = taskpluginconfigService.findAllByTaskid(taskId);
                if (taskidInTaskpluginConfig.isEmpty()) {
                    assetserviceList = pluginassetserviceService.findAll();
                } else {
                    assetserviceList = pluginassetserviceService.findPluginassetserviceByTaskid(taskId);
                }
                //获取所有插件
                //根据插件的service确定插件
                //assetserviceList = pluginassetserviceService.findAll();
                if (!assetserviceList.isEmpty()) {
                    assetserviceList.forEach(assetservice -> {
                        String pluginconfigid = assetservice.getPluginconfigid();
                        Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                        String pluginconfigName = pluginconfig.getName();
                        String pluginconfigType = pluginconfig.getType();
                        String pluginconfigArgs = pluginconfig.getArgs();
                        String pluginconfigTimeout = pluginconfig.getTimeout();
                        String pluginconfigPlugincode = pluginconfig.getPlugincode();
                        String pluginConfigValidateType = pluginconfig.getValidatetype();

                        //应该是这里，如果插件的service为空，则启用所有插件
                        String serviceName = assetservice.getAssetservice();
                        //根据service获取未下线且安全检测白名单为false的端口列表
                        Map<String, Object> searchMap = new HashMap<>();
                        searchMap.put("service", serviceName);
                        searchMap.put("state", "open");
                        searchMap.put("checkwhitelist", false);
                        searchMap.put("downtime", null);
                        List<Assetport> assetportList = assetportService.findSearch(searchMap);
                        //List<Assetport> assetportList = assetportService.findByServiceLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(serviceName,"open");
                        if (!assetportList.isEmpty()) {
                            assetportList.parallelStream().forEach(assetport -> {
                                String assetipid = assetport.getAssetipid();
                                //Assetip assetip = assetipService.findById(assetipid);
                                Assetip assetip = assetipService.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(assetipid);
                                if (!Objects.isNull(assetip)) {
                                    if ("selfd".equals(pluginconfigType)) {
                                        //貌似service没啥用
                                        String ipv4 = assetip.getIpaddressv4();
                                        String assetPortName = assetport.getPort();
                                        String assetServiceName = assetport.getService();
                                        String assetVersionName = assetport.getVersion();

                                        //ip|port|pluginName|args|timeout|service|version
                                        selfdSet.add(ipv4 + "<=-=>" + assetPortName + "<=-=>" + pluginconfigName + "<=-=>" + pluginconfigArgs + "<=-=>" + pluginconfigTimeout + "<=-=>" + pluginconfigPlugincode + "<=-=>" + pluginConfigValidateType + "<=-=>" + assetServiceName + "<=-=>" + assetVersionName);

                                    }
                                }
                            });
                        }
                    });
                }

                if (taskidInTaskpluginConfig.isEmpty()) {
                    assetversionList = pluginassetversionService.findAll();
                } else {
                    assetversionList = pluginassetversionService.findPluginassetversionByTaskid(taskId);
                }

                //根据插件的version确定ip和端口
                //assetversionList = pluginassetversionService.findAll();
                if (!assetversionList.isEmpty()) {
                    assetversionList.forEach(assetversion -> {
                        String pluginconfigid = assetversion.getPluginconfigid();
                        Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                        String pluginconfigName = pluginconfig.getName();
                        String pluginconfigType = pluginconfig.getType();
                        String pluginconfigArgs = pluginconfig.getArgs();
                        String pluginconfigTimeout = pluginconfig.getTimeout();
                        String pluginconfigPlugincode = pluginconfig.getPlugincode();
                        String pluginConfigValidateType = pluginconfig.getValidatetype();

                        String versionName = assetversion.getAssetversion();
                        //根据version获取未下线端口列表
                        Map<String, Object> searchMap = new HashMap<>();
                        searchMap.put("version", versionName);
                        searchMap.put("state", "open");
                        searchMap.put("checkwhitelist", false);
                        searchMap.put("downtime", null);
                        List<Assetport> assetportList = assetportService.findSearch(searchMap);
                        //List<Assetport> assetportList = assetportService.findByVersionLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(versionName,"open");
                        if (!assetportList.isEmpty()) {
                            assetportList.parallelStream().forEach(assetport -> {
                                String assetipid = assetport.getAssetipid();
                                //Assetip assetip = assetipService.findById(assetipid);
                                Assetip assetip = assetipService.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(assetipid);
                                if (!Objects.isNull(assetip)) {
                                    if ("selfd".equals(pluginconfigType)) {
                                        //貌似service没啥用
                                        String ipv4 = assetip.getIpaddressv4();
                                        String assetPortName = assetport.getPort();
                                        String assetServiceName = assetport.getService();
                                        String assetVersionName = assetport.getVersion();

                                        //ip|port|pluginName|args|timeout|service|version
                                        selfdSet.add(ipv4 + "<=-=>" + assetPortName + "<=-=>" + pluginconfigName + "<=-=>" + pluginconfigArgs + "<=-=>" + pluginconfigTimeout + "<=-=>" + pluginconfigPlugincode + "<=-=>" + pluginConfigValidateType + "<=-=>" + assetServiceName + "<=-=>" + assetVersionName);
                                    }
                                }
                            });
                        }
                    });
                }
                if (selfdSet.isEmpty()) {
                    return;
                }
                //写到redis
                String selfdSliceIPList = "selfdSliceIPList_" + taskId;
                //pluginconfigid|ip|port|pluginName|args|timeout
                //127.0.0.1|6379|RedisInfo|info|1000
                selfdSet.forEach(selfdCommand -> {
                    redisTemplate.opsForList().leftPush(selfdSliceIPList, selfdCommand);
                });
                //设置当前分组大小
                sliceIPListSize = redisTemplate.opsForList().size(selfdSliceIPList);
                redisTemplate.opsForValue().set("sliceIPListSize_" + taskId, String.valueOf(redisTemplate.opsForList().size(selfdSliceIPList)));
                taskConfig = pojoTask2TaskConfigMap(taskId, selfdSliceIPList, task);
                break;
            case "httpp":
                //从数据库中获取http like
                Set<String> httpLikeOrUnsureServiceSet = getAssetPortHttpLikeOrUnsureService(assetipService, assetportService);
                String httppSliceIPList = "httppSliceIPList_" + taskId;
                if (httpLikeOrUnsureServiceSet.isEmpty())
                    return;
                httpLikeOrUnsureServiceSet.forEach(singleHttp -> {
                    redisTemplate.opsForList().leftPush(httppSliceIPList, singleHttp);
                });
                //设置当前分组大小
                sliceIPListSize = redisTemplate.opsForList().size(httppSliceIPList);
                redisTemplate.opsForValue().set("sliceIPListSize_" + taskId, String.valueOf(redisTemplate.opsForList().size(httppSliceIPList)));
                taskConfig = pojoTask2TaskConfigMap(taskId, httppSliceIPList, task);
                break;
            default:
                return;
        }
        //记录任务开始时间
        task.setStarttime(new Date());
        //清空任务结束时间
        //task.setEndtime(null);
        taskService.update(task);

        //向上取整，确保所有的agent取出的数量大于等于总数
        double maxSliceSize = Math.ceil(sliceIPListSize / agentCount);

        //任务丢给MQ
        taskConfig.put("maxSliceSize", String.valueOf(maxSliceSize));
        rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
        //rabbitMessagingTemplate.convertAndSend(taskWorkType + "fanout", "", taskConfig);
    }

    /**
     * 根据taskId停止任务并删除计划任务
     *
     * @param taskId
     * @return
     */
    public synchronized void stopMutilTaskAndDeleteSchedule(String taskId) throws SchedulerException {
        List<Task> taskChildList = taskService.findAllByTaskparentid(taskId);
        if (taskChildList.isEmpty()) {
            stopSingleTaskAndDeleteSchedule(taskId);
        } else {
            stopSingleTaskAndDeleteSchedule(taskId);
            for (Task childTask : taskChildList) {
                String childTaskId = childTask.getId();
                stopSingleTaskAndDeleteSchedule(childTaskId);
            }
        }

    }

    private void stopSingleTaskAndDeleteSchedule(String taskId) throws SchedulerException {
        //删除计划任务
        String jobKeyName = "jobKeyName_" + taskId;
        String jobKeyGroup = "jobKeyGroup_" + taskId;
        JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
        quartzJobService.deleteJob(jobKey);

        //停止任务
        Task task = taskService.findById(taskId);

        Map<String, String> taskConfig = new HashMap<>();
        //任务丢给agent
        taskConfig.put("taskId", taskId);
        taskConfig.put("status", "stop");


        //取消crontask标记
        task.setCrontask(false);
        //记录任务结束时间
        if (!Objects.isNull(task.getStarttime())) {
            task.setEndtime(new Date());
        }
        taskService.update(task);
        rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
        //rabbitMessagingTemplate.convertAndSend(task.getWorktype() + "fanout", "", taskConfig);
    }

    /**
     * 根据taskId获取任务状态
     *
     * @param taskId
     * @return
     */
    public Map<String, Object> getMutilTaskStatus(String taskId) {
        Map<String, Object> taskStatus = new LinkedHashMap<>();
        Task task = taskService.findById(taskId);
        //未开始，starttime和endtime都为空
        if (Objects.isNull(task.getStarttime()) && Objects.isNull(task.getEndtime())) {
            taskStatus.put("任务未开始", taskId);
            //正在运行，starttime不为空，endtime空
        } else if (!Objects.isNull(task.getStarttime()) && Objects.isNull(task.getEndtime())) {
            taskStatus.put("任务状态", getSingleTaskStatus(taskId, redisTemplate));
            //已完成
        } else if (!Objects.isNull(task.getStarttime()) && !Objects.isNull(task.getEndtime())) {
            //判断子任务
            List<Task> taskChildList = taskService.findAllByTaskparentid(taskId);
            //没有子任务
            if (taskChildList.isEmpty()) {
                taskStatus.put("任务已完成", taskId);

                if (!redisTemplate.hasKey("sliceIPListSize_" + taskId)) {
                    //查询状态时清除已完成，防止任务被终结，但accomplishTaskList缓存没清除
                    redisTemplate.delete("accomplishTaskList_" + taskId);
                }

                //taskStatus.put("taskcomplicated", taskId);
            } else {
                taskChildList.forEach(childTask -> {
                    String childTaskId = childTask.getId();
                    Map<String, Object> childTaskStatus = new LinkedHashMap<>();
                    //子任务已完成
                    if (!Objects.isNull(childTask.getStarttime()) && !Objects.isNull(childTask.getEndtime())) {
                        childTaskStatus.put("子任务已完成", childTaskId);

                        if (!redisTemplate.hasKey("sliceIPListSize_" + taskId)) {
                            //查询状态时清除已完成，防止任务被终结，但accomplishTaskList缓存没清除
                            redisTemplate.delete("accomplishTaskList_" + taskId);
                        }

                        taskStatus.put("子任务状态" + childTaskId, childTaskStatus);
                    } else {
                        taskStatus.put("子任务状态" + childTaskId, getSingleTaskStatus(childTaskId, redisTemplate));
                    }
                });
            }
        }
        return taskStatus;
    }

    //监控任务状态
    private Map<String, String> getSingleTaskStatus(String taskId, RedisTemplate<String, String> redisTemplate) {
        Map<String, String> taskStatus = new LinkedHashMap<>();
        //总任务数
        String totalTaskListName = "totalTaskList_" + taskId;
        //已完成任务数
        String accomplishTaskListName = "accomplishTaskList_" + taskId;
        //正在进行的，没有操作到这个..
        String workingTaskListName = "workingTaskList_" + taskId;
        //分组大小
        String sliceIPListSizeName = "sliceIPListSize_" + taskId;


        if (!redisTemplate.hasKey(sliceIPListSizeName)) {
            //任务未在进行
            taskStatus.put("任务未在进行", taskId);
        } else {
            long totalTaskListSize = redisTemplate.opsForList().size(totalTaskListName);
            long accomplishTaskListSize = redisTemplate.opsForList().size(accomplishTaskListName);
            long workingTaskListSize = redisTemplate.opsForList().size(workingTaskListName);

            //正在进行等于 totalTaskList与accomplishTaskList的差集
            //redisTemplate.opsForList().differenceAndStore(totalTaskListName, accomplishTaskListName, workingTaskListName);
            //
            List<String> totalTaskList = redisTemplate.opsForList().range(totalTaskListName, 0, -1);
            List<String> accomplishTaskList = redisTemplate.opsForList().range(accomplishTaskListName, 0, -1);
            //正在进行 = 总任务 - 已完成
            //计算后，totalTaskList 就是 working task
            if (totalTaskList != null && accomplishTaskList != null) {
                totalTaskList.removeAll(accomplishTaskList);
            }
            taskStatus.put("已完成", String.valueOf(accomplishTaskListSize));
            //taskStatus.put("进行中", String.valueOf(workingTaskListSize));
            String worktype = taskService.findById(taskId).getWorktype();
            //为啥之前把nse给去掉了?不记得了
//            if (!worktype.equals("nse") && !worktype.equals("selfd") && !worktype.equals("httpp")) {
            //修改插件核心，自定义插件也有PID了，执行文件的方式才有，Jep的方式与之前类似
            if (!"httpp".equals(worktype) && !"selfd".equals(worktype)) {
                taskStatus.put("进行中", String.valueOf(totalTaskList.size()));
                taskStatus.put("进行中PID", totalTaskList.toString());
            }

            if (redisTemplate.hasKey(sliceIPListSizeName)) {
                //进度监控
                long sliceIPListSize = Long.parseLong(redisTemplate.opsForValue().get(sliceIPListSizeName));
                double taskPercentStatus = (double) accomplishTaskListSize / sliceIPListSize;
                taskStatus.put("总数", String.valueOf(sliceIPListSize));
                taskStatus.put("剩余", String.valueOf(sliceIPListSize - accomplishTaskListSize));
                taskStatus.put("进度", String.valueOf(taskPercentStatus));
                //accomplishTaskListSize等于总分组数sliceIPList，任务结束
                if (accomplishTaskListSize == sliceIPListSize) {
                    taskStatus.put("任务结束", taskId);
                }
            }
        }
        return taskStatus;
    }

    public synchronized Map<String, Object> repeat(String taskId) throws InterruptedException {
        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        int agentCount;
        if (onlineAgentList.isEmpty()) {
            return null;
        } else {
            agentCount = onlineAgentList.size();
        }


        Task oldTask = taskService.findById(taskId);
        String newTaskId = idWorker.nextId() + "";
        if ("mass2Nmap".equals(oldTask.getWorktype())) {
            Nmapconfig oldNmapconfig = nmapconfigService.findByTaskid(taskId);
            String nmapconfigId = idWorker.nextId() + "";
            String threadnumber = oldNmapconfig.getThreadnumber();
            String singleipscantime = oldNmapconfig.getSingleipscantime();
            String additionoption = oldNmapconfig.getAdditionoption();

            Nmapconfig newNmapconfig = new Nmapconfig();
            newNmapconfig.setId(nmapconfigId);
            newNmapconfig.setThreadnumber(threadnumber);
            newNmapconfig.setSingleipscantime(singleipscantime);
            newNmapconfig.setAdditionoption(additionoption);
            newNmapconfig.setTaskid(newTaskId);
            nmapconfigService.add(newNmapconfig);
        }


        Task newTask = new Task(newTaskId, taskId, oldTask.getProjectid(), oldTask.getName(), "repeatTask", oldTask.getCronexpression(), false, new Date(), null,
                oldTask.getWorktype(), oldTask.getChecktype(), oldTask.getThreadnumber(), oldTask.getSingleipscantime(), oldTask.getAdditionoption(), oldTask.getRate(), oldTask.getTargetip(),
                oldTask.getTargetport(), oldTask.getExcludeip(), oldTask.getIpslicesize(), oldTask.getPortslicesize(), oldTask.getDbipisexcludeip(), oldTask.getMerge2asset());
        newTask.setId(newTaskId);
        taskService.add(newTask);

        Map<String, Object> taskInfo = new LinkedHashMap<>();
        //ip分组，进redis
        String sliceIPList = TargetIpSlicer.slice(newTaskId, taskInfo, assetipService, taskipService, taskService, assetportService, redisTemplate, rabbitMessagingTemplate, agentCount);
        Map<String, String> taskConfig = pojoTask2TaskConfigMap(newTaskId, sliceIPList, newTask);

        //分组数存入redis
        double sliceIPListSize = redisTemplate.opsForList().size(sliceIPList);
        redisTemplate.opsForValue().set("sliceIPListSize_" + newTaskId, String.valueOf(redisTemplate.opsForList().size(sliceIPList)));
        //向上取整，确保所有的agent取出的数量大于等于总数
        double maxSliceSize = Math.ceil(sliceIPListSize / agentCount);
        taskConfig.put("maxSliceSize", String.valueOf(maxSliceSize));

        rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
        //rabbitMessagingTemplate.convertAndSend(newTask.getWorktype() + "fanout", "", taskConfig);
        taskInfo.put("new task id", newTaskId);
        return taskInfo;
    }

}

package com.tiji.agent.mq;

import com.tiji.agent.thread.CheckerThread;
import com.tiji.agent.thread.GatherThread;
import com.tiji.agent.thread.ParserThread;
import com.tiji.agent.util.AgentGatherHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import util.ExcpUtil;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;

/**
 * @author 贰拾壹
 * @create 2019-07-03 15:53
 */


@Component

@RabbitListener(bindings = @QueueBinding(value = @Queue(value = ("${task.name}")), exchange = @Exchange("tijifanout")))
public class TaskReceiver {

    //id，runningTaskMap
    //保存运行中的任务，用于终止任务
    private final static Map<String, Map<String, ExecutorService>> RUNNING_TASK_MAP = new Hashtable<>();
    private final static Logger logger = LoggerFactory.getLogger(TaskReceiver.class);
    //public static final ConcurrentHashMap<String, ExecutorService> runningTaskMap = new ConcurrentHashMap<>();
    private static String agentName;
    private static String allAddress;

    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            agentName = "agent_" + addr.getHostName();
        } catch (UnknownHostException e) {
            agentName = "agent_" + "UnknownHostException";
        }
        try {
            AgentGatherHelper agentGatherHelper = new AgentGatherHelper();
            allAddress = agentGatherHelper.getAllAddress();
        } catch (SocketException e) {
            allAddress = e.getLocalizedMessage();
        }
    }

    @Value("${nmap.path}")
    private String nmapPath;
    @Value("${mass.path}")
    private String massPath;
    @Value("${center.httpValidateApi}")
    private String httpValidateApi;
    @Value("${center.dnsValidateIp}")
    private String dnsValidateIp;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @RabbitHandler
    public void getMessage(Map<String, String> task) {
        try {
            String taskId = task.get("taskId");
            String sliceIPList = task.get("sliceIPList");
            String workType = task.get("workType");
            switch (task.get("status")) {
                case "start":
                    Boolean existSliceIPList = redisTemplate.hasKey(sliceIPList);
                    if (!Objects.isNull(existSliceIPList) && existSliceIPList) {
                        System.out.println("getting " + workType + " task.... size: " + redisTemplate.opsForList().size(sliceIPList));

                        String threadNumber = task.get("threadNumber");
                        String singleIpScanTime = task.get("singleIpScanTime");
                        String targetPort = task.get("targetPort");
                        String rate = task.get("rate");
                        String additionOption = task.get("additionOption");


                        double maxSliceSize = Double.parseDouble(task.get("maxSliceSize"));
                        int count = 0;

                        String sliceIPListSizeName = "sliceIPListSize_" + taskId;
                        Boolean existSliceIPListSizeName = redisTemplate.hasKey(sliceIPListSizeName);
                        if (!Objects.isNull(existSliceIPListSizeName) && existSliceIPListSizeName) {
                            String targetIps;
                            //线程数量
                            ExecutorService totalThreadPool = Executors.newFixedThreadPool(Integer.parseInt(threadNumber));
                            //当前任务信息进runningTaskMap
                            Map<String, ExecutorService> singleTaskMap = new LinkedHashMap<>();
                            singleTaskMap.put("totalThreadPool", totalThreadPool);
                            RUNNING_TASK_MAP.put(taskId, singleTaskMap);
                            while (count < maxSliceSize) {
                                targetIps = redisTemplate.opsForList().leftPop(sliceIPList);
                                if (!Objects.isNull(targetIps)) {
                                    //从sliceIPList中取IPs进行处理
                                    for (int i = 0; i < Integer.parseInt(singleIpScanTime); i++) {
                                        if ("selfd".equals(workType)) {
                                            //self define plugin
                                            totalThreadPool.execute(new Thread(new CheckerThread(taskId, workType, targetIps, httpValidateApi, dnsValidateIp, rabbitMessagingTemplate, redisTemplate)));
                                        } else if ("httpp".equals(workType)) {
                                            //http crawler model
                                            String redisDictUserAgent = "userAgentSet_";
                                            String userAgent;
                                            Boolean existUserAgent = redisTemplate.hasKey(redisDictUserAgent);
                                            if (!Objects.isNull(existUserAgent) && existUserAgent) {
                                                userAgent = redisTemplate.opsForSet().randomMember(redisDictUserAgent);
                                            } else {
                                                userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";
                                            }
//                                            String userAgent = task.get("userAgent");
                                            totalThreadPool.execute(new Thread(new ParserThread(taskId, userAgent, workType, targetIps, rabbitMessagingTemplate, redisTemplate)));
                                        } else {
                                            //不是mass二次结果 去掉,[]
                                            if (!targetIps.contains("-p")) {
                                                targetIps = targetIps.replaceAll("[,\\[\\]]", "");
                                            }
                                            if (!currentThread().isInterrupted()) {
                                                totalThreadPool.execute(new Thread(new GatherThread(taskId, workType, targetIps, rabbitMessagingTemplate, targetPort, additionOption, rate, nmapPath, massPath, redisTemplate)));
                                            }
                                        }
                                    }
                                }
                                count++;
                            }
                            //关闭线程池
                            totalThreadPool.shutdown();
                        }
                    }
                    break;
                case "stop":
                    System.out.println("task stop here 1 :");
                    if (!RUNNING_TASK_MAP.isEmpty()) {
                        System.out.println("task stop here 2 :");
                        if (RUNNING_TASK_MAP.containsKey(taskId)) {
                            Map<String, ExecutorService> singleTaskMap = RUNNING_TASK_MAP.get(taskId);
                            if (!Objects.isNull(singleTaskMap) && !singleTaskMap.isEmpty()) {
                                System.out.println("task stop here 3 :");

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
                                ExecutorService executorService = singleTaskMap.get("totalThreadPool");

                                System.out.println(totalTaskListName);
                                System.out.println("stopping task now...");
                                executorService.shutdown();
                                try {
                                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                                        // 超时则向线程池中所有的线程发出中断(interrupted)
                                        executorService.shutdownNow();
                                    }
                                } catch (InterruptedException e) {
                                    executorService.shutdownNow();
                                }
                                System.out.println("removing task process...");

                                List<String> totalTaskList = redisTemplate.opsForList().range(totalTaskListName, 0, -1);
                                List<String> accomplishTaskList = redisTemplate.opsForList().range(accomplishTaskListName, 0, -1);
                                //正在进行 = 总任务 - 已完成
                                //计算后，totalTaskList 就是 working task
                                if (totalTaskList != null && accomplishTaskList != null) {
                                    totalTaskList.removeAll(accomplishTaskList);
                                }
                                //停止任务进程
                                if (!Objects.isNull(totalTaskList)) {
                                    totalTaskList.parallelStream().forEach(singleTaskId -> {

                                        Optional<ProcessHandle> handleOptional = ProcessHandle.of(Long.parseLong(singleTaskId));
                                        if (handleOptional.isPresent()) {
                                            ProcessHandle processHandle = handleOptional.get();
                                            ProcessHandle.Info info1 = processHandle.info();
                                            if (info1.command().isPresent()) {
                                                String command = info1.command().get();
                                                //System.out.println("info command: " + command);
                                                //避免agent被干掉
                                                if (!command.contains("java")) {
                                                    ProcessHandle.of(Long.parseLong(singleTaskId)).ifPresent(ProcessHandle::destroy);
                                                }
                                            }
                                        }
                                    });
                                }
                                //进程结束后，会向accomplishTaskList写数据，设置过期延迟
                                redisTemplate.expire(accomplishTaskListName, 5, TimeUnit.SECONDS);

                                System.out.println("stopping executorService....");
                                System.out.println("thread pool closed...");


                                //清空任务的redis缓存
                                //redisTemplate.delete(totalTaskListName);
                                //redisTemplate.delete(accomplishTaskListName);
                                //redisTemplate.delete(workingTaskListName);
                                redisTemplate.delete(sliceIPListSizeName);

                                redisTemplate.expire(totalTaskListName, 5, TimeUnit.SECONDS);
                                redisTemplate.expire(workingTaskListName, 5, TimeUnit.SECONDS);
                                //redisTemplate.expire(sliceIPListSizeName, 5, TimeUnit.SECONDS);

                                //任务退出，用于结束mass2Nmap第一次任务后，防止发送第二次nmap任务
                                redisTemplate.opsForValue().set(taskStatusName, "exit");
                                redisTemplate.expire(taskStatusName, 5, TimeUnit.SECONDS);


                                System.out.println("task " + taskId + " stopped");
                                RUNNING_TASK_MAP.remove(taskId);
                            }

                        } else {
                            System.out.println("task " + taskId + " stopped or not in working...");
                        }
                    }
                    break;
                case "removeAccomplishTask":
                    if (RUNNING_TASK_MAP.containsKey(taskId)) {
                        //增加任务完成时，关闭线程池
                        if (!Objects.isNull(workType) && !"mass2Nmap".equals(workType)) {
                            Map<String, ExecutorService> singleTaskMap = RUNNING_TASK_MAP.get(taskId);
                            if (!Objects.isNull(singleTaskMap) && !singleTaskMap.isEmpty()) {
                                ExecutorService executorService = singleTaskMap.get("totalThreadPool");
                                executorService.shutdown();
                                try {
                                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                                        // 超时则向线程池中所有的线程发出中断(interrupted)
                                        executorService.shutdownNow();
                                    }
                                } catch (InterruptedException e) {
                                    executorService.shutdownNow();
                                }
                            }
                        }
                        RUNNING_TASK_MAP.remove(taskId);
                        //正在进行的，没有操作到这个..
                        String workingTaskListName = "workingTaskList_" + taskId;
                        //分组大小
                        String sliceIPListSizeName = "sliceIPListSize_" + taskId;
                        //redisTemplate.delete(workingTaskListName);
                        //redisTemplate.delete(sliceIPListSizeName);
                        //redisTemplate.expire(workingTaskListName, 5, TimeUnit.SECONDS);
//                        redisTemplate.expire("ip_" + taskId, 5, TimeUnit.SECONDS);
//                        redisTemplate.expire("port_" + taskId, 5, TimeUnit.SECONDS);
//                        redisTemplate.expire("cmd_" + taskId, 5, TimeUnit.SECONDS);
//                        redisTemplate.expire("nginxRawVersion_"+taskId, 5, TimeUnit.SECONDS);
//                        redisTemplate.expire("timeout_" + taskId, 5, TimeUnit.SECONDS);
                        redisTemplate.expire(sliceIPListSizeName, 5, TimeUnit.SECONDS);

                        System.out.printf("remove task %s success\n", taskId);
                    }
                    break;
                case "heartbeat":

                    //InetAddress addr = InetAddress.getLocalHost();
                    //String agentName = "agent_" + addr.getHostName();
                    Map<String, String> agentConfig = new HashMap<>();
                    agentConfig.put("agentName", agentName);
                    agentConfig.put("nmapPath", nmapPath);
                    agentConfig.put("massPath", massPath);
                    agentConfig.put("online", agentName);
                    agentConfig.put("ipAddress", allAddress);
                    //TODO 发送cpu和内存状态
                    rabbitMessagingTemplate.convertAndSend("agentconfig", agentConfig);
                    break;
                default:
                    System.out.println("error");
            }
        } catch (Exception e) {
            logger.error("TaskReceiver Exception here: " + " _ " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

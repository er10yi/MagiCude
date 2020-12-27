package com.tiji.center.schedule;

import com.tiji.center.pojo.Task;
import com.tiji.center.service.TaskService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.ExcpUtil;

import java.util.*;


/**
 * @author 贰拾壹
 * @create 2019-09-30 10:32
 */
public class TaskStatusMonitorScheduler implements Job {
    private final static Logger logger = LoggerFactory.getLogger(TaskStatusMonitorScheduler.class);
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            //监控总任务信息
            //遍历所有starttime不为null的id，防止未刷新结束任务信息
            List<Task> startTimeNotNullTaskList = taskService.findAllByEndtimeIsNullAndStarttimeIsNotNull();
            List<Task> endTaskList = taskService.findAllByWorktypeAndEndtimeIsNotNullAndStarttimeIsNotNull("selfd");
            endTaskList.addAll(taskService.findAllByWorktypeAndEndtimeIsNotNullAndStarttimeIsNotNull("httpp"));
            ///加nmap
            endTaskList.addAll(taskService.findAllByWorktypeAndEndtimeIsNotNullAndStarttimeIsNotNull("nmap"));

            if (!startTimeNotNullTaskList.isEmpty()) {
                for (Task task : startTimeNotNullTaskList) {
                    String worktype = task.getWorktype();
                    //if (!worktype.equals("mass2Nmap")) {
                    String taskIdTotal = task.getId();
                    //已完成任务数
                    String accomplishTaskListNameTotal = "accomplishTaskList_" + taskIdTotal;
                    //分组大小
                    String sliceIPListSizeNameTotal = "sliceIPListSize_" + taskIdTotal;
                    //总任务数
                    String totalTaskListNameTotal = "totalTaskList_" + taskIdTotal;
                    //正在进行的，没有操作到这个..
                    String workingTaskListNameTotal = "workingTaskList_" + taskIdTotal;

                    System.out.println("TaskStatusMonitorScheduler running for taskId: " + taskIdTotal);
                    Boolean aBoolean = redisTemplate.hasKey(sliceIPListSizeNameTotal);
                    if (!Objects.isNull(aBoolean) && aBoolean) {
                        long accomplishTaskListSizeTotal = redisTemplate.opsForList().size(accomplishTaskListNameTotal);
                        long sliceIPListSizeTotal = Long.parseLong(redisTemplate.opsForValue().get(sliceIPListSizeNameTotal));
                        System.out.println("accomplishTaskListSizeTotal: " + accomplishTaskListSizeTotal);
                        System.out.println("sliceIPListSizeTotal: " + sliceIPListSizeTotal);

                        if (accomplishTaskListSizeTotal != 0 && accomplishTaskListSizeTotal == sliceIPListSizeTotal) {
                            if (!"mass2Nmap".equals(worktype)) {
                                System.out.println("TaskStatusMonitorScheduler update task endtime " + task.getId());
                                //如果任务结束，给agent发结束信息，runningTaskMap删除已完成任务
                                Map<String, String> taskConfig = new HashMap<>();
                                taskConfig.put("status", "removeAccomplishTask");
                                taskConfig.put("taskId", task.getId());
                                rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);

                                redisTemplate.delete(totalTaskListNameTotal);
                                redisTemplate.delete(accomplishTaskListNameTotal);
                                //redisTemplate.delete(workingTaskListNameTotal);
                                redisTemplate.delete(sliceIPListSizeNameTotal);
//                                redisTemplate.delete("ip_" + taskIdTotal);
//                                redisTemplate.delete("port_" + taskIdTotal);
//                                redisTemplate.delete("cmd_" + taskIdTotal);
//                                redisTemplate.delete("nginxRawVersion_"+taskIdTotal);
//                                redisTemplate.delete("timeout_" + taskIdTotal);
                                task.setEndtime(new Date());
                                taskService.update(task);
                            }
                            if ("mass2Nmap".equals(worktype)) {
                                //防止任务结束后，scanResultReceiver接收不到最后的结果，不能更新secondTaskFlag
                                Map<String, String> resultMap = new HashMap<>();
                                resultMap.put("workType", "mass2Nmap");
                                resultMap.put("taskId", taskIdTotal);
                                resultMap.put("scanResult", "mass2Nmap");
                                rabbitMessagingTemplate.convertAndSend("scanresult", resultMap);
                            }
                        }
                        //结果不受影响
                        //if (worktype.equals("mass") || worktype.equals("mass2Nmap") || worktype.equals("nmap") || worktype.equals("nse")) {
                        //    //if (worktype.equals("mass") || worktype.equals("nmap") || worktype.equals("nse")) {
                        //    if (!redisTemplate.hasKey(sliceIPListSizeNameTotal) && !redisTemplate.hasKey(workingTaskListNameTotal)) {
                        //        System.out.println("TaskStatusMonitorScheduler update task endtime " + task.getId());
                        //        redisTemplate.delete(accomplishTaskListNameTotal);
                        //        redisTemplate.delete(sliceIPListSizeNameTotal);
                        //        task.setEndtime(new Date());
                        //        taskService.update(task);
                        //    }
                        //} else {
                        //    //httpp selfd 手动停止时会有残留
                        //    if (!redisTemplate.hasKey(sliceIPListSizeNameTotal)) {
                        //        System.out.println("TaskStatusMonitorScheduler remove redis cache " + task.getId());
                        //        redisTemplate.delete(accomplishTaskListNameTotal);
                        //    }
                        //}
                        //
                    }

                }
            }
            if (!endTaskList.isEmpty()) {
                for (Task task : endTaskList) {
                    //httpp selfd 手动停止时会有线程残留，清除redis缓存
                    //加nmap
                    String worktype = task.getWorktype();
                    String taskIdTotal = task.getId();
                    //已完成任务数
                    String accomplishTaskListNameTotal = "accomplishTaskList_" + taskIdTotal;
                    //分组大小
                    String sliceIPListSizeNameTotal = "sliceIPListSize_" + taskIdTotal;
                    //总任务数
                    String totalTaskListNameTotal = "totalTaskList_" + taskIdTotal;
                    //正在进行的，没有操作到这个..
                    String workingTaskListNameTotal = "workingTaskList_" + taskIdTotal;
                    if (("selfd".equals(worktype) || "httpp".equals(worktype) || "nmap".equals(worktype)) && (task.getStarttime() != null && task.getEndtime() != null) && (!redisTemplate.hasKey(sliceIPListSizeNameTotal) && redisTemplate.hasKey(accomplishTaskListNameTotal))) {
                        redisTemplate.delete(accomplishTaskListNameTotal);
//                        redisTemplate.delete("ip_" + taskIdTotal);
//                        redisTemplate.delete("port_" + taskIdTotal);
//                        redisTemplate.delete("cmd_" + taskIdTotal);
//                        redisTemplate.delete("nginxRawVersion_"+taskIdTotal);
//                        redisTemplate.delete("timeout_" + taskIdTotal);
                        redisTemplate.delete(totalTaskListNameTotal);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("TaskStatusMonitorScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

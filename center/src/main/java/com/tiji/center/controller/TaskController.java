package com.tiji.center.controller;

import com.tiji.center.pojo.Agent;
import com.tiji.center.pojo.Nmapconfig;
import com.tiji.center.pojo.Project;
import com.tiji.center.pojo.Task;
import com.tiji.center.schedule.ExecuteCheckTaskScheduler;
import com.tiji.center.schedule.ExecuteWorkTaskScheduler;
import com.tiji.center.schedule.quartz.QuartzJob;
import com.tiji.center.schedule.quartz.QuartzJobService;
import com.tiji.center.service.*;
import com.tiji.center.util.TijiHelper;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.quartz.*;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * task控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDispatcherService taskDispatcherService;

    @Autowired
    private NmapconfigService nmapconfigService;

    @Autowired
    private QuartzJobService quartzJobService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AgentService agentService;
    @Autowired
    private TaskpluginconfigService taskpluginconfigService;
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    @Autowired
    private ProjectService projectService;
    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", taskService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", taskService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
     @PostMapping(value = "/search/{page}/{size}")
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Task> pageList = taskService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(task -> {
            String taskparentid = task.getTaskparentid();
            if (!StringUtils.isEmpty(taskparentid)) {
                Task task1 = taskService.findById(taskparentid);
                task.setTaskparentid(task1.getName());
            }

            String taskId = task.getId();
            String projectid = task.getProjectid();
            if (!StringUtils.isEmpty(projectid)) {
                Project project = projectService.findById(projectid);
                if (!Objects.isNull(project)) {
                    task.setProjectid(project.getName());
                }
            }
            List<Task> childTaskList = taskService.findAllByTaskparentid(taskId);
            String name = task.getName();
            if (!StringUtils.isEmpty(name)) {
                task.setStatistic(String.valueOf(childTaskList.size()));
            }
            Double taskPercent = taskService.getTaskPercent(taskId);
            task.setPercentage(String.valueOf(taskPercent));
            try {
                String triggerStates = quartzJobService.getTriggerStates(taskId);
                task.setJobstate(triggerStates);
            } catch (SchedulerException ignored) {
            }

        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", taskService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param task
     */
    @PostMapping
    public Result add(@RequestBody Task task) {
        String taskId = "";
        if (Objects.isNull(task.getId())) {
            taskId = idWorker.nextId() + "";
            task.setId(taskId);
        }
        taskService.add(task);
        if ("mass2Nmap".equals(task.getWorktype()) || "nse".equals(task.getWorktype()) || "selfd".equals(task.getWorktype())) {
            return new Result(true, StatusCode.OK, "增加成功", taskId);
        } else {
            return new Result(true, StatusCode.OK, "增加成功");
        }

    }

    /**
     * 修改
     *
     * @param task
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Task task, @PathVariable String id) {
        task.setId(id);
        taskService.update(task);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */

    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        //nmap配置在service层删除
        taskService.deleteById(id);
        //删除任务插件配置
        taskpluginconfigService.deleteAllByTaskid(id);
        //删除子任务
        taskService.deleteAllByTaskparentid(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据taskId开始任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/execute/{id}", method = RequestMethod.GET)
    public Result executeTask(@PathVariable String id) throws SchedulerException, InterruptedException, ParseException {
        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        if (onlineAgentList.isEmpty()) {
            return new Result(false, StatusCode.ERROR, "没有agent在线");
        }
        Map<String, Object> taskInfo = new HashMap<>();
        Task task = taskService.findById(id);
        String taskTargetIp = task.getTargetip();
        String taskWorkType = task.getWorktype();
        boolean crontask = task.getCrontask();

        //非cron任务，直接执行任务
        if (!crontask) {
            //对资产库中的所有whitelist为false的ip进行nse/selfd/httpp
            if ("assetip".equals(taskTargetIp) && ("nse".equals(taskWorkType) || "selfd".equals(taskWorkType) || "httpp".equals(taskWorkType))) {
                taskDispatcherService.executeTotalCheck(id);
            } else {
                //标准任务
                //判断有没有nmap的配置
                if ("mass2Nmap".equals(taskWorkType)) {
                    Nmapconfig nmapconfig = nmapconfigService.findByTaskid(id);
                    if (Objects.isNull(nmapconfig)) {
                        return new Result(false, StatusCode.ERROR, "失败：nmap配置为空");
                    }
                }
                taskInfo = taskDispatcherService.executeWork(id);
            }
        } else {
            //cron任务，进行schedule
            String cronExpression = task.getCronexpression();
            if (Objects.isNull(cronExpression) || cronExpression.isEmpty()) {
                return new Result(false, StatusCode.ERROR, "失败：Cron表达式为空");
            }
            if (!CronExpression.isValidExpression(cronExpression)) {
                return new Result(false, StatusCode.ERROR, "失败：Cron表达式错误");
            }
            String jobKeyName = "jobKeyName_" + id;
            String jobKeyGroup = "jobKeyGroup_" + id;
            JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
            Map<String, Object> jobDataMap = new HashMap<>();
            jobDataMap.put("taskId", id);
            jobDataMap.put("taskTargetIp", taskTargetIp);
            jobDataMap.put("taskWorkType", taskWorkType);
            //jobDataMap.put("taskInfo", taskInfo);

            QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, jobDataMap, ExecuteWorkTaskScheduler.class);
            quartzJobService.scheduleJob(quartzJob);

            taskInfo.putAll(TijiHelper.cronParseResult(cronExpression));
        }
        return new Result(true, StatusCode.OK, "成功开始", taskInfo);
    }

    /**
     * 根据taskId开始check任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/execute/check/{id}", method = RequestMethod.GET)
    public Result executeCheck(@PathVariable String id) throws SchedulerException, ParseException {
        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        if (onlineAgentList.isEmpty()) {
            return new Result(false, StatusCode.ERROR, "没有agent在线");
        }
        Map<String, Object> taskInfo = new HashMap<>();
        Task task = taskService.findById(id);
        //非cron任务，直接执行任务
        boolean crontask = task.getCrontask();
        if (!crontask) {
            taskDispatcherService.executeCheck(id);
        } else {
            //cron任务，进行schedule
            String cronExpression = task.getCronexpression();
            if (Objects.isNull(cronExpression) || cronExpression.isEmpty()) {
                throw new RuntimeException("Cron表达式为空");
            }
            if (!CronExpression.isValidExpression(cronExpression)) {
                throw new RuntimeException("Cron表达式错误");
            }
            String jobKeyName = "jobKeyName_" + id;
            String jobKeyGroup = "jobKeyGroup_" + id;
            JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
            Map<String, String> jobDataMap = new HashMap<>();
            jobDataMap.put("taskId", id);

            QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, jobDataMap, ExecuteCheckTaskScheduler.class);
            quartzJobService.scheduleJob(quartzJob);
            taskInfo.putAll(TijiHelper.cronParseResult(cronExpression));

        }
        return new Result(true, StatusCode.OK, "成功开始", taskInfo);
    }

    /**
     * 根据taskId停止任务，并删除计划任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    public Result stopTask(@PathVariable String id) throws SchedulerException {
        taskDispatcherService.stopMutilTaskAndDeleteSchedule(id);
        return new Result(true, StatusCode.OK, "成功结束", "任务已停止, 并删除计划任务");
    }

    /**
     * 根据taskId获取任务状态
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/status/{id}", method = RequestMethod.GET)
    public Result getTaskStatus(@PathVariable String id) {
        Map<String, Object> taskStatus = taskDispatcherService.getMutilTaskStatus(id);
        return new Result(true, StatusCode.OK, "任务状态", taskStatus);
    }

    /**
     * 根据taskId重新开始任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/repeat/{id}", method = RequestMethod.GET)
    public Result repeatTask(@PathVariable String id) throws InterruptedException {
        List<Agent> onlineAgentList = agentService.findAllByOnline(true);
        if (onlineAgentList.isEmpty()) {
            return new Result(false, StatusCode.ERROR, "没有agent在线");
        }
        Map<String, Object> taskInfo = taskDispatcherService.repeat(id);
        return new Result(true, StatusCode.OK, "成功开始", taskInfo);
    }


    /**
     * 根据taskId删除schedule任务，不终止当前任务
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/schedule/{id}", method = RequestMethod.DELETE)
    public Result stopScheduleTask(@PathVariable String id) throws SchedulerException {
        String jobKeyName = "jobKeyName_" + id;
        String jobKeyGroup = "jobKeyGroup_" + id;
        JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
        quartzJobService.deleteJob(jobKey);
        Task task = taskService.findById(id);
        task.setCrontask(false);
        taskService.update(task);
        return new Result(true, StatusCode.OK, "成功删除", "计划任务已删除");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        taskService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //nmap配置在service层删除
            //删除任务插件配置
            taskpluginconfigService.deleteAllByTaskid(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据taskId获取任务状态
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/statuspercent/{id}", method = RequestMethod.GET)
    public Result getTaskStatusPercent(@PathVariable String id) {
        Map<String, Object> taskStatus = taskDispatcherService.getTaskStatusPercent(id);
        return new Result(true, StatusCode.OK, "任务状态", taskStatus);
    }
}

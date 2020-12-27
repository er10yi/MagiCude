package com.tiji.center.controller;

import com.tiji.center.pojo.Cronjob;
import com.tiji.center.schedule.quartz.QuartzJob;
import com.tiji.center.schedule.quartz.QuartzJobService;
import com.tiji.center.service.CronjobService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * cronjob控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/cronjob")
public class CronjobController {

    private static final Map<String, String> JOB_CLASS_STRING_WHITELIST_MAP;

    static {
        JOB_CLASS_STRING_WHITELIST_MAP = new HashMap<>();
        JOB_CLASS_STRING_WHITELIST_MAP.put("任务状态监控", "TaskStatusMonitor");
        JOB_CLASS_STRING_WHITELIST_MAP.put("agent心跳包监控", "AgentHeartbeatMonitor");
        JOB_CLASS_STRING_WHITELIST_MAP.put("邮件资产报告", "AssetNotify");
        JOB_CLASS_STRING_WHITELIST_MAP.put("邮件漏洞报告", "VulnNotify");
        JOB_CLASS_STRING_WHITELIST_MAP.put("每天执行一次的任务", "MidnightTask");
        JOB_CLASS_STRING_WHITELIST_MAP.put("统计报表数据", "Statistics");
        JOB_CLASS_STRING_WHITELIST_MAP.put("IM通知", "IMNotify");
    }

    @Autowired
    private CronjobService cronjobService;
    @Autowired
    private QuartzJobService quartzJobService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", cronjobService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", cronjobService.findById(id));
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
        Page<Cronjob> pageList = cronjobService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(cronjob -> {
            String cronjobName = cronjob.getName();
            String jobClassString = JOB_CLASS_STRING_WHITELIST_MAP.get(cronjobName);
            String jobClassName = jobClassString + "Scheduler";
            try {
                String triggerStates = quartzJobService.getTriggerStates(jobClassName);
                cronjob.setJobstate(triggerStates);
            } catch (SchedulerException ignored) {
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Cronjob>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", cronjobService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param cronjob
     */
    //@PostMapping
    public Result add(@RequestBody Cronjob cronjob) {
        cronjobService.add(cronjob);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改，先删除，后增加
     *
     * @param cronjob
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Cronjob cronjob, @PathVariable String id) throws ClassNotFoundException, SchedulerException, ParseException {
        String cronjobName = cronjob.getName();
        if (!JOB_CLASS_STRING_WHITELIST_MAP.containsKey(cronjobName)) {
            return new Result(false, StatusCode.ERROR, "修改失败");
        }

        String cronExpression = cronjob.getCronexpression();
        if (!CronExpression.isValidExpression(cronExpression)) {
            return new Result(false, StatusCode.ERROR, "修改失败：Cron表达式错误");
        }
        boolean errIM = lessThanMinInterval("IM通知", cronjobName, cronExpression, 4L);
        if (errIM) {
            return new Result(false, StatusCode.ERROR, "IM通知时间间隔不能小于4秒");
        }
        boolean errAssetMail = lessThanMinInterval("邮件资产报告", cronjobName, cronExpression, 600L);
        if (errAssetMail) {
            return new Result(false, StatusCode.ERROR, "邮件资产报告时间间隔不能小于10分钟");
        }
        boolean errVulnMail = lessThanMinInterval("邮件漏洞报告", cronjobName, cronExpression, 600L);
        if (errVulnMail) {
            return new Result(false, StatusCode.ERROR, "邮件漏洞报告时间间隔不能小于10分钟");
        }
        boolean errHeartbeat = lessThanMinInterval("agent心跳包监控", cronjobName, cronExpression, 60L);
        if (errHeartbeat) {
            return new Result(false, StatusCode.ERROR, "时间间隔不能小于1分钟");
        }
        cronjob.setId(id);
        cronjobService.update(cronjob);
        String jobClassString = JOB_CLASS_STRING_WHITELIST_MAP.get(cronjobName);
        String jobClassName = jobClassString + "Scheduler";

        String jobKeyName = "jobKeyName_" + jobClassName;
        String jobKeyGroup = "jobKeyGroup_" + jobClassName;
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName("com.tiji.center.schedule." + jobClassName);
        JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);

        quartzJobService.deleteJob(jobKey);
        QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, null, jobClass);

        quartzJobService.scheduleJob(quartzJob);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    private boolean lessThanMinInterval(String sourceCronjobName, String targetCronjobName, String cronExpression, Long minInterval) throws ParseException {
        if (sourceCronjobName.equals(targetCronjobName)) {
            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
            cronTriggerImpl.setCronExpression(cronExpression);
            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 2);
            long interval = dates.get(1).getTime() - dates.get(0).getTime();
            return interval < minInterval * 1000;
        }
        return false;
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) throws SchedulerException {
        //cronjobService.deleteById(id);
        Cronjob cronjob = cronjobService.findById(id);
        if (Objects.isNull(cronjob)) {
            return new Result(false, StatusCode.ERROR, "删除失败");
        }
        String cronjobName = cronjob.getName();
        if (!JOB_CLASS_STRING_WHITELIST_MAP.containsKey(cronjobName)) {
            return new Result(false, StatusCode.ERROR, "删除失败");
        }
        String jobClassString = JOB_CLASS_STRING_WHITELIST_MAP.get(cronjobName);
        String jobClassName = jobClassString + "Scheduler";

        String jobKeyName = "jobKeyName_" + jobClassName;
        String jobKeyGroup = "jobKeyGroup_" + jobClassName;
        JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
        quartzJobService.deleteJob(jobKey);

        cronjob.setCronexpression(null);
        cronjobService.update(cronjob);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * cronExpression模拟解析
     */
    @RequestMapping(value = "/parse", method = RequestMethod.POST)
    public Result parse(@RequestBody Map searchMap) throws ParseException {
        String cronExpression = (String) searchMap.get("cronExpression");
        if (StringUtils.isEmpty(cronExpression) || !CronExpression.isValidExpression(cronExpression)) {
            return new Result(false, StatusCode.ERROR, "解析失败：Cron表达式错误");
        }
        List<String> resultList = new ArrayList<>();
        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        cronTriggerImpl.setCronExpression(cronExpression);
        List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateNow = new Date();
        String now = sdf.format(dateNow);
        resultList.add(now);
        for (Date date : dates) {
            resultList.add(sdf.format(date));
        }
        return new Result(true, StatusCode.OK, "解析成功", resultList);
    }
}

package com.tiji.center.controller;

import com.tiji.center.pojo.Cronjob;
import com.tiji.center.schedule.quartz.QuartzJob;
import com.tiji.center.schedule.quartz.QuartzJobService;
import com.tiji.center.service.CronjobService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", cronjobService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
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
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Cronjob> pageList = cronjobService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Cronjob>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", cronjobService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param cronjob
     */
    //@RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Cronjob cronjob) {
        cronjobService.add(cronjob);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改，先删除，后增加
     *
     * @param cronjob
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Cronjob cronjob, @PathVariable String id) throws ClassNotFoundException, SchedulerException {
        String cronjobName = cronjob.getName();
        if (!JOB_CLASS_STRING_WHITELIST_MAP.containsKey(cronjobName)) {
            return new Result(false, StatusCode.ERROR, "修改失败");
        }

        String cronExpression = cronjob.getCronexpression();
        if (!CronExpression.isValidExpression(cronExpression)) {
            return new Result(false, StatusCode.ERROR, "修改失败：Cron表达式错误");
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

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
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
    public Result parse(@RequestBody Map searchMap) {
        String cronExpression = (String) searchMap.get("cronExpression");
        if (StringUtils.isEmpty(cronExpression) || !CronExpression.isValidExpression(cronExpression)) {
            return new Result(false, StatusCode.ERROR, "解析失败：Cron表达式错误");
        }
        List<Object> resultList = new LinkedList<>();
        //只支持6位
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String format = sdf.format(date);
        Date date1 = cronSequenceGenerator.next(date);
        Date date2 = cronSequenceGenerator.next(date1);
        Date date3 = cronSequenceGenerator.next(date2);
        Date date4 = cronSequenceGenerator.next(date3);
        Date date5 = cronSequenceGenerator.next(date4);
        Date date6 = cronSequenceGenerator.next(date5);
        Date date7 = cronSequenceGenerator.next(date6);
        Date date8 = cronSequenceGenerator.next(date7);
        Date date9 = cronSequenceGenerator.next(date8);
        Date date10 = cronSequenceGenerator.next(date9);
        resultList.add(format);
        resultList.add(sdf.format(date1));
        resultList.add(sdf.format(date2));
        resultList.add(sdf.format(date3));
        resultList.add(sdf.format(date4));
        resultList.add(sdf.format(date5));
        resultList.add(sdf.format(date6));
        resultList.add(sdf.format(date7));
        resultList.add(sdf.format(date8));
        resultList.add(sdf.format(date9));
        resultList.add(sdf.format(date10));
        return new Result(true, StatusCode.OK, "解析成功", resultList);
    }
}

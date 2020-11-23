package com.tiji.center.schedule.quartz;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;


@Service
public class QuartzJobService {

    @Autowired
    private Scheduler scheduler;
    private static final Map<String, String> jobStateMap = new LinkedHashMap<>();

    static {
        jobStateMap.put("BLOCKED", "阻塞");
        jobStateMap.put("COMPLETE", "完成");
        jobStateMap.put("ERROR", "出错");
        jobStateMap.put("NONE", "");
        jobStateMap.put("NORMAL", "正常");
        jobStateMap.put("PAUSED", "暂停");

        jobStateMap.put("4", "阻塞");
        jobStateMap.put("2", "完成");
        jobStateMap.put("3", "出错");
        jobStateMap.put("-1", "");
        jobStateMap.put("0", "正常");
        jobStateMap.put("1", "暂停");
    }

    /**
     * scheduleJob
     *
     * @param quartzJob
     */
    public void scheduleJob(QuartzJob quartzJob) throws SchedulerException {
        JobKey jobKey = quartzJob.getJobKey();
        JobDataMap jobDataMap = getJobDataMap(quartzJob.getJobDataMap());
        Class<? extends Job> jobClass = quartzJob.getJobClass();
        String cron = quartzJob.getCronExpression();
        JobDetail jobDetail = getJobDetail(jobKey, jobDataMap, jobClass);
        Trigger trigger = getTrigger(jobKey, jobDataMap, cron);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * deleteJob
     *
     * @param jobKey
     */
    public void deleteJob(JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }


    /**
     * getJobDetail
     *
     * @param jobKey
     * @param jobDataMap
     * @param jobClass
     */
    public JobDetail getJobDetail(JobKey jobKey, JobDataMap jobDataMap, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .setJobData(jobDataMap)
                .usingJobData(jobDataMap)
                .requestRecovery()
                .storeDurably()
                .build();
    }


    /**
     * getTrigger
     *
     * @param jobKey
     * @param jobDataMap
     * @param cronExpression
     */
    public Trigger getTrigger(JobKey jobKey, JobDataMap jobDataMap, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData(jobDataMap)
                .build();
    }


    public JobDataMap getJobDataMap(Map<?, ?> dataMap) {
        return dataMap == null ? new JobDataMap() : new JobDataMap(dataMap);
    }


    /**
     * @param jobName 根据jobName获取job的状态
     */
    public String getTriggerStates(String jobName) throws SchedulerException {
        String jobKeyName = "jobKeyName_" + jobName;
        String jobKeyGroup = "jobKeyGroup_" + jobName;
        JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
        TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
        String state = String.valueOf(scheduler.getTriggerState(triggerKey));
        return jobStateMap.get(state);
    }
}

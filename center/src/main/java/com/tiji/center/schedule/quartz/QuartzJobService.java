package com.tiji.center.schedule.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class QuartzJobService {

    @Autowired
    private Scheduler scheduler;

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

}

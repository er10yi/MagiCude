package com.tiji.center.schedule.quartz;

import org.quartz.Job;
import org.quartz.JobKey;

import java.util.Map;

public class QuartzJob {


    private JobKey jobKey;//job名字和分组名
    private String cronExpression;//cron表达式
    private Map<?, ?> jobDataMap;//元数据
    private Class<? extends Job> jobClass;//执行的类

    public QuartzJob() {
    }

    public QuartzJob(JobKey jobKey, String cronExpression, Map<?, ?> jobDataMap, Class<? extends Job> jobClass) {
        this.jobKey = jobKey;
        this.cronExpression = cronExpression;
        this.jobDataMap = jobDataMap;
        this.jobClass = jobClass;
    }

    public JobKey getJobKey() {
        return jobKey;
    }

    public void setJobKey(JobKey jobKey) {
        this.jobKey = jobKey;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Map<?, ?> getJobDataMap() {
        return jobDataMap;
    }

    public void setJobDataMap(Map<?, ?> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public Class<? extends Job> getJobClass() {
        return jobClass;
    }

    public void setJobClass(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }
}

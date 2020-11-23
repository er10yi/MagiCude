package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * task实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_task")
public class Task implements Serializable {

    @Id
    private String id;//任务编号

    @Transient
    private String statistic;
    @Transient
    private String percentage;
    @Transient
    private String jobstate;

    private String taskparentid;//任务父编号
    private String projectid;//项目编号
    private String name;//任务名称
    private String description;//任务描述
    private String cronexpression;//cron表达式
    private Boolean crontask;//cron任务
    private java.util.Date starttime;//任务开始时间
    private java.util.Date endtime;//任务结束时间
    private String worktype;//任务类型
    private String checktype;//检测类型
    private String threadnumber;//线程数量
    private String singleipscantime;//单个ip扫描次数
    private String additionoption;//任务附加选项
    private String rate;//扫描速率
    private String targetip;//目标ip
    private String targetport;//目标端口，为空为所有端口，regular为nmap默认端口，端口格式:80,443
    private String excludeip;//排除ip
    private String ipslicesize;//分组大小
    private String portslicesize;//端口分组大小，nmap全端口模式时，如果该字段有值，则进行端口分组，分组大小范围：1000-10000
    private Boolean dbipisexcludeip;//db中ip作为排除ip
    private Boolean merge2asset;//扫描结果合并到资产

    public Task() {
    }

    public Task(String id, String taskparentid, String projectid, String name, String description, String cronexpression, Boolean crontask, Date starttime, Date endtime, String worktype, String checktype, String threadnumber, String singleipscantime, String additionoption, String rate, String targetip, String targetport, String excludeip, String ipslicesize, String portslicesize, Boolean dbipisexcludeip, Boolean merge2asset) {
        this.id = id;
        this.taskparentid = taskparentid;
        this.projectid = projectid;
        this.name = name;
        this.description = description;
        this.cronexpression = cronexpression;
        this.crontask = crontask;
        this.starttime = starttime;
        this.endtime = endtime;
        this.worktype = worktype;
        this.checktype = checktype;
        this.threadnumber = threadnumber;
        this.singleipscantime = singleipscantime;
        this.additionoption = additionoption;
        this.rate = rate;
        this.targetip = targetip;
        this.targetport = targetport;
        this.excludeip = excludeip;
        this.ipslicesize = ipslicesize;
        this.portslicesize = portslicesize;
        this.dbipisexcludeip = dbipisexcludeip;
        this.merge2asset = merge2asset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    public String getJobstate() {
        return jobstate;
    }

    public void setJobstate(String jobstate) {
        this.jobstate = jobstate;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getTaskparentid() {
        return taskparentid;
    }

    public void setTaskparentid(String taskparentid) {
        this.taskparentid = taskparentid;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronexpression() {
        return cronexpression;
    }

    public void setCronexpression(String cronexpression) {
        this.cronexpression = cronexpression;
    }

    public Boolean getCrontask() {
        return crontask;
    }

    public void setCrontask(Boolean crontask) {
        this.crontask = crontask;
    }

    public java.util.Date getStarttime() {
        return starttime;
    }

    public void setStarttime(java.util.Date starttime) {
        this.starttime = starttime;
    }

    public java.util.Date getEndtime() {
        return endtime;
    }

    public void setEndtime(java.util.Date endtime) {
        this.endtime = endtime;
    }

    public String getWorktype() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }

    public String getChecktype() {
        return checktype;
    }

    public void setChecktype(String checktype) {
        this.checktype = checktype;
    }

    public String getThreadnumber() {
        return threadnumber;
    }

    public void setThreadnumber(String threadnumber) {
        this.threadnumber = threadnumber;
    }

    public String getSingleipscantime() {
        return singleipscantime;
    }

    public void setSingleipscantime(String singleipscantime) {
        this.singleipscantime = singleipscantime;
    }

    public String getAdditionoption() {
        return additionoption;
    }

    public void setAdditionoption(String additionoption) {
        this.additionoption = additionoption;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getTargetip() {
        return targetip;
    }

    public void setTargetip(String targetip) {
        this.targetip = targetip;
    }

    public String getTargetport() {
        return targetport;
    }

    public void setTargetport(String targetport) {
        this.targetport = targetport;
    }

    public String getExcludeip() {
        return excludeip;
    }

    public void setExcludeip(String excludeip) {
        this.excludeip = excludeip;
    }

    public String getIpslicesize() {
        return ipslicesize;
    }

    public void setIpslicesize(String ipslicesize) {
        this.ipslicesize = ipslicesize;
    }

    public String getPortslicesize() {
        return portslicesize;
    }

    public void setPortslicesize(String portslicesize) {
        this.portslicesize = portslicesize;
    }

    public Boolean getDbipisexcludeip() {
        return dbipisexcludeip;
    }

    public void setDbipisexcludeip(Boolean dbipisexcludeip) {
        this.dbipisexcludeip = dbipisexcludeip;
    }

    public Boolean getMerge2asset() {
        return merge2asset;
    }

    public void setMerge2asset(Boolean merge2asset) {
        this.merge2asset = merge2asset;
    }


}

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * nmapconfig实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_nmapconfig")
public class Nmapconfig implements Serializable {

    @Id
    private String id;//nmap配置编号


    private String taskid;//任务编号
    private String threadnumber;//线程数量，在mass2Nmap模式下使用
    private String singleipscantime;//单个ip扫描次数，在mass2Nmap模式下使用
    private String additionoption;//附加选项，在mass2Nmap模式下使用


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
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


}

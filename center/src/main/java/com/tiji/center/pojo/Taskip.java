package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * taskip实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_taskip")
public class Taskip implements Serializable {

    @Id
    private String id;//资产ip编号


    private String taskid;//任务编号
    private String ipaddressv4;//ip地址
    private String ipaddressv6;//ipaddressv6
    private Boolean checkwhitelist;//安全检测白名单

    public Taskip() {
    }

    public Taskip(String id, String taskid, String ipaddressv4, String ipaddressv6, Boolean checkwhitelist) {
        this.id = id;
        this.taskid = taskid;
        this.ipaddressv4 = ipaddressv4;
        this.ipaddressv6 = ipaddressv6;
        this.checkwhitelist = checkwhitelist;
    }

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

    public String getIpaddressv4() {
        return ipaddressv4;
    }

    public void setIpaddressv4(String ipaddressv4) {
        this.ipaddressv4 = ipaddressv4;
    }

    public String getIpaddressv6() {
        return ipaddressv6;
    }

    public void setIpaddressv6(String ipaddressv6) {
        this.ipaddressv6 = ipaddressv6;
    }

    public Boolean getCheckwhitelist() {
        return checkwhitelist;
    }

    public void setCheckwhitelist(Boolean checkwhitelist) {
        this.checkwhitelist = checkwhitelist;
    }


}

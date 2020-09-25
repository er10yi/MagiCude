package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * ipwhitelist实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_ipwhitelist")
public class Ipwhitelist implements Serializable {

    @Id
    private String id;//编号


    private String ip;//ip
    private Boolean checkwhitelist;//检测白名单
    private Boolean notifywhitelist;//提醒白名单

    public Ipwhitelist() {
    }

    public Ipwhitelist(String id, String ip, Boolean checkwhitelist, Boolean notifywhitelist) {
        this.id = id;
        this.ip = ip;
        this.checkwhitelist = checkwhitelist;
        this.notifywhitelist = notifywhitelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getCheckwhitelist() {
        return checkwhitelist;
    }

    public void setCheckwhitelist(Boolean checkwhitelist) {
        this.checkwhitelist = checkwhitelist;
    }

    public Boolean getNotifywhitelist() {
        return notifywhitelist;
    }

    public void setNotifywhitelist(Boolean notifywhitelist) {
        this.notifywhitelist = notifywhitelist;
    }


}

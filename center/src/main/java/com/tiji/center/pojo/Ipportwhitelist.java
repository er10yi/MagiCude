package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * ipportwhitelist实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_ipportwhitelist")
public class Ipportwhitelist implements Serializable {

    @Id
    private String id;//编号


    private String ipwhitelistid;//ip白名单编号
    private String port;//端口
    private Boolean checkwhitelist;//检测白名单
    private Boolean notifywhitelist;//提醒白名单

    public Ipportwhitelist() {
    }

    public Ipportwhitelist(String id, String ipwhitelistid, String port, Boolean checkwhitelist, Boolean notifywhitelist) {
        this.id = id;
        this.ipwhitelistid = ipwhitelistid;
        this.port = port;
        this.checkwhitelist = checkwhitelist;
        this.notifywhitelist = notifywhitelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpwhitelistid() {
        return ipwhitelistid;
    }

    public void setIpwhitelistid(String ipwhitelistid) {
        this.ipwhitelistid = ipwhitelistid;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * projectportwhitelist实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_projectportwhitelist")
public class Projectportwhitelist implements Serializable {

    @Id
    private String id;//编号


    private String projectinfoid;//项目信息编号
    private String port;//端口
    private Boolean checkwhitelist;//检测白名单
    private Boolean notifywhitelist;//提醒白名单

    public Projectportwhitelist() {
    }

    public Projectportwhitelist(String id, String projectinfoid, String port, Boolean checkwhitelist, Boolean notifywhitelist) {
        this.id = id;
        this.projectinfoid = projectinfoid;
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

    public String getProjectinfoid() {
        return projectinfoid;
    }

    public void setProjectinfoid(String projectinfoid) {
        this.projectinfoid = projectinfoid;
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

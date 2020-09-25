package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * taskport实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_taskport")
public class Taskport implements Serializable {

    @Id
    private String id;//端口编号


    private String taskipid;//资产ip编号
    private String port;//端口
    private String protocol;//端口协议
    private String state;//端口开放状态
    private String service;//端口服务
    private String version;//服务版本
    private Boolean checkwhitelist;//安全检测白名单

    public Taskport() {
    }

    public Taskport(String id, String taskipid, String port, String protocol, String state, String service, String version, Boolean checkwhitelist) {
        this.id = id;
        this.taskipid = taskipid;
        this.port = port;
        this.protocol = protocol;
        this.state = state;
        this.service = service;
        this.version = version;
        this.checkwhitelist = checkwhitelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskipid() {
        return taskipid;
    }

    public void setTaskipid(String taskipid) {
        this.taskipid = taskipid;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getCheckwhitelist() {
        return checkwhitelist;
    }

    public void setCheckwhitelist(Boolean checkwhitelist) {
        this.checkwhitelist = checkwhitelist;
    }
}

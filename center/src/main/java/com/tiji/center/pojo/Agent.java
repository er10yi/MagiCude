package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * agent实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_agent")
public class Agent implements Serializable {
    @Id
    private String id;//agent编号


    private String name;//agent名称
    private String nmappath;//nmap路径
    private String masspath;//mass路径
    private String ipaddress;//ip地址
    private Boolean online;//在线
    private String timeouts;//超时次数

    public Agent() {
    }

    public Agent(String id, String name, String nmappath, String masspath, String ipaddress, Boolean online, String timeouts) {
        this.id = id;
        this.name = name;
        this.nmappath = nmappath;
        this.masspath = masspath;
        this.ipaddress = ipaddress;
        this.online = online;
        this.timeouts = timeouts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNmappath() {
        return nmappath;
    }

    public void setNmappath(String nmappath) {
        this.nmappath = nmappath;
    }

    public String getMasspath() {
        return masspath;
    }

    public void setMasspath(String masspath) {
        this.masspath = masspath;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(String timeouts) {
        this.timeouts = timeouts;
    }


}

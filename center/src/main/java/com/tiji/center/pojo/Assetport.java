package com.tiji.center.pojo;

import com.tiji.center.pojo.category.CategoryTab;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * assetport实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_assetport")
public class Assetport implements Serializable {

    @Id
    private String id;//端口编号

    @Transient
    private String statistic;

    private String assetipid;//资产ip编号
    private String port;//端口
    private String protocol;//端口协议
    private String state;//端口开放状态
    private String service;//端口服务
    private String version;//服务版本
    private Boolean checkwhitelist;//安全检测白名单
    private Boolean assetnotifywhitelist;//资产提醒白名单
    private java.util.Date uptime;//端口发现时间
    private java.util.Date downtime;//端口关闭时间
    private java.util.Date changedtime;//修改时间
    //标签bitmap
    private String tabbitmap;
    //标签名
    @Transient
    private String tabname;
    //标签
    @Transient
    private List<CategoryTab> tabList;
    public Assetport() {
    }
    @Transient
    //应用系统名称
    private String appsysname;
    public Assetport(String id, String assetipid, String port, String protocol, String state, String service, String version, Boolean checkwhitelist, Boolean assetnotifywhitelist, Date uptime, Date downtime, Date changedtime) {
        this.id = id;
        this.assetipid = assetipid;
        this.port = port;
        this.protocol = protocol;
        this.state = state;
        this.service = service;
        this.version = version;
        this.checkwhitelist = checkwhitelist;
        this.assetnotifywhitelist = assetnotifywhitelist;
        this.uptime = uptime;
        this.downtime = downtime;
        this.changedtime = changedtime;
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

    public String getAssetipid() {
        return assetipid;
    }

    public void setAssetipid(String assetipid) {
        this.assetipid = assetipid;
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

    public Boolean getAssetnotifywhitelist() {
        return assetnotifywhitelist;
    }

    public void setAssetnotifywhitelist(Boolean assetnotifywhitelist) {
        this.assetnotifywhitelist = assetnotifywhitelist;
    }

    public Date getUptime() {
        return uptime;
    }

    public void setUptime(Date uptime) {
        this.uptime = uptime;
    }

    public Date getDowntime() {
        return downtime;
    }

    public void setDowntime(Date downtime) {
        this.downtime = downtime;
    }

    public Date getChangedtime() {
        return changedtime;
    }

    public void setChangedtime(Date changedtime) {
        this.changedtime = changedtime;
    }

    public String getTabbitmap() {
        return tabbitmap;
    }

    public void setTabbitmap(String tabbitmap) {
        this.tabbitmap = tabbitmap;
    }

    public String getTabname() {
        return tabname;
    }

    public void setTabname(String tabname) {
        this.tabname = tabname;
    }

    public List<CategoryTab> getTabList() {
        return tabList;
    }

    public void setTabList(List<CategoryTab> tabList) {
        this.tabList = tabList;
    }

    public String getAppsysname() {
        return appsysname;
    }

    public void setAppsysname(String appsysname) {
        this.appsysname = appsysname;
    }
}

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * webinfo实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_webinfo")
public class Webinfo implements Serializable {

    @Id
    private String id;//web信息编号


    @Transient
    private String assetip;
    @Transient
    private String url;

    @Transient
    private String assetport;//端口

    @Transient
    private String header;
    @Transient
    private String response;


    private String portid;//端口编号
    private String titlewhitelistid;//标题白名单编号
    private String title;//页面标题
    private String bodychildrenstextcontent;//body子节点文本内容
    private String server;//响应头中的服务
    private String xpoweredby;//xpoweredby
    private String setcookie;//设置cookie
    private String wwwauthenticate;//认证方式
    private String appname;//appname
    private String appversion;//应用版本
    private String devlanguage;//devlanguage
    private java.util.Date crawltime;//页面抓取时间

    public Webinfo() {
    }

    public Webinfo(String id, String portid, String titlewhitelistid, String title, String bodychildrenstextcontent, String server, String xpoweredby, String setcookie, String wwwauthenticate, String appname, String appversion, String devlanguage, Date crawltime) {
        this.id = id;
        this.portid = portid;
        this.titlewhitelistid = titlewhitelistid;
        this.title = title;
        this.bodychildrenstextcontent = bodychildrenstextcontent;
        this.server = server;
        this.xpoweredby = xpoweredby;
        this.setcookie = setcookie;
        this.wwwauthenticate = wwwauthenticate;
        this.appname = appname;
        this.appversion = appversion;
        this.devlanguage = devlanguage;
        this.crawltime = crawltime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetip() {
        return assetip;
    }

    public void setAssetip(String assetip) {
        this.assetip = assetip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAssetport() {
        return assetport;
    }

    public void setAssetport(String assetport) {
        this.assetport = assetport;
    }

    public String getPortid() {
        return portid;
    }

    public void setPortid(String portid) {
        this.portid = portid;
    }

    public String getTitlewhitelistid() {
        return titlewhitelistid;
    }

    public void setTitlewhitelistid(String titlewhitelistid) {
        this.titlewhitelistid = titlewhitelistid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodychildrenstextcontent() {
        return bodychildrenstextcontent;
    }

    public void setBodychildrenstextcontent(String bodychildrenstextcontent) {
        this.bodychildrenstextcontent = bodychildrenstextcontent;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getXpoweredby() {
        return xpoweredby;
    }

    public void setXpoweredby(String xpoweredby) {
        this.xpoweredby = xpoweredby;
    }

    public String getSetcookie() {
        return setcookie;
    }

    public void setSetcookie(String setcookie) {
        this.setcookie = setcookie;
    }

    public String getWwwauthenticate() {
        return wwwauthenticate;
    }

    public void setWwwauthenticate(String wwwauthenticate) {
        this.wwwauthenticate = wwwauthenticate;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getDevlanguage() {
        return devlanguage;
    }

    public void setDevlanguage(String devlanguage) {
        this.devlanguage = devlanguage;
    }

    public java.util.Date getCrawltime() {
        return crawltime;
    }

    public void setCrawltime(java.util.Date crawltime) {
        this.crawltime = crawltime;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

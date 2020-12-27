package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * host实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_host")
public class Host implements Serializable {

    @Id
    private String id;//主机编号


    private String assetipid;//资产ip编号
    private String macaddress;//mac地址
    private String hostname;//主机名
    private String subdomain;//子域名
    private String ostype;//操作系统类型
    private String osversion;//操作系统版本
    private String type;//主机类型
    private String owner;//主机所有者
    private java.util.Date activetime;//主机发现时间
    private String remark;//备注，标记非dns反向解析

    @Transient
    //应用系统名称
    private String appsysname;


    public Host() {
    }

    public Host(String id, String assetipid, String macaddress, String hostname, String ostype, String osversion, String type, String owner, Date activetime, String remark) {
        this.id = id;
        this.assetipid = assetipid;
        this.macaddress = macaddress;
        this.hostname = hostname;
        this.ostype = ostype;
        this.osversion = osversion;
        this.type = type;
        this.owner = owner;
        this.activetime = activetime;
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetipid() {
        return assetipid;
    }

    public void setAssetipid(String assetipid) {
        this.assetipid = assetipid;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getOstype() {
        return ostype;
    }

    public void setOstype(String ostype) {
        this.ostype = ostype;
    }

    public String getOsversion() {
        return osversion;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public java.util.Date getActivetime() {
        return activetime;
    }

    public void setActivetime(java.util.Date activetime) {
        this.activetime = activetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAppsysname() {
        return appsysname;
    }

    public void setAppsysname(String appsysname) {
        this.appsysname = appsysname;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }
}

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * checkresult实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_checkresult")
public class Checkresult implements Serializable {

    @Id
    private String id;//检测结果编号



    @Transient
    private String assetip;
    @Transient
    private String vulname;
    @Transient
    private String assetport;//端口

    private String assetportid;//端口编号
    private String name;//检测结果名称
    private String result;//检测结果
    private String risk;//缺陷风险级别
    private java.util.Date activetime;//缺陷发现时间
    private java.util.Date passivetime;//缺陷修复时间
    private String remark;//备注

    public Checkresult() {
    }

    public Checkresult(String id, String assetportid, String name, String result, String risk, Date activetime, Date passivetime, String remark) {
        this.id = id;
        this.assetportid = assetportid;
        this.name = name;
        this.result = result;
        this.risk = risk;
        this.activetime = activetime;
        this.passivetime = passivetime;
        this.remark = remark;
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

    public String getVulname() {
        return vulname;
    }

    public String getAssetport() {
        return assetport;
    }

    public void setAssetport(String assetport) {
        this.assetport = assetport;
    }

    public void setVulname(String vulname) {
        this.vulname = vulname;
    }

    public String getAssetportid() {
        return assetportid;
    }

    public void setAssetportid(String assetportid) {
        this.assetportid = assetportid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public Date getActivetime() {
        return activetime;
    }

    public void setActivetime(Date activetime) {
        this.activetime = activetime;
    }

    public Date getPassivetime() {
        return passivetime;
    }

    public void setPassivetime(Date passivetime) {
        this.passivetime = passivetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}


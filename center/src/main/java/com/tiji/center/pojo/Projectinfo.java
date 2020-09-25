package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * projectinfo实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_projectinfo")
public class Projectinfo implements Serializable {

    @Id
    private String id;//编号


    private String departmentid;//部门编号
    private String projectname;//项目名称
    private Boolean checkwhitelist;//检测白名单
    private Boolean notifywhitelist;//提醒白名单
    private java.util.Date inserttime;//插入时间
    private Boolean overrideipwhitelist;//覆盖ip白名单，默认为false，如果为true，则会对项目下所有的ip进行白名单

    public Projectinfo() {
    }

    public Projectinfo(String id, String departmentid, String projectname, Boolean checkwhitelist, Boolean notifywhitelist, Date inserttime, Boolean overrideipwhitelist) {
        this.id = id;
        this.departmentid = departmentid;
        this.projectname = projectname;
        this.checkwhitelist = checkwhitelist;
        this.notifywhitelist = notifywhitelist;
        this.inserttime = inserttime;
        this.overrideipwhitelist = overrideipwhitelist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(String departmentid) {
        this.departmentid = departmentid;
    }

    public String getProjectname() {
        return projectname;
    }

    public void setProjectname(String projectname) {
        this.projectname = projectname;
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

    public java.util.Date getInserttime() {
        return inserttime;
    }

    public void setInserttime(java.util.Date inserttime) {
        this.inserttime = inserttime;
    }

    public Boolean getOverrideipwhitelist() {
        return overrideipwhitelist;
    }

    public void setOverrideipwhitelist(Boolean overrideipwhitelist) {
        this.overrideipwhitelist = overrideipwhitelist;
    }
}

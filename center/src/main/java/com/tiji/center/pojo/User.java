package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * user实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_user")
public class User implements Serializable {

    @Id
    private String id;//用户编号


    private String username;//用户名
    private String password;//密码
    private Boolean admin;//是否管理员
    private Boolean active;//是否有效
    private String avatar;//头像地址
    private java.util.Date lastdate;//最后登录时间


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public java.util.Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(java.util.Date lastdate) {
        this.lastdate = lastdate;
    }

}

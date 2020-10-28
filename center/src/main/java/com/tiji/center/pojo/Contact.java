package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * contact实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_contact")
public class Contact implements Serializable {

    @Id
    private String id;//编号


    @Transient
    private String projectinfoid;
    private String name;//联系人
    private String email;//邮箱
    private String phone;//电话，座机或手机

    public Contact() {
    }

    public Contact(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}

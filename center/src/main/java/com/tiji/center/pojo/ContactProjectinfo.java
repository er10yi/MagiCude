package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * contactProjectinfo实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_contact_projectinfo")
public class ContactProjectinfo implements Serializable {

    @Id
    private String id;//编号


    private String contactid;//联系编号
    private String projectinfoid;//项目信息编号

    public ContactProjectinfo() {
    }

    public ContactProjectinfo(String id, String contactid, String projectinfoid) {
        this.id = id;
        this.contactid = contactid;
        this.projectinfoid = projectinfoid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContactid() {
        return contactid;
    }

    public void setContactid(String contactid) {
        this.contactid = contactid;
    }

    public String getProjectinfoid() {
        return projectinfoid;
    }

    public void setProjectinfoid(String projectinfoid) {
        this.projectinfoid = projectinfoid;
    }


}

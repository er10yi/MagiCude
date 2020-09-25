package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * titlewhitelist实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_titlewhitelist")
public class Titlewhitelist implements Serializable {

    @Id
    private String id;//参数编号


    private String title;//标题

    public Titlewhitelist() {
    }

    public Titlewhitelist(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * cronjob实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_cronjob")
public class Cronjob implements Serializable {

    @Id
    private String id;//编号


    private String name;//名称
    private String cronexpression;//cron表达式


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

    public String getCronexpression() {
        return cronexpression;
    }

    public void setCronexpression(String cronexpression) {
        this.cronexpression = cronexpression;
    }


}

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * project实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_project")
public class Project implements Serializable {

    @Id
    private String id;//项目编号


    private String name;//项目名称
    private String description;//项目描述


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}

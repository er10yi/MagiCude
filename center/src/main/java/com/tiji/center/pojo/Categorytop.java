package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * categorytop实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_categorytop")
public class Categorytop implements Serializable {

    @Id
    private String id;//漏洞一级分类编号


    private String name;//漏洞一级分类名称

    public Categorytop() {
    }

    public Categorytop(String id, String name) {
        this.id = id;
        this.name = name;
    }

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


}

package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * categorysecond实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_categorysecond")
public class Categorysecond implements Serializable {

    @Id
    private String id;//漏洞二级分类编号


    private String categorytopid;//漏洞一级分类编号
    private String name;//漏洞二级分类类型

    public Categorysecond() {
    }

    public Categorysecond(String id, String categorytopid, String name) {
        this.id = id;
        this.categorytopid = categorytopid;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategorytopid() {
        return categorytopid;
    }

    public void setCategorytopid(String categorytopid) {
        this.categorytopid = categorytopid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

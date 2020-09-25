package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * url实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_url")
public class Url implements Serializable {

    @Id
    private String id;//url编号


    private String webinfoid;//web信息编号
    private String name;//名称
    private String url;//url

    public Url() {
    }

    public Url(String id, String webinfoid, String name, String url) {
        this.id = id;
        this.webinfoid = webinfoid;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebinfoid() {
        return webinfoid;
    }

    public void setWebinfoid(String webinfoid) {
        this.webinfoid = webinfoid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}

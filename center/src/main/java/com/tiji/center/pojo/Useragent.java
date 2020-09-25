package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * useragent实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_useragent")
public class Useragent implements Serializable {

    @Id
    private String id;//编号


    private String useragent;//useragent

    public Useragent() {
    }

    public Useragent(String id, String useragent) {
        this.id = id;
        this.useragent = useragent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }


}

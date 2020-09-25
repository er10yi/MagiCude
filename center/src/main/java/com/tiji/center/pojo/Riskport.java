package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * riskport实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_riskport")
public class Riskport implements Serializable {

    @Id
    private String id;//编号


    private String port;//高危端口

    public Riskport() {

    }

    public Riskport(String id, String port) {
        this.id = id;
        this.port = port;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


}

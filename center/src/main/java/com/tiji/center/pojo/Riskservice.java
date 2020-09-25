package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * riskservice实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_riskservice")
public class Riskservice implements Serializable {

    @Id
    private String id;//编号


    private String service;//高危服务

    public Riskservice() {
    }

    public Riskservice(String id, String service) {
        this.id = id;
        this.service = service;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }


}

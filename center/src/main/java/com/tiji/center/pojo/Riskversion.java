package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * riskversion实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_riskversion")
public class Riskversion implements Serializable {

    @Id
    private String id;//编号


    private String version;//高危版本

    public Riskversion() {
    }

    public Riskversion(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


}

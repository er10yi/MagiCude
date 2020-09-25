package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * projectvulnnotify实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_projectvulnnotify")
public class Projectvulnnotify implements Serializable {

    @Id
    private String id;//编号


    private String risk;//风险等级


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }


}

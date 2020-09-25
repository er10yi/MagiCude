package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * domainwhitelist实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_domainwhitelist")
public class Domainwhitelist implements Serializable {

    @Id
    private String id;//参数编号


    private String domain;//域名

    public Domainwhitelist() {
    }

    public Domainwhitelist(String id, String domain) {
        this.id = id;
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


}

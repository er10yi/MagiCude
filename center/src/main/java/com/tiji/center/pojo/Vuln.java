package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * vuln实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_vuln")
public class Vuln implements Serializable {

    @Id
    private String id;//漏洞编号


    private String categorysecondid;//漏洞二级分类编号
    private String name;//漏洞名称
    private String description;//漏洞描述
    private String risk;//漏洞风险级别
    private String refer;//参考
    private String impactscope;//impactscope

    public Vuln() {
    }

    public Vuln(String id, String categorysecondid, String name, String description, String risk, String refer, String impactscope) {
        this.id = id;
        this.categorysecondid = categorysecondid;
        this.name = name;
        this.description = description;
        this.risk = risk;
        this.refer = refer;
        this.impactscope = impactscope;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategorysecondid() {
        return categorysecondid;
    }

    public void setCategorysecondid(String categorysecondid) {
        this.categorysecondid = categorysecondid;
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

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getImpactscope() {
        return impactscope;
    }

    public void setImpactscope(String impactscope) {
        this.impactscope = impactscope;
    }


}

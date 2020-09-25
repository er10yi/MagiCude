package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * solution实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_solution")
public class Solution implements Serializable {

    @Id
    private String id;//修复方案编号


    private String vulnid;//漏洞编号
    private String solution;//修复方案
    private String codedemo;//修复代码示例
    private String configdemo;//修复配置示例

    public Solution() {
    }

    public Solution(String id, String vulnid, String solution, String codedemo, String configdemo) {
        this.id = id;
        this.vulnid = vulnid;
        this.solution = solution;
        this.codedemo = codedemo;
        this.configdemo = configdemo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVulnid() {
        return vulnid;
    }

    public void setVulnid(String vulnid) {
        this.vulnid = vulnid;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getCodedemo() {
        return codedemo;
    }

    public void setCodedemo(String codedemo) {
        this.codedemo = codedemo;
    }

    public String getConfigdemo() {
        return configdemo;
    }

    public void setConfigdemo(String configdemo) {
        this.configdemo = configdemo;
    }


}

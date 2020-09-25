package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * democode实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_democode")
public class Democode implements Serializable {

    @Id
    private String id;//漏洞示例代码编号


    private String vulnid;//漏洞编号
    private String democode;//漏洞示例代码
    private String poc;//漏洞poc

    public Democode() {
    }

    public Democode(String id, String vulnid, String democode, String poc) {
        this.id = id;
        this.vulnid = vulnid;
        this.democode = democode;
        this.poc = poc;
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

    public String getDemocode() {
        return democode;
    }

    public void setDemocode(String democode) {
        this.democode = democode;
    }

    public String getPoc() {
        return poc;
    }

    public void setPoc(String poc) {
        this.poc = poc;
    }


}

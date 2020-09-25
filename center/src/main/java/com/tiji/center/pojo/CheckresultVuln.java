package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * checkresultVuln实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_checkresult_vuln")
public class CheckresultVuln implements Serializable {

    @Id
    private String id;//编号


    private String checkresultid;//检测结果编号
    private String vulnid;//漏洞编号

    public CheckresultVuln() {
    }

    public CheckresultVuln(String id, String checkresultid, String vulnid) {
        this.id = id;
        this.checkresultid = checkresultid;
        this.vulnid = vulnid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckresultid() {
        return checkresultid;
    }

    public void setCheckresultid(String checkresultid) {
        this.checkresultid = checkresultid;
    }

    public String getVulnid() {
        return vulnid;
    }

    public void setVulnid(String vulnid) {
        this.vulnid = vulnid;
    }


}

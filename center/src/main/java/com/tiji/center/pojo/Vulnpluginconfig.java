package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * vulnpluginconfig实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_vulnpluginconfig")
public class Vulnpluginconfig implements Serializable {

    @Id
    private String id;//编号


    private String vulnid;//漏洞编号
    private String pluginconfigid;//插件配置编号

    public Vulnpluginconfig() {
    }

    public Vulnpluginconfig(String id, String vulnid, String pluginconfigid) {
        this.id = id;
        this.vulnid = vulnid;
        this.pluginconfigid = pluginconfigid;
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

    public String getPluginconfigid() {
        return pluginconfigid;
    }

    public void setPluginconfigid(String pluginconfigid) {
        this.pluginconfigid = pluginconfigid;
    }


}

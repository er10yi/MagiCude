package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * vulnkeyword实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_vulnkeyword")
public class Vulnkeyword implements Serializable {

    @Id
    private String id;//漏洞关键字编号


    private String pluginconfigid;//插件配置编号
    private String keyword;//漏洞关键字

    public Vulnkeyword() {
    }

    public Vulnkeyword(String id, String pluginconfigid, String keyword) {
        this.id = id;
        this.pluginconfigid = pluginconfigid;
        this.keyword = keyword;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPluginconfigid() {
        return pluginconfigid;
    }

    public void setPluginconfigid(String pluginconfigid) {
        this.pluginconfigid = pluginconfigid;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


}

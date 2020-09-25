package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * assetservice实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_pluginassetservice")
public class Pluginassetservice implements Serializable {

    @Id
    private String id;//资产服务编号


    private String pluginconfigid;//插件配置编号
    private String assetservice;//资产服务

    public Pluginassetservice() {
    }

    public Pluginassetservice(String id, String pluginconfigid, String assetservice) {
        this.id = id;
        this.pluginconfigid = pluginconfigid;
        this.assetservice = assetservice;
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

    public String getAssetservice() {
        return assetservice;
    }

    public void setAssetservice(String assetservice) {
        this.assetservice = assetservice;
    }


}

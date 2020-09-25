package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * assetversion实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_pluginassetversion")
public class Pluginassetversion implements Serializable {

    @Id
    private String id;//资产版本编号


    private String pluginconfigid;//插件配置编号
    private String assetversion;//资产版本

    public Pluginassetversion() {
    }

    public Pluginassetversion(String id, String pluginconfigid, String assetversion) {
        this.id = id;
        this.pluginconfigid = pluginconfigid;
        this.assetversion = assetversion;
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

    public String getAssetversion() {
        return assetversion;
    }

    public void setAssetversion(String assetversion) {
        this.assetversion = assetversion;
    }


}

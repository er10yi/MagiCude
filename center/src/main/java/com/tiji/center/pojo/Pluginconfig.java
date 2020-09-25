package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * pluginconfig实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_pluginconfig")
public class Pluginconfig implements Serializable {

    @Id
    private String id;//插件配置编号


    private String name;//插件名称
    private String args;//插件参数
    private String risk;//插件风险级别
    private String type;//插件类型：nse或者自定义
    private String validatetype;//http辅助验证或dns辅助验证
    private String timeout;//插件超时
    private String plugincode;//插件代码

    public Pluginconfig() {
    }

    public Pluginconfig(String id, String name, String args, String risk, String type, String validatetype, String timeout, String plugincode) {
        this.id = id;
        this.name = name;
        this.args = args;
        this.risk = risk;
        this.type = type;
        this.validatetype = validatetype;
        this.timeout = timeout;
        this.plugincode = plugincode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getPlugincode() {
        return plugincode;
    }

    public void setPlugincode(String plugincode) {
        this.plugincode = plugincode;
    }

    public String getValidatetype() {
        return validatetype;
    }

    public void setValidatetype(String validatetype) {
        this.validatetype = validatetype;
    }
}

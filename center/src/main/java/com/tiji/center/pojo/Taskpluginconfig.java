package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * taskpluginconfig实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_taskpluginconfig")
public class Taskpluginconfig implements Serializable {

    @Id
    private String id;//编号


    private String taskid;//任务编号
    private String pluginconfigid;//插件编号

    public Taskpluginconfig() {
    }

    public Taskpluginconfig(String id, String taskid, String pluginconfigid) {
        this.id = id;
        this.taskid = taskid;
        this.pluginconfigid = pluginconfigid;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getPluginconfigid() {
        return pluginconfigid;
    }

    public void setPluginconfigid(String pluginconfigid) {
        this.pluginconfigid = pluginconfigid;
    }


}

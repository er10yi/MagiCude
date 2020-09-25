package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * department实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_department")
public class Department implements Serializable {

    @Id
    private String id;//编号


    private String departmentname;//部门名称

    public Department() {
    }

    public Department(String id, String departmentname) {
        this.id = id;
        this.departmentname = departmentname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }


}

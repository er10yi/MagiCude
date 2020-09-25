package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * dictionarypassword实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_dictionarypassword")
public class Dictionarypassword implements Serializable {

    @Id
    private String id;//字典编号


    private String password;//字典密码

    public Dictionarypassword() {
    }

    public Dictionarypassword(String id, String password) {
        this.id = id;
        this.password = password;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}

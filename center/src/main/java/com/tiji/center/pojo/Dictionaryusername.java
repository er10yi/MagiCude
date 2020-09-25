package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * dictionaryusername实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_dictionaryusername")
public class Dictionaryusername implements Serializable {

    @Id
    private String id;//字典编号


    private String username;//字典用户名

    public Dictionaryusername() {
    }

    public Dictionaryusername(String id, String username) {
        this.id = id;
        this.username = username;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

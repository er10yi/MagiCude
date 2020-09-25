package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * notifylog实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_notifylog")
public class Notifylog implements Serializable {

    @Id
    private String id;//编号


    private String type;//类型
    private String recipient;//接收人
    private String receiveuser;//接收账户
    private String content;//内容
    private Boolean success;//发送成功
    private String exception;//异常消息
    private java.util.Date sendtime;//发送时间

    public Notifylog() {
    }

    public Notifylog(String id, String type, String recipient, String receiveuser, String content, Boolean success, String exception, Date sendtime) {
        this.id = id;
        this.type = type;
        this.recipient = recipient;
        this.receiveuser = receiveuser;
        this.content = content;
        this.success = success;
        this.exception = exception;
        this.sendtime = sendtime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getReceiveuser() {
        return receiveuser;
    }

    public void setReceiveuser(String receiveuser) {
        this.receiveuser = receiveuser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public java.util.Date getSendtime() {
        return sendtime;
    }

    public void setSendtime(java.util.Date sendtime) {
        this.sendtime = sendtime;
    }


}

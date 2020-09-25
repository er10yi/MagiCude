package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * imvulnnotify实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_imvulnnotify")
public class Imvulnnotify implements Serializable {

    @Id
    private String id;//编号


    private Boolean notify;//是否开启通知
    private Boolean notifyall;//是否提醒所有人
    private String secret;//秘钥
    private String risk;//风险等级
    private String receiver;//接收人列表
    private String messageurl;//消息地址
    private String messagetitle;//消息标题
    private String messageprefix;//消息前缀
    private String messagesuffix;//消息后缀
    private String messagecharset;//消息编码

    public Boolean getNotify() {
        return notify;
    }

    public void setNotify(Boolean notify) {
        this.notify = notify;
    }

    public Boolean getNotifyall() {
        return notifyall;
    }

    public void setNotifyall(Boolean notifyall) {
        this.notifyall = notifyall;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageurl() {
        return messageurl;
    }

    public void setMessageurl(String messageurl) {
        this.messageurl = messageurl;
    }

    public String getMessagetitle() {
        return messagetitle;
    }

    public void setMessagetitle(String messagetitle) {
        this.messagetitle = messagetitle;
    }

    public String getMessageprefix() {
        return messageprefix;
    }

    public void setMessageprefix(String messageprefix) {
        this.messageprefix = messageprefix;
    }

    public String getMessagesuffix() {
        return messagesuffix;
    }

    public void setMessagesuffix(String messagesuffix) {
        this.messagesuffix = messagesuffix;
    }

    public String getMessagecharset() {
        return messagecharset;
    }

    public void setMessagecharset(String messagecharset) {
        this.messagecharset = messagecharset;
    }


}

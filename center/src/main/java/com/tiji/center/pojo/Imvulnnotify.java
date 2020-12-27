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

    private Boolean dingtalknotify;//是否开启通知
    private Boolean dingtalknotifyall;//是否提醒所有人
    private String secret;//秘钥
    private String dingtalkmessageurl;//消息地址
    private String dingtalkreceiver;//接收人列表

    private Boolean wechatnotify;//是否开启通知
    private Boolean wechatnotifyall;//是否提醒所有人
    private String wechatmessageurl;//消息地址
    private String wechatreceiver;//接收人列表

    private Boolean riskassetnotify;//新增高危资产是否实时推送到群

    private String risk;//风险等级
    private String messagetitle;//消息标题
    private String messageprefix;//消息前缀
    private String messagesuffix;//消息后缀
    private String messagecharset;//消息编码

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getDingtalknotify() {
        return dingtalknotify;
    }

    public void setDingtalknotify(Boolean dingtalknotify) {
        this.dingtalknotify = dingtalknotify;
    }

    public Boolean getDingtalknotifyall() {
        return dingtalknotifyall;
    }

    public void setDingtalknotifyall(Boolean dingtalknotifyall) {
        this.dingtalknotifyall = dingtalknotifyall;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getDingtalkmessageurl() {
        return dingtalkmessageurl;
    }

    public void setDingtalkmessageurl(String dingtalkmessageurl) {
        this.dingtalkmessageurl = dingtalkmessageurl;
    }

    public String getDingtalkreceiver() {
        return dingtalkreceiver;
    }

    public void setDingtalkreceiver(String dingtalkreceiver) {
        this.dingtalkreceiver = dingtalkreceiver;
    }

    public Boolean getWechatnotify() {
        return wechatnotify;
    }

    public void setWechatnotify(Boolean wechatnotify) {
        this.wechatnotify = wechatnotify;
    }

    public Boolean getWechatnotifyall() {
        return wechatnotifyall;
    }

    public void setWechatnotifyall(Boolean wechatnotifyall) {
        this.wechatnotifyall = wechatnotifyall;
    }

    public String getWechatmessageurl() {
        return wechatmessageurl;
    }

    public void setWechatmessageurl(String wechatmessageurl) {
        this.wechatmessageurl = wechatmessageurl;
    }

    public String getWechatreceiver() {
        return wechatreceiver;
    }

    public void setWechatreceiver(String wechatreceiver) {
        this.wechatreceiver = wechatreceiver;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
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

    public Boolean getRiskassetnotify() {
        return riskassetnotify;
    }

    public void setRiskassetnotify(Boolean riskassetnotify) {
        this.riskassetnotify = riskassetnotify;
    }
}

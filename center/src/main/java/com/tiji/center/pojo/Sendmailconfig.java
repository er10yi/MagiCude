package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * sendmailconfig实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_sendmailconfig")
public class Sendmailconfig implements Serializable {

    @Id
    private String id;//编号


    private String sendhost;//邮箱host
    private String sendpassword;//密码
    private String sendfrom;//发件人
    private String sendto;//提醒邮箱，强制提醒，不管是否在提醒白名单里，提醒包括所有资产和在收件人列表接收漏洞风险中的漏洞
    private String sendtorisk;//收件人列表接收漏洞风险
    private String vulnsubject;//漏洞邮件主题
    private String assetsubject;//资产邮件主题
    private String vulncontent;//漏洞邮件内容
    private String assetcontent;//资产邮件内容
    private String excelauthor;//excel作者

    public Sendmailconfig() {
    }

    public Sendmailconfig(String id, String sendhost, String sendpassword, String sendfrom, String sendto, String sendtorisk, String vulnsubject, String assetsubject, String vulncontent, String assetcontent, String excelauthor) {
        this.id = id;
        this.sendhost = sendhost;
        this.sendpassword = sendpassword;
        this.sendfrom = sendfrom;
        this.sendto = sendto;
        this.sendtorisk = sendtorisk;
        this.vulnsubject = vulnsubject;
        this.assetsubject = assetsubject;
        this.vulncontent = vulncontent;
        this.assetcontent = assetcontent;
        this.excelauthor = excelauthor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendhost() {
        return sendhost;
    }

    public void setSendhost(String sendhost) {
        this.sendhost = sendhost;
    }

    public String getSendpassword() {
        return sendpassword;
    }

    public void setSendpassword(String sendpassword) {
        this.sendpassword = sendpassword;
    }

    public String getSendfrom() {
        return sendfrom;
    }

    public void setSendfrom(String sendfrom) {
        this.sendfrom = sendfrom;
    }

    public String getSendto() {
        return sendto;
    }

    public void setSendto(String sendto) {
        this.sendto = sendto;
    }

    public String getSendtorisk() {
        return sendtorisk;
    }

    public void setSendtorisk(String sendtorisk) {
        this.sendtorisk = sendtorisk;
    }

    public String getVulnsubject() {
        return vulnsubject;
    }

    public void setVulnsubject(String vulnsubject) {
        this.vulnsubject = vulnsubject;
    }

    public String getAssetsubject() {
        return assetsubject;
    }

    public void setAssetsubject(String assetsubject) {
        this.assetsubject = assetsubject;
    }

    public String getVulncontent() {
        return vulncontent;
    }

    public void setVulncontent(String vulncontent) {
        this.vulncontent = vulncontent;
    }

    public String getAssetcontent() {
        return assetcontent;
    }

    public void setAssetcontent(String assetcontent) {
        this.assetcontent = assetcontent;
    }

    public String getExcelauthor() {
        return excelauthor;
    }

    public void setExcelauthor(String excelauthor) {
        this.excelauthor = excelauthor;
    }

}

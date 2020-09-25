package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * statistics实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_statistics")
public class Statistics implements Serializable {

    @Id
    private String id;//编号


    private String ipcount;//ip数
    private String ipcountonline;//未下线ip数
    private String portcount;//端口数
    private String portcountonline;//未下线端口数
    private String checkresultcount;//检测结果数
    private String checkresultcountonline;//未修复检测结果数
    private String infocount;//信息检测结果数
    private String lowcount;//低危检测结果数
    private String mediumcount;//中危检测结果数
    private String highcount;//高危检测结果数
    private String criticalcount;//严重检测结果数
    private String fatalcount;//致命检测结果数
    private String infocountonline;//未修复信息检测结果数
    private String lowcountonline;//未修复低危检测结果数
    private String mediumcountonline;//未修复中危检测结果数
    private String highcountonline;//未修复高危检测结果数
    private String criticalcountonline;//未修复严重检测结果数
    private String fatalcountonline;//未修复致命检测结果数
    private String riskportcount;//高危端口数
    private String riskportcountonline;//未下线高危端口数
    private String riskservicecount;//高危服务数
    private String riskservicecountonline;//未下线高危服务数
    private String riskversioncount;//高危版本数
    private String riskversioncountonline;//未下线高危版本数
    private java.util.Date updatetime;//更新时间


    public Statistics() {
    }

    public Statistics(String id, String ipcount, String ipcountonline, String portcount, String portcountonline,
                      String checkresultcount, String checkresultcountonline, String infocount, String lowcount,
                      String mediumcount, String highcount, String criticalcount, String fatalcount,
                      String infocountonline, String lowcountonline, String mediumcountonline, String highcountonline,
                      String criticalcountonline, String fatalcountonline,
                      String riskportcount, String riskportcountonline, String riskservicecount, String riskservicecountonline, String riskversioncount, String riskversioncountonline,
                      java.util.Date updatetime) {
        this.id = id;
        this.ipcount = ipcount;
        this.ipcountonline = ipcountonline;
        this.portcount = portcount;
        this.portcountonline = portcountonline;
        this.checkresultcount = checkresultcount;
        this.checkresultcountonline = checkresultcountonline;
        this.infocount = infocount;
        this.lowcount = lowcount;
        this.mediumcount = mediumcount;
        this.highcount = highcount;
        this.criticalcount = criticalcount;
        this.fatalcount = fatalcount;
        this.infocountonline = infocountonline;
        this.lowcountonline = lowcountonline;
        this.mediumcountonline = mediumcountonline;
        this.highcountonline = highcountonline;
        this.criticalcountonline = criticalcountonline;
        this.fatalcountonline = fatalcountonline;
        this.riskportcount = riskportcount;
        this.riskportcountonline = riskportcountonline;
        this.riskservicecount = riskservicecount;
        this.riskservicecountonline = riskservicecountonline;
        this.riskversioncount = riskversioncount;
        this.riskversioncountonline = riskversioncountonline;
        this.updatetime = updatetime;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpcount() {
        return ipcount;
    }

    public void setIpcount(String ipcount) {
        this.ipcount = ipcount;
    }

    public String getIpcountonline() {
        return ipcountonline;
    }

    public void setIpcountonline(String ipcountonline) {
        this.ipcountonline = ipcountonline;
    }

    public String getPortcount() {
        return portcount;
    }

    public void setPortcount(String portcount) {
        this.portcount = portcount;
    }

    public String getPortcountonline() {
        return portcountonline;
    }

    public void setPortcountonline(String portcountonline) {
        this.portcountonline = portcountonline;
    }

    public String getCheckresultcount() {
        return checkresultcount;
    }

    public void setCheckresultcount(String checkresultcount) {
        this.checkresultcount = checkresultcount;
    }

    public String getCheckresultcountonline() {
        return checkresultcountonline;
    }

    public void setCheckresultcountonline(String checkresultcountonline) {
        this.checkresultcountonline = checkresultcountonline;
    }

    public String getInfocount() {
        return infocount;
    }

    public void setInfocount(String infocount) {
        this.infocount = infocount;
    }

    public String getLowcount() {
        return lowcount;
    }

    public void setLowcount(String lowcount) {
        this.lowcount = lowcount;
    }

    public String getMediumcount() {
        return mediumcount;
    }

    public void setMediumcount(String mediumcount) {
        this.mediumcount = mediumcount;
    }

    public String getHighcount() {
        return highcount;
    }

    public void setHighcount(String highcount) {
        this.highcount = highcount;
    }

    public String getCriticalcount() {
        return criticalcount;
    }

    public void setCriticalcount(String criticalcount) {
        this.criticalcount = criticalcount;
    }

    public String getFatalcount() {
        return fatalcount;
    }

    public void setFatalcount(String fatalcount) {
        this.fatalcount = fatalcount;
    }

    public String getInfocountonline() {
        return infocountonline;
    }

    public void setInfocountonline(String infocountonline) {
        this.infocountonline = infocountonline;
    }

    public String getLowcountonline() {
        return lowcountonline;
    }

    public void setLowcountonline(String lowcountonline) {
        this.lowcountonline = lowcountonline;
    }

    public String getMediumcountonline() {
        return mediumcountonline;
    }

    public void setMediumcountonline(String mediumcountonline) {
        this.mediumcountonline = mediumcountonline;
    }

    public String getHighcountonline() {
        return highcountonline;
    }

    public void setHighcountonline(String highcountonline) {
        this.highcountonline = highcountonline;
    }

    public String getCriticalcountonline() {
        return criticalcountonline;
    }

    public void setCriticalcountonline(String criticalcountonline) {
        this.criticalcountonline = criticalcountonline;
    }

    public String getFatalcountonline() {
        return fatalcountonline;
    }

    public void setFatalcountonline(String fatalcountonline) {
        this.fatalcountonline = fatalcountonline;
    }

    public String getRiskportcount() {
        return riskportcount;
    }

    public void setRiskportcount(String riskportcount) {
        this.riskportcount = riskportcount;
    }

    public String getRiskportcountonline() {
        return riskportcountonline;
    }

    public void setRiskportcountonline(String riskportcountonline) {
        this.riskportcountonline = riskportcountonline;
    }

    public String getRiskservicecount() {
        return riskservicecount;
    }

    public void setRiskservicecount(String riskservicecount) {
        this.riskservicecount = riskservicecount;
    }

    public String getRiskservicecountonline() {
        return riskservicecountonline;
    }

    public void setRiskservicecountonline(String riskservicecountonline) {
        this.riskservicecountonline = riskservicecountonline;
    }

    public String getRiskversioncount() {
        return riskversioncount;
    }

    public void setRiskversioncount(String riskversioncount) {
        this.riskversioncount = riskversioncount;
    }

    public String getRiskversioncountonline() {
        return riskversioncountonline;
    }

    public void setRiskversioncountonline(String riskversioncountonline) {
        this.riskversioncountonline = riskversioncountonline;
    }

    public java.util.Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(java.util.Date updatetime) {
        this.updatetime = updatetime;
    }


}

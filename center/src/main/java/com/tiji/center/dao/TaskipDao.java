package com.tiji.center.dao;

import com.tiji.center.pojo.Taskip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * taskip数据访问接口
 * 所有多表的sql语句都写在这里
 *
 * @author 贰拾壹
 */
public interface TaskipDao extends JpaRepository<Taskip, String>, JpaSpecificationExecutor<Taskip> {
    List<Taskip> findByTaskid(String taskId);

    Taskip findByTaskidAndIpaddressv4(String taskid, String ipaddressv4);

    List<Taskip> findAllByTaskidAndCheckwhitelistIsFalse(String taskid);

    @Query(value =
            "SELECT " +
                    "tip.ipaddressv4,tp.port " +
                    "FROM tb_taskip tip, tb_taskport tp " +
                    "WHERE tp.taskipid=tip.id and tip.taskid=?1", nativeQuery = true)
    List<String> findTaskIpAndPort(String taskId);


    @Query(value = "SELECT " +
            "ta.id,ta.ipaddressv4,tp.`port`,tp.service " +
            "FROM tb_taskip ta,tb_taskport tp " +
            "WHERE tp.taskipid = ta.id and tp.state=\"open\" and tp.checkwhitelist IS FALSE and tp.service like ?2 and taskid=?1 ", nativeQuery = true)
    List<String> findAllByServiceLikeAndCheckwhitelistIsFalse(String taskId, String serviceLike);

    @Query(value =
            "SELECT " +
                    "ta.id,ta.ipaddressv4,tp.`port`,tp.service " +
                    "FROM tb_taskip ta,tb_taskport tp " +
                    "WHERE tp.taskipid = ta.id and tp.state=\"open\" and tp.checkwhitelist IS FALSE and tp.service is NULL and taskid=?1", nativeQuery = true)
    List<String> findByTaskidAndServiceIsNullAndCheckwhitelistIsFalse(String taskId);


    @Query(value =
            "SELECT " +
                    "ta.ipaddressv4,tp.`port`,tp.service,tp.version " +
                    "FROM tb_taskip ta,tb_taskport tp " +
                    "WHERE tp.taskipid = ta.id and tp.state=\"open\" and ta.checkwhitelist IS FALSE and tp.checkwhitelist IS FALSE and ta.taskid=?1 and tp.service like ?2", nativeQuery = true)
    List<String> findByTaskidAndServiceLikeAndCheckwhitelistIsFalse(String taskId, String serviceLike);

    @Query(value =
            "SELECT " +
                    "ta.ipaddressv4,tp.`port`,tp.service,tp.version " +
                    "FROM tb_taskip ta,tb_taskport tp " +
                    "WHERE tp.taskipid = ta.id and tp.state=\"open\" and ta.checkwhitelist IS FALSE and tp.checkwhitelist IS FALSE and ta.taskid=?1 and tp.version like ?2", nativeQuery = true)
    List<String> findByTaskidAndVersionLikeAndCheckwhitelistIsFalse(String taskId, String versionLike);


    @Query(value =
            "SELECT " +
                    "tcp.`name` \"tcpname\",tcs.`name` \"tcsname\", " +
                    "tv.`name` \"tvname\",tv.risk \"tvrisk\", " +
                    "taip.ipaddressv4,DATE_FORMAT(taip.activetime,'%Y-%m-%d %H:%i:%s') \"taipactivetime\",DATE_FORMAT(taip.passivetime,'%Y-%m-%d %H:%i:%s') \"taippassivetime\", " +
                    "tap.`port`,tap.service,tap.version,DATE_FORMAT(tap.uptime,'%Y-%m-%d %H:%i:%s'),DATE_FORMAT(tap.downtime,'%Y-%m-%d %H:%i:%s'), " +
                    "tcr.`name` \"tcrname\",tcr.result,DATE_FORMAT(tcr.activetime,'%Y-%m-%d %H:%i:%s') \"tcractivetime\", " +
                    "tv.description,tv.refer,tv.impactscope, " +
                    "ts.solution,ts.codedemo,ts.configdemo, " +
                    "taip.projectinfoid " +
                    "FROM tb_assetip taip, tb_assetport tap,tb_checkresult tcr,tb_checkresult_vuln tcvm,tb_vuln tv,tb_solution ts,tb_categorytop tcp, tb_categorysecond tcs " +
                    "WHERE tap.assetipid=taip.id and tcr.assetportid=tap.id and tv.id=tcvm.vulnid and  tcvm.checkresultid=tcr.id and ts.vulnid=tv.id " +
                    "and  tcvm.checkresultid=tcr.id and tv.categorysecondid=tcs.id and tcs.categorytopid=tcp.id " +
                    "and tcr.passivetime IS NULL " +
                    "ORDER BY FIELD(tv.risk,\"致命\",\"严重\",\"高危\",\"中危\",\"低危\",\"信息\")", nativeQuery = true)
    List<List> findAllVulns();

    @Query(value =
            "SELECT " +
                    "taip.ipaddressv4,tp.`port`,tp.protocol,tp.state,tp.service,tp.version,tp.uptime,tp.changedtime, " +
                    "IF(tp.`port` IN (SELECT `port` FROM tb_riskport),1,NULL) \"riskport\", " +
                    "IF(tp.service IN (SELECT service FROM tb_riskservice),1,NULL) \"riskservice\", " +
                    "IF(tp.version IN (SELECT version FROM tb_riskversion),1,NULL) \"riskversion\", " +
                    "taip.projectinfoid " +
                    "FROM tb_assetip taip,tb_assetport tp " +
                    "WHERE tp.assetipid = taip.id " +
                    "and taip.assetnotifywhitelist IS FALSE and tp.assetnotifywhitelist IS FALSE " +
                    "and taip.passivetime IS NULL and tp.downtime IS NULL " +
                    "ORDER BY taip.ipaddressv4,tp.service ", nativeQuery = true)
    List<List> findAllAssets();

    @Query(value =
            "SELECT COUNT(*) " +
                    "FROM tb_assetip taip,tb_assetport tp " +
                    "WHERE tp.assetipid = taip.id " +
                    "and taip.assetnotifywhitelist IS FALSE and tp.assetnotifywhitelist IS FALSE " +
                    "and taip.passivetime IS NULL and tp.downtime IS NULL ", nativeQuery = true)
    long findAllAssetsCount();

    @Query(value =
            "SELECT " +
                    "taip.ipaddressv4,tp.`port`,tp.protocol,tp.state,tp.service,tp.version," +
                    "DATE_FORMAT(tp.uptime,'%Y-%m-%d %H:%i:%s'),DATE_FORMAT(tp.changedtime,'%Y-%m-%d %H:%i:%s'), " +
                    "IF(tp.`port` IN (SELECT `port` FROM tb_riskport),1,NULL) \"riskport\", " +
                    "IF(tp.service IN (SELECT service FROM tb_riskservice),1,NULL) \"riskservice\", " +
                    "IF(tp.version IN (SELECT version FROM tb_riskversion),1,NULL) \"riskversion\", " +
                    "taip.projectinfoid " +
                    "FROM tb_assetip taip,tb_assetport tp " +
                    "WHERE tp.assetipid = taip.id " +
                    "and taip.assetnotifywhitelist IS FALSE and tp.assetnotifywhitelist IS FALSE " +
                    "and taip.passivetime IS NULL and tp.downtime IS NULL " +
                    "LIMIT ?1,?2 ", nativeQuery = true)
    List<List<String>> findAllAssetsByPage(long offset, long rows);

    @Query(value =
            "SELECT  \n" +
                    "taip.ipaddressv4,tp.`port`,tp.protocol,tp.state,tp.service,tp.version, \n" +
                    "DATE_FORMAT(tp.uptime,'%Y-%m-%d %H:%i:%s') \"uptime\",DATE_FORMAT(tp.changedtime,'%Y-%m-%d %H:%i:%s') \"changedtime\",  \n" +
                    "IF(tp.`port` IN (SELECT `port` FROM tb_riskport),1,NULL) \"riskport\",  \n" +
                    "IF(tp.service IN (SELECT service FROM tb_riskservice),1,NULL) \"riskservice\",  \n" +
                    "IF(tp.version IN (SELECT version FROM tb_riskversion),1,NULL) \"riskversion\",  \n" +
                    "IF(taip.projectinfoid IN (SELECT projectinfoid FROM tb_projectinfo),(SELECT tpi.projectname FROM tb_projectinfo tpi WHERE tpi.id = taip.projectinfoid),NULL) \"projectinfoName\",\n" +
                    "(select group_concat(tc.`name` SEPARATOR \";\") from tb_contact tc,tb_contact_projectinfo tcp,tb_projectinfo tpi where tcp.contactid = tc.id and taip.projectinfoid = tpi.id and tpi.id = tcp.projectinfoid) \"contactname\",\n" +
                    "(select group_concat(tc.email SEPARATOR \";\") from tb_contact tc,tb_contact_projectinfo tcp,tb_projectinfo tpi where tcp.contactid = tc.id and taip.projectinfoid = tpi.id and tpi.id = tcp.projectinfoid) \"contactemail\",\n" +
                    "(select group_concat(tc.phone SEPARATOR \";\") from tb_contact tc,tb_contact_projectinfo tcp,tb_projectinfo tpi where tcp.contactid = tc.id and taip.projectinfoid = tpi.id and tpi.id = tcp.projectinfoid) \"contactphone\"\n" +
                    "FROM tb_assetip taip,tb_assetport tp  \n" +
                    "WHERE tp.assetipid = taip.id  \n" +
                    "and taip.assetnotifywhitelist IS FALSE and tp.assetnotifywhitelist IS FALSE  \n" +
                    "and taip.passivetime IS NULL and tp.downtime IS NULL  \n" +
                    "LIMIT ?1,?2", nativeQuery = true)
    List<List<String>> findAllAssetsByPageNew(long offset, long rows);

    @Query(value = "SELECT tpp.ipaddressv4 FROM tb_assetip tpp WHERE tpp.id in(select taip.id from tb_assetip taip where taip.id not in (select tp.assetipid from tb_assetport tp)) ", nativeQuery = true)
    List<String> findAllAssetipNoPort();

    @Query(value =
            "SELECT COUNT(*) FROM `tb_checkresult` WHERE passivetime is NULL", nativeQuery = true)
    long findAllVulnsCount();

    @Query(value =
            "SELECT DISTINCTROW\n" +
                    "tcp.`name` \"一级分类\",tcs.`name` \"二级分类\",\n" +
                    "tv.`name` \"漏洞名称\",\n" +
                    "tv.risk \"风险\",\n" +
                    "taip.ipaddressv4,\n" +
                    "DATE_FORMAT(taip.activetime,'%Y-%m-%d %H:%i:%s') \"ip上线时间\",DATE_FORMAT(taip.passivetime,'%Y-%m-%d %H:%i:%s') \"ip下线时间\",\n" +
                    "tap.`port`,tap.service,tap.version,DATE_FORMAT(tap.uptime,'%Y-%m-%d %H:%i:%s') \"端口发现时间\",DATE_FORMAT(tap.downtime,'%Y-%m-%d %H:%i:%s') \"端口关闭时间\",\n" +
                    "(select group_concat(tcr.`name` SEPARATOR \";\") from tb_checkresult tcr ,tb_checkresult_vuln tcvm,tb_vuln tv where tcr.assetportid=tap.id and tv.id=tcvm.vulnid and  tcvm.checkresultid=tcr.id and tcr.passivetime IS NULL) \"检测插件\",\n" +
                    "(select group_concat(tcr.`result` SEPARATOR \";\") from tb_checkresult tcr ,tb_checkresult_vuln tcvm,tb_vuln tv where tcr.assetportid=tap.id and tv.id=tcvm.vulnid and  tcvm.checkresultid=tcr.id and tcr.passivetime IS NULL) \"检测结果\",\n" +
                    "(select group_concat(DATE_FORMAT(tcr.`activetime`,'%Y-%m-%d %H:%i:%s') SEPARATOR \";\") from tb_checkresult tcr ,tb_checkresult_vuln tcvm,tb_vuln tv where tcr.assetportid=tap.id and tv.id=tcvm.vulnid and  tcvm.checkresultid=tcr.id and tcr.passivetime IS NULL) \"漏洞发现时间\",\n" +
                    "tv.description,tv.refer,tv.impactscope,\n" +
                    "(select group_concat(ts.solution SEPARATOR \";\") from tb_solution ts where ts.vulnid=tv.id) \"solution\",\n" +
                    "(select group_concat(ts.codedemo SEPARATOR \";\") from tb_solution ts where ts.vulnid=tv.id) \"codedemo\",\n" +
                    "(select group_concat(ts.configdemo SEPARATOR \";\") from tb_solution ts where ts.vulnid=tv.id) \"configdemo\",\n" +
                    "IF(taip.projectinfoid IN (SELECT projectinfoid FROM tb_projectinfo),(SELECT tpi.projectname FROM tb_projectinfo tpi WHERE tpi.id = taip.projectinfoid),NULL) \"projectinfoName\" ,\n" +
                    "(select group_concat(tc.`name` SEPARATOR \";\") from tb_contact tc,tb_contact_projectinfo tcp,tb_projectinfo tpi where tcp.contactid = tc.id and taip.projectinfoid = tpi.id and tpi.id = tcp.projectinfoid) \"contactname\",\n" +
                    "(select group_concat(tc.email SEPARATOR \";\") from tb_contact tc,tb_contact_projectinfo tcp,tb_projectinfo tpi where tcp.contactid = tc.id and taip.projectinfoid = tpi.id and tpi.id = tcp.projectinfoid) \"contactemail\",\n" +
                    "(select group_concat(tc.phone SEPARATOR \";\") from tb_contact tc,tb_contact_projectinfo tcp,tb_projectinfo tpi where tcp.contactid = tc.id and taip.projectinfoid = tpi.id and tpi.id = tcp.projectinfoid) \"contactphone\" \n" +
                    "FROM tb_assetip taip, tb_assetport tap,tb_checkresult tcr,tb_checkresult_vuln tcvm,tb_vuln tv,tb_categorytop tcp, tb_categorysecond tcs  \n" +
                    "WHERE tap.assetipid=taip.id and tcr.assetportid=tap.id and tv.id=tcvm.vulnid and  tcvm.checkresultid=tcr.id\n" +
                    "and  tcvm.checkresultid=tcr.id and tv.categorysecondid=tcs.id and tcs.categorytopid=tcp.id\n" +
                    "and tcr.passivetime IS NULL\n" +
                    "ORDER BY FIELD(tv.risk,\"致命\",\"严重\",\"高危\",\"中危\",\"低危\",\"信息\"),taip.ipaddressv4 \n" +
                    "LIMIT ?1,?2", nativeQuery = true)
    List<List<String>> findAllVulnsByPage(long offset, long rows);

    @Modifying
    @Query(value = "DELETE FROM `tb_taskip` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

package com.tiji.center.dao;

import com.tiji.center.pojo.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * statistics数据访问接口
 *
 * @author 贰拾壹
 */
public interface StatisticsDao extends JpaRepository<Statistics, String>, JpaSpecificationExecutor<Statistics> {

    @Query(value = "SELECT taip.ipaddressv4,COUNT(*) as portCount FROM tb_assetip taip,tb_assetport tp WHERE tp.assetipid = taip.id GROUP BY taip.ipaddressv4 ORDER BY portCount desc LIMIT 0,100", nativeQuery = true)
    List<String> findIpPortCount();

    @Query(value = "SELECT taip.ipaddressv4,COUNT(*) as portCount FROM tb_assetip taip,tb_assetport tp WHERE tp.assetipid = taip.id and tp.downtime IS null and taip.passivetime is NULL GROUP BY taip.ipaddressv4 ORDER BY portCount desc LIMIT 0,100", nativeQuery = true)
    List<String> findIpPortCountOnline();

    @Query(value = "SELECT tp.service,COUNT(*)  serviceCount FROM tb_assetport tp GROUP BY tp.service ORDER BY serviceCount desc", nativeQuery = true)
    List<String> findServiceCount();

    @Query(value = "SELECT tp.service,COUNT(*)  serviceCount FROM tb_assetport tp WHERE tp.downtime IS null GROUP BY tp.service ORDER BY serviceCount desc", nativeQuery = true)
    List<String> findServiceCountOnline();

    @Query(value = "SELECT tp.version,COUNT(*)  versionCount FROM tb_assetport tp GROUP BY tp.version ORDER BY versionCount desc", nativeQuery = true)
    List<String> findVersionCount();

    @Query(value = "SELECT tp.version,COUNT(*)  versionCount FROM tb_assetport tp WHERE tp.downtime IS null GROUP BY tp.version ORDER BY versionCount desc", nativeQuery = true)
    List<String> findVersionCountOnline();

    @Query(value = "SELECT twb.`server`,COUNT(*)  serverCount FROM tb_webinfo twb GROUP BY twb.`server` ORDER BY serverCount desc", nativeQuery = true)
    List<String> findWebinfoServerCount();

    @Query(value = "SELECT tp.`port`,COUNT(*) portCount FROM tb_assetport tp WHERE tp.`port` IN (SELECT DISTINCT trp.`port` FROM tb_riskport trp) GROUP BY tp.`port` ORDER BY portCount desc", nativeQuery = true)
    List<String> findRiskPortCount();

    @Query(value = "SELECT tp.`port`,COUNT(*) portCount FROM tb_assetport tp WHERE tp.`port` IN (SELECT DISTINCT trp.`port` FROM tb_riskport trp) and tp.downtime IS null GROUP BY tp.`port` ORDER BY portCount desc", nativeQuery = true)
    List<String> findRiskPortCountOnline();

    @Query(value = "SELECT tp.service,COUNT(*)  serviceCount FROM tb_assetport tp WHERE tp.service IN (SELECT DISTINCT trs.service FROM tb_riskservice trs) GROUP BY tp.service ORDER BY serviceCount desc", nativeQuery = true)
    List<String> findRiskServiceCount();

    @Query(value = "SELECT tp.service,COUNT(*)  serviceCount FROM tb_assetport tp WHERE tp.service IN (SELECT DISTINCT trs.service FROM tb_riskservice trs)  and tp.downtime IS null GROUP BY tp.service ORDER BY serviceCount desc", nativeQuery = true)
    List<String> findRiskServiceCountOnline();

    @Query(value = "SELECT tp.version,COUNT(*)  versionCount FROM tb_assetport tp WHERE tp.version IN (SELECT DISTINCT trv.version FROM tb_riskversion trv) GROUP BY tp.version ORDER BY versionCount desc", nativeQuery = true)
    List<String> findRiskVersionCount();

    @Query(value = "SELECT tp.version,COUNT(*)  versionCount FROM tb_assetport tp WHERE tp.version IN (SELECT DISTINCT trv.version FROM tb_riskversion trv) and tp.downtime IS null GROUP BY tp.version ORDER BY versionCount desc", nativeQuery = true)
    List<String> findRiskVersionCountOnline();

    @Query(value = "SELECT tcr.risk,COUNT(*)  riskCount FROM tb_checkresult tcr GROUP BY tcr.risk ORDER BY riskCount desc", nativeQuery = true)
    List<String> findRiskCount();

    @Query(value = "SELECT tcr.risk,COUNT(*)  riskCount FROM tb_checkresult tcr WHERE tcr.passivetime IS NULL GROUP BY tcr.risk ORDER BY riskCount desc", nativeQuery = true)
    List<String> findRiskCountOnline();

    @Query(value = "SELECT (SELECT tv.name FROM tb_vuln tv WHERE tv.id = tcrv.vulnid) as \"vulnname\",COUNT(vulnid) AS vulnCount FROM `tb_checkresult_vuln` tcrv GROUP BY tcrv.vulnid ORDER BY vulnCount desc", nativeQuery = true)
    List<String> findRiskVulnCount();

    @Query(value = "SELECT (SELECT tv.name FROM tb_vuln tv WHERE tv.id = tcrv.vulnid) as \"vulnname\",COUNT(vulnid) AS vulnCount FROM `tb_checkresult_vuln` tcrv WHERE tcrv.checkresultid IN (SELECT tcr.id FROM tb_checkresult tcr WHERE tcr.passivetime IS NULL) GROUP BY tcrv.vulnid ORDER BY vulnCount desc", nativeQuery = true)
    List<String> findRiskVulnCountOnline();

    @Query(value = "SELECT COUNT(*) FROM tb_assetip", nativeQuery = true)
    String findIpCount();

    @Query(value = "SELECT COUNT(*) FROM tb_assetip WHERE passivetime is NULL", nativeQuery = true)
    String findIpCountOnline();

    @Query(value = "SELECT COUNT(*) FROM tb_assetport", nativeQuery = true)
    String findPortCount();

    @Query(value = "SELECT COUNT(*) FROM tb_assetport WHERE downtime is NULL", nativeQuery = true)
    String findPortCountOnline();

    @Query(value = "SELECT tcr.risk,COUNT(*)  riskCount FROM tb_checkresult tcr GROUP BY tcr.risk ORDER BY FIELD(tcr.risk,\"致命\",\"严重\",\"高危\",\"中危\",\"低危\",\"信息\")", nativeQuery = true)
    List<String> findCheckresultCount();

    @Query(value = "SELECT tcr.risk,COUNT(*)  riskCount FROM tb_checkresult tcr WHERE tcr.passivetime IS NULL GROUP BY tcr.risk ORDER BY FIELD(tcr.risk,\"致命\",\"严重\",\"高危\",\"中危\",\"低危\",\"信息\")", nativeQuery = true)
    List<String> findCheckresultCountOnline();

    @Query(value = "SELECT COUNT(*) portCount FROM tb_assetport tp WHERE tp.`port` IN (SELECT DISTINCT trp.`port` FROM tb_riskport trp)", nativeQuery = true)
    String findRiskportCount();

    @Query(value = "SELECT COUNT(*) portCount FROM tb_assetport tp WHERE tp.`port` IN (SELECT DISTINCT trp.`port` FROM tb_riskport trp) and tp.downtime IS null", nativeQuery = true)
    String findRiskportCountOnline();

    @Query(value = "SELECT COUNT(*)  serviceCount FROM tb_assetport tp WHERE tp.service IN (SELECT DISTINCT trs.service FROM tb_riskservice trs)", nativeQuery = true)
    String findRiskserviceCount();

    @Query(value = "SELECT COUNT(*)  serviceCount FROM tb_assetport tp WHERE tp.service IN (SELECT DISTINCT trs.service FROM tb_riskservice trs) and tp.downtime IS null", nativeQuery = true)
    String findRiskserviceCountOnline();

    @Query(value = "SELECT COUNT(*) versionCount FROM tb_assetport tp WHERE tp.version IN (SELECT DISTINCT trv.version FROM tb_riskversion trv)", nativeQuery = true)
    String findRiskversionCount();

    @Query(value = "SELECT COUNT(*) versionCount FROM tb_assetport tp WHERE tp.version IN (SELECT DISTINCT trv.version FROM tb_riskversion trv) and tp.downtime IS null", nativeQuery = true)
    String findRiskversionCountOnline();
}

package com.tiji.center.dao;

import com.tiji.center.pojo.Assetport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * assetport数据访问接口
 *
 * @author 贰拾壹
 */
public interface AssetportDao extends JpaRepository<Assetport, String>, JpaSpecificationExecutor<Assetport> {

    List<Assetport> findAllByServiceAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(String service);

    List<Assetport> findByVersionLikeAndDowntimeIsNull(String assetVersion);

    List<Assetport> findByServiceLikeAndDowntimeIsNull(String assetService);

    List<Assetport> findAllByAssetipidAndDowntimeIsNull(String assetipid);

    Assetport findByAssetipidAndPortAndDowntimeIsNull(String assetipid, String assetport);

    Assetport findByIdAndAndAssetipidAndDowntimeIsNull(String id, String assetipid);

    List<Assetport> findByServiceLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(String version, String state);

    List<Assetport> findByVersionLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(String version, String state);

    List<Assetport> deleteAllByAssetipid(String assetipid);

    //    @Query(value = "SELECT DISTINCT service FROM `tb_assetport` WHERE not regexp_like(service,'[,?]+') ORDER BY service",nativeQuery = true)
    @Query(value = "SELECT DISTINCT service FROM `tb_assetport`  ORDER BY service", nativeQuery = true)
    List<String> findAllDistinctService();

    //     @Query(value = "SELECT DISTINCT version FROM `tb_assetport` WHERE not regexp_like(version,'[,?]+') ORDER BY version",nativeQuery = true)
    @Query(value = "SELECT DISTINCT version FROM `tb_assetport` ORDER BY version", nativeQuery = true)
    List<String> findAllDistinctVersion();

    List<Assetport> findAllByAssetipid(String assetipid);

    List<Assetport> findAllByVersionAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(String version);

    @Modifying
    @Query(value = "DELETE FROM `tb_assetport` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

    @Query(value = "SELECT tp.id,COUNT(*) as vulnCount FROM tb_assetport tp,tb_checkresult tcl  WHERE tcl.assetportid IN(tp.id) and tp.id in(?1) GROUP BY tp.id ", nativeQuery = true)
    List<String> findVulnCountByIds(List<String> ids);

    @Query(value = "SELECT tp.id,COUNT(*) as vulnCount FROM tb_assetport tp,tb_checkresult tcl  WHERE tcl.assetportid IN(tp.id) and ISNULL(tcl.passivetime) and tp.id in(?1) GROUP BY tp.id ", nativeQuery = true)
    List<String> findVulnCountOnlineByIds(List<String> ids);

    @Modifying
    @Query(value = "UPDATE tb_assetip_appsys_hostdomain SET assetportid =null WHERE assetportid=?1", nativeQuery = true)
    void updateMiddByAssetportidSetAssetportid2Null(String id);
}

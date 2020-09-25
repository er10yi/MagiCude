package com.tiji.center.dao;

import com.tiji.center.pojo.Pluginassetversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * assetversion数据访问接口
 *
 * @author 贰拾壹
 */
public interface PluginassetversionDao extends JpaRepository<Pluginassetversion, String>, JpaSpecificationExecutor<Pluginassetversion> {
    Pluginassetversion findByPluginconfigidAndAssetversion(String pluginConfigId, String versionName);

    void deleteAllByPluginconfigid(String pluginconfigId);

    List<Pluginassetversion> findAllByPluginconfigid(String pluginconfigid);


    @Query(value =
            "SELECT " +
                    "tpav.* FROM tb_pluginassetversion tpav " +
                    "WHERE tpav.pluginconfigid IN (SELECT pluginconfigid FROM `tb_taskpluginconfig` WHERE taskid = ?1)", nativeQuery = true)
    List<Pluginassetversion> findPluginassetversionByTaskid(String taskId);

    @Modifying
    @Query(value = "DELETE FROM `tb_pluginassetversion` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

package com.tiji.center.dao;

import com.tiji.center.pojo.Pluginassetservice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * assetservice数据访问接口
 *
 * @author 贰拾壹
 */
public interface PluginassetserviceDao extends JpaRepository<Pluginassetservice, String>, JpaSpecificationExecutor<Pluginassetservice> {
    Pluginassetservice findByPluginconfigidAndAssetservice(String pluginConfigId, String serviceName);

    void deleteAllByPluginconfigid(String pluginconfigId);

    List<Pluginassetservice> findAllByPluginconfigid(String pluginconfigid);

    @Query(value = "SELECT " +
            "tpas.* FROM tb_pluginassetservice tpas  " +
            "WHERE tpas.pluginconfigid IN (SELECT pluginconfigid FROM `tb_taskpluginconfig` WHERE taskid = ?1)", nativeQuery = true)
    List<Pluginassetservice> findPluginassetserviceByTaskid(String taskId);

    @Modifying
    @Query(value = "DELETE FROM `tb_pluginassetservice` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

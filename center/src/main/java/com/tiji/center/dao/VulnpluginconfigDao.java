package com.tiji.center.dao;

import com.tiji.center.pojo.Vulnpluginconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * vulnpluginconfig数据访问接口
 *
 * @author 贰拾壹
 */
public interface VulnpluginconfigDao extends JpaRepository<Vulnpluginconfig, String>, JpaSpecificationExecutor<Vulnpluginconfig> {
    List<Vulnpluginconfig> findAllByPluginconfigid(String pluginConfigId);

    void deleteAllByPluginconfigid(String pluginconfigId);

    Vulnpluginconfig findByVulnidAndPluginconfigid(String vulnid, String pluginconfigid);

    @Modifying
    @Query(value = "DELETE FROM `tb_vulnpluginconfig` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

    void deleteByPluginconfigidAndVulnid(String pluginId, String vulnId);
}

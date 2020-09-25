package com.tiji.center.dao;

import com.tiji.center.pojo.Pluginconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * pluginconfig数据访问接口
 *
 * @author 贰拾壹
 */
public interface PluginconfigDao extends JpaRepository<Pluginconfig, String>, JpaSpecificationExecutor<Pluginconfig> {

    Pluginconfig findByName(String pluginConfigName);

    Pluginconfig findByNameAndType(String pluginConfigName, String pluginConfigType);

    Pluginconfig findByIdAndType(String id, String type);

    @Modifying
    @Query(value = "DELETE FROM `tb_pluginconfig` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

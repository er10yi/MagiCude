package com.tiji.center.dao;

import com.tiji.center.pojo.Vulnkeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * vulnkeyword数据访问接口
 *
 * @author 贰拾壹
 */
public interface VulnkeywordDao extends JpaRepository<Vulnkeyword, String>, JpaSpecificationExecutor<Vulnkeyword> {
    Vulnkeyword findByPluginconfigidAndKeyword(String pluginConfigId, String vulnKeywordName);

    void deleteAllByPluginconfigid(String pluginconfigId);

    List<Vulnkeyword> findAllByPluginconfigid(String pluginconfigid);

    @Query(value = "SELECT DISTINCT keyword  FROM tb_vulnkeyword ORDER BY keyword", nativeQuery = true)
    List<String> findAllDistinctVulnKeyword();

    @Modifying
    @Query(value = "DELETE FROM `tb_vulnkeyword` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

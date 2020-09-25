package com.tiji.center.dao;

import com.tiji.center.pojo.Vuln;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * vuln数据访问接口
 *
 * @author 贰拾壹
 */
public interface VulnDao extends JpaRepository<Vuln, String>, JpaSpecificationExecutor<Vuln> {

    Vuln findByName(String name);

    @Modifying
    @Query(value = "DELETE FROM `tb_vuln` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

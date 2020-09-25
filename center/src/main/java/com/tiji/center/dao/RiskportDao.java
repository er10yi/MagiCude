package com.tiji.center.dao;

import com.tiji.center.pojo.Riskport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * riskport数据访问接口
 *
 * @author 贰拾壹
 */
public interface RiskportDao extends JpaRepository<Riskport, String>, JpaSpecificationExecutor<Riskport> {

    Riskport findByPort(String port);

    @Modifying
    @Query(value = "DELETE FROM `tb_riskport` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

package com.tiji.center.dao;

import com.tiji.center.pojo.Riskversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * riskversion数据访问接口
 *
 * @author 贰拾壹
 */
public interface RiskversionDao extends JpaRepository<Riskversion, String>, JpaSpecificationExecutor<Riskversion> {

    Riskversion findByVersion(String version);

    @Modifying
    @Query(value = "DELETE FROM `tb_riskversion` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

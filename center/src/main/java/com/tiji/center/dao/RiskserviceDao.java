package com.tiji.center.dao;

import com.tiji.center.pojo.Riskservice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * riskservice数据访问接口
 *
 * @author 贰拾壹
 */
public interface RiskserviceDao extends JpaRepository<Riskservice, String>, JpaSpecificationExecutor<Riskservice> {

    Riskservice findByService(String service);

    @Modifying
    @Query(value = "DELETE FROM `tb_riskservice` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

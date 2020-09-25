package com.tiji.center.dao;

import com.tiji.center.pojo.Democode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * democode数据访问接口
 *
 * @author 贰拾壹
 */
public interface DemocodeDao extends JpaRepository<Democode, String>, JpaSpecificationExecutor<Democode> {

    List<Democode> findAllByVulnid(String vulnId);

    void deleteAllByVulnid(String vulnId);

    @Modifying
    @Query(value = "DELETE FROM `tb_democode` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

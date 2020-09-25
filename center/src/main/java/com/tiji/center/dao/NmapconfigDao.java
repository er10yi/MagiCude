package com.tiji.center.dao;

import com.tiji.center.pojo.Nmapconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * nmapconfig数据访问接口
 *
 * @author 贰拾壹
 */
public interface NmapconfigDao extends JpaRepository<Nmapconfig, String>, JpaSpecificationExecutor<Nmapconfig> {
    Nmapconfig findByTaskid(String taskid);

    void deleteAllByTaskid(String taskid);

    @Modifying
    @Query(value = "DELETE FROM `tb_nmapconfig` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

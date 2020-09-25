package com.tiji.center.dao;

import com.tiji.center.pojo.Useragent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * useragent数据访问接口
 *
 * @author 贰拾壹
 */
public interface UseragentDao extends JpaRepository<Useragent, String>, JpaSpecificationExecutor<Useragent> {

    @Query(value = "select distinct useragent from Useragent")
    List<String> findAllDistinctUserAgentList();

    Useragent findByUseragent(String ua);

    @Modifying
    @Query(value = "DELETE FROM `Useragent` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

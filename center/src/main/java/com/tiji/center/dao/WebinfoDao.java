package com.tiji.center.dao;

import com.tiji.center.pojo.Webinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * webinfo数据访问接口
 *
 * @author 贰拾壹
 */
public interface WebinfoDao extends JpaRepository<Webinfo, String>, JpaSpecificationExecutor<Webinfo> {

    List<Webinfo> findByPortid(String portId);

    List<Webinfo> deleteAllByPortid(String assetportId);

    List<Webinfo> findAllByPortid(String assetportid);

    @Modifying
    @Query(value = "DELETE FROM `tb_webinfo` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

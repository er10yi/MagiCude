package com.tiji.center.dao;

import com.tiji.center.pojo.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * url数据访问接口
 *
 * @author 贰拾壹
 */
public interface UrlDao extends JpaRepository<Url, String>, JpaSpecificationExecutor<Url> {


    @Query(value = "SELECT `name`,url FROM `tb_url` WHERE webinfoid = ?1", nativeQuery = true)
    List<String> findByWebinfoid(String webinfoid);

    void deleteAllByWebinfoid(String webinfoid);

    List<Url> findAllByWebinfoid(String webinfoid);

    @Modifying
    @Query(value = "DELETE FROM `tb_url` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

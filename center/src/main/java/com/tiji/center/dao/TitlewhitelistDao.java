package com.tiji.center.dao;

import com.tiji.center.pojo.Titlewhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * titlewhitelist数据访问接口
 *
 * @author 贰拾壹
 */
public interface TitlewhitelistDao extends JpaRepository<Titlewhitelist, String>, JpaSpecificationExecutor<Titlewhitelist> {

    Titlewhitelist findByTitle(String title);

    @Modifying
    @Query(value = "DELETE FROM `tb_titlewhitelist` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

package com.tiji.center.dao;

import com.tiji.center.pojo.Projectportwhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * projectportwhitelist数据访问接口
 *
 * @author 贰拾壹
 */
public interface ProjectportwhitelistDao extends JpaRepository<Projectportwhitelist, String>, JpaSpecificationExecutor<Projectportwhitelist> {

    Projectportwhitelist findByProjectinfoidAndPort(String projectInfoId, String port);

    void deleteAllByProjectinfoid(String projectinfoid);

    @Query(value = "SELECT tppwl.`port` FROM tb_projectportwhitelist tppwl WHERE tppwl.projectinfoid=?1", nativeQuery = true)
    List<String> findAllPortByProjectinfoid(String projectinfoid);

    @Modifying
    @Query(value = "DELETE FROM `tb_projectportwhitelist` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

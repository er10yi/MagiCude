package com.tiji.center.dao;

import com.tiji.center.pojo.Domainwhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * domainwhitelist数据访问接口
 *
 * @author 贰拾壹
 */
public interface DomainwhitelistDao extends JpaRepository<Domainwhitelist, String>, JpaSpecificationExecutor<Domainwhitelist> {

    @Query(value = "SELECT DISTINCT domain FROM `tb_domainwhitelist`", nativeQuery = true)
    List<String> findAllDistinct();

    Domainwhitelist findByDomain(String domain);

    @Modifying
    @Query(value = "DELETE FROM `tb_domainwhitelist` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

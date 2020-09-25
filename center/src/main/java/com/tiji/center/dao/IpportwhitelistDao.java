package com.tiji.center.dao;

import com.tiji.center.pojo.Ipportwhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * ipportwhitelist数据访问接口
 *
 * @author 贰拾壹
 */
public interface IpportwhitelistDao extends JpaRepository<Ipportwhitelist, String>, JpaSpecificationExecutor<Ipportwhitelist> {
    Ipportwhitelist findByIpwhitelistidAndPort(String ipwhitelistid, String port);

    @Query(value = "SELECT tiwl.port FROM `tb_ipportwhitelist` tiwl WHERE tiwl.ipwhitelistid=?1", nativeQuery = true)
    List<String> findAllPortByIpwhitelistid(String ipwhitelistid);

    void deleteAllByipwhitelistid(String ipwhitelistid);

    @Modifying
    @Query(value = "DELETE FROM `tb_ipportwhitelist` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

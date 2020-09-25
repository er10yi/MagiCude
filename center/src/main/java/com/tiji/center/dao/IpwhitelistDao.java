package com.tiji.center.dao;

import com.tiji.center.pojo.Ipwhitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * ipwhitelist数据访问接口
 *
 * @author 贰拾壹
 */
public interface IpwhitelistDao extends JpaRepository<Ipwhitelist, String>, JpaSpecificationExecutor<Ipwhitelist> {

    @Query(value = "SELECT DISTINCT tipwl.ip FROM `tb_ipwhitelist` tipwl WHERE  tipwl.id  NOT IN (SELECT tpwl.ipwhitelistid FROM tb_portwhitelist tpwl )", nativeQuery = true)
    List<String> findAllIpNoPorts();

    @Query(value = "SELECT * FROM `tb_ipwhitelist` tipwl WHERE  tipwl.id  IN (SELECT tpwl.ipwhitelistid FROM tb_portwhitelist tpwl )", nativeQuery = true)
    List<Ipwhitelist> findAllIpHasPorts();

    Ipwhitelist findByIp(String ip);

    @Modifying
    @Query(value = "DELETE FROM `tb_ipwhitelist` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

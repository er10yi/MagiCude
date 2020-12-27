package com.tiji.center.dao;

import com.tiji.center.pojo.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * host数据访问接口
 *
 * @author 贰拾壹
 */
public interface HostDao extends JpaRepository<Host, String>, JpaSpecificationExecutor<Host> {

    List<Host> findByassetipid(String assetipid);

    void deleteAllByAssetipid(String assetipid);

    Host findByHostname(String hostname);

    Host findByMacaddress(String macaddress);

    List<Host> findAllByAssetipid(String assetipid);

    Host findByAssetipidAndHostname(String assetipid, String hostname);

    @Modifying
    @Query(value = "DELETE FROM `tb_host` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

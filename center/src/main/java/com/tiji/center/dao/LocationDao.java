package com.tiji.center.dao;

import com.tiji.center.pojo.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * location数据访问接口
 *
 * @author 贰拾壹
 */
public interface LocationDao extends JpaRepository<Location, String>, JpaSpecificationExecutor<Location> {

    void deleteAllByAssetipid(String assetipid);

    @Modifying
    @Query(value = "DELETE FROM `tb_location` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

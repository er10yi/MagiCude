package com.tiji.center.dao;

import com.tiji.center.pojo.Checkresult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * checkresult数据访问接口
 *
 * @author 贰拾壹
 */
public interface CheckresultDao extends JpaRepository<Checkresult, String>, JpaSpecificationExecutor<Checkresult> {
    Checkresult findByAssetportidAndNameAndPassivetimeIsNull(String assetPortId, String pluginName);

    List<Checkresult> findAllByName(String name);

    List<Checkresult> deleteAllByAssetportid(String assetportId);

    List<Checkresult> findAllByAssetportid(String assetportid);

    @Modifying
    @Query(value = "DELETE FROM `tb_checkresult` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

package com.tiji.center.dao;

import com.tiji.center.pojo.Webrawdata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * webrawdata 数据访问接口
 * @author 贰拾壹
 *
 */
public interface WebrawdataDao extends JpaRepository<Webrawdata,String>,JpaSpecificationExecutor<Webrawdata>{

	@Modifying
    @Query(value = "DELETE FROM `tb_webrawdata` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

    void deleteAllByWebinfoid(String webinfoid);

    List<Webrawdata> findAllByWebinfoid(String webinfoid);
}

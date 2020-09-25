package com.tiji.center.dao;

import com.tiji.center.pojo.Notifylog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * notifylog数据访问接口
 *
 * @author 贰拾壹
 */
public interface NotifylogDao extends JpaRepository<Notifylog, String>, JpaSpecificationExecutor<Notifylog> {

    @Modifying
    @Query(value = "DELETE FROM `tb_notifylog` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

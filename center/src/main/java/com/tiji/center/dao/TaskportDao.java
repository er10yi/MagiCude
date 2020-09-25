package com.tiji.center.dao;

import com.tiji.center.pojo.Taskport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * taskport数据访问接口
 *
 * @author 贰拾壹
 */
public interface TaskportDao extends JpaRepository<Taskport, String>, JpaSpecificationExecutor<Taskport> {

    List<Taskport> findByTaskipidAndState(String taskIpId, String state);

    Taskport findByTaskipidAndPort(String taskipid, String taskport);

    List<Taskport> findByTaskipid(String taskIpId);

    List<Taskport> findByTaskipidAndServiceAndState(String taskipid, String service, String state);

    List<Taskport> findByTaskipidAndVersionAndState(String taskipid, String version, String state);

    void deleteAllByTaskipid(String taskipid);

    @Modifying
    @Query(value = "DELETE FROM `tb_taskport` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

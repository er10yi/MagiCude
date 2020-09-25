package com.tiji.center.dao;

import com.tiji.center.pojo.Taskpluginconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * taskpluginconfig数据访问接口
 *
 * @author 贰拾壹
 */
public interface TaskpluginconfigDao extends JpaRepository<Taskpluginconfig, String>, JpaSpecificationExecutor<Taskpluginconfig> {
    Taskpluginconfig findByTaskidAndPluginconfigid(String taskid, String pluginconfigid);

    List<Taskpluginconfig> findAllByTaskid(String taskid);

    void deleteAllByTaskid(String taskid);

    void deleteAllByTaskidAndPluginconfigid(String taskid, String pluginconfigid);

    void deleteAllByPluginconfigid(String pluginconfigid);

    @Modifying
    @Query(value = "DELETE FROM `tb_taskpluginconfig` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

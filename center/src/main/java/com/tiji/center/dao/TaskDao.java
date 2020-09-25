package com.tiji.center.dao;

import com.tiji.center.pojo.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * task数据访问接口
 *
 * @author 贰拾壹
 */
public interface TaskDao extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {
    List<Task> findAllByEndtimeIsNullAndStarttimeIsNotNull();

    List<Task> findAllByWorktypeAndEndtimeIsNotNullAndStarttimeIsNotNull(String worktype);

    List<Task> findAllByCrontaskIsTrueAndTaskparentidIsNull();

    List<Task> findAllByTaskparentid(String taskparentid);

    Task findByName(String name);

    @Modifying
    @Query(value = "DELETE FROM `tb_task` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

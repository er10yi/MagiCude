package com.tiji.center.dao;

import com.tiji.center.pojo.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * project数据访问接口
 *
 * @author 贰拾壹
 */
public interface ProjectDao extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {

    Project findByName(String name);

    @Modifying
    @Query(value = "DELETE FROM `tb_project` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

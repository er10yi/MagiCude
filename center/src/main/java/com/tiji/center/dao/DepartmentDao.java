package com.tiji.center.dao;

import com.tiji.center.pojo.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * department数据访问接口
 *
 * @author 贰拾壹
 */
public interface DepartmentDao extends JpaRepository<Department, String>, JpaSpecificationExecutor<Department> {

    Department findByDepartmentname(String departmentname);

    @Modifying
    @Query(value = "DELETE FROM `tb_department` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

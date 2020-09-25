package com.tiji.center.dao;

import com.tiji.center.pojo.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * solution数据访问接口
 *
 * @author 贰拾壹
 */
public interface SolutionDao extends JpaRepository<Solution, String>, JpaSpecificationExecutor<Solution> {

    List<Solution> findAllByVulnid(String vulnId);

    void deleteAllByVulnid(String vulnId);

    @Modifying
    @Query(value = "DELETE FROM `tb_solution` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

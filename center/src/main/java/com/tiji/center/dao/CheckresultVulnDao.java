package com.tiji.center.dao;

import com.tiji.center.pojo.CheckresultVuln;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * checkresultVuln数据访问接口
 *
 * @author 贰拾壹
 */
public interface CheckresultVulnDao extends JpaRepository<CheckresultVuln, String>, JpaSpecificationExecutor<CheckresultVuln> {
    List<CheckresultVuln> findAllByCheckresultid(String checkresultid);

    void deleteAllByCheckresultid(String checkresultid);
}

package com.tiji.center.dao;

import com.tiji.center.pojo.Cronjob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * cronjob数据访问接口
 *
 * @author 贰拾壹
 */
public interface CronjobDao extends JpaRepository<Cronjob, String>, JpaSpecificationExecutor<Cronjob> {
    Cronjob findByName(String name);
}

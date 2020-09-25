package com.tiji.center.dao;

import com.tiji.center.pojo.Sendmailconfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * sendmailconfig数据访问接口
 *
 * @author 贰拾壹
 */
public interface SendmailconfigDao extends JpaRepository<Sendmailconfig, String>, JpaSpecificationExecutor<Sendmailconfig> {

}

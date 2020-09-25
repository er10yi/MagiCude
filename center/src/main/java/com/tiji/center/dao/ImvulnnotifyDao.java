package com.tiji.center.dao;

import com.tiji.center.pojo.Imvulnnotify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * imvulnnotify数据访问接口
 *
 * @author 贰拾壹
 */
public interface ImvulnnotifyDao extends JpaRepository<Imvulnnotify, String>, JpaSpecificationExecutor<Imvulnnotify> {

}

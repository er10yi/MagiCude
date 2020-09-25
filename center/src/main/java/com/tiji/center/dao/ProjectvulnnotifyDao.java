package com.tiji.center.dao;

import com.tiji.center.pojo.Projectvulnnotify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * projectvulnnotify数据访问接口
 *
 * @author 贰拾壹
 */
public interface ProjectvulnnotifyDao extends JpaRepository<Projectvulnnotify, String>, JpaSpecificationExecutor<Projectvulnnotify> {

}

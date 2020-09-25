package com.tiji.center.dao;

import com.tiji.center.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * user数据访问接口
 *
 * @author 贰拾壹
 */
public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    @Modifying
    @Query(value = "DELETE FROM `tb_user` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

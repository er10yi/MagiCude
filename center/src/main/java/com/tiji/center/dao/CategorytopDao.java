package com.tiji.center.dao;

import com.tiji.center.pojo.Categorytop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * categorytop数据访问接口
 *
 * @author 贰拾壹
 */
public interface CategorytopDao extends JpaRepository<Categorytop, String>, JpaSpecificationExecutor<Categorytop> {
    Categorytop findByName(String name);

    @Modifying
    @Query(value = "DELETE FROM `tb_categorytop` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

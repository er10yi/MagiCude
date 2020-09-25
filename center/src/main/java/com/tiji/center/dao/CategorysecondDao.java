package com.tiji.center.dao;

import com.tiji.center.pojo.Categorysecond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * categorysecond数据访问接口
 *
 * @author 贰拾壹
 */
public interface CategorysecondDao extends JpaRepository<Categorysecond, String>, JpaSpecificationExecutor<Categorysecond> {
    Categorysecond findByName(String name);

    Categorysecond findByCategorytopid(String categorytopid);

    @Modifying
    @Query(value = "DELETE FROM `tb_categorysecond` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

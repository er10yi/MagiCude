package com.tiji.center.dao;

import com.tiji.center.pojo.Dictionaryusername;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * dictionaryusername数据访问接口
 *
 * @author 贰拾壹
 */
public interface DictionaryusernameDao extends JpaRepository<Dictionaryusername, String>, JpaSpecificationExecutor<Dictionaryusername> {

    Dictionaryusername findByUsername(String username);

    @Query(value = "SELECT username FROM `tb_dictionaryusername`", nativeQuery = true)
    List<String> findAllUsername();

    @Modifying
    @Query(value = "DELETE FROM `tb_dictionaryusername` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

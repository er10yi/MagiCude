package com.tiji.center.dao;

import com.tiji.center.pojo.Dictionarypassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * dictionarypassword数据访问接口
 *
 * @author 贰拾壹
 */
public interface DictionarypasswordDao extends JpaRepository<Dictionarypassword, String>, JpaSpecificationExecutor<Dictionarypassword> {

    Dictionarypassword findByPassword(String password);

    @Query(value = "SELECT `password` FROM `tb_dictionarypassword`", nativeQuery = true)
    List<String> findAllPassword();


    @Modifying
    @Query(value = "DELETE FROM `tb_dictionarypassword` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);
}

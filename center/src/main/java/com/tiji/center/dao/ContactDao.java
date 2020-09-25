package com.tiji.center.dao;

import com.tiji.center.pojo.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * contact数据访问接口
 *
 * @author 贰拾壹
 */
public interface ContactDao extends JpaRepository<Contact, String>, JpaSpecificationExecutor<Contact> {

    Contact findByNameAndEmail(String name, String email);

    @Modifying
    @Query(value = "DELETE FROM `tb_contact` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

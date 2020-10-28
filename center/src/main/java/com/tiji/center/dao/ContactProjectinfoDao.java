package com.tiji.center.dao;

import com.tiji.center.pojo.ContactProjectinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * contactProjectinfo数据访问接口
 *
 * @author 贰拾壹
 */
public interface ContactProjectinfoDao extends JpaRepository<ContactProjectinfo, String>, JpaSpecificationExecutor<ContactProjectinfo> {

    void deleteAllByContactid(String contactid);

    void deleteAllByProjectinfoid(String projectinfoid);

    List<ContactProjectinfo> findAllByProjectinfoid(String projectinfoid);

    ContactProjectinfo findByContactidAndProjectinfoid(String contactid, String projectinfoid);

    @Modifying
    @Query(value = "DELETE FROM `tb_contact_projectinfo` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

    void deleteByContactidAndProjectinfoid(String contactid, String projectinfoid);

}

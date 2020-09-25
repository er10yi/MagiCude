package com.tiji.center.dao;

import com.tiji.center.pojo.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * agent数据访问接口
 *
 * @author 贰拾壹
 */
public interface AgentDao extends JpaRepository<Agent, String>, JpaSpecificationExecutor<Agent> {
    Agent findByNameAndIpaddress(String name, String ipaddress);

    @Modifying
    @Query(value = "UPDATE `tb_agent` SET `online` = FALSE", nativeQuery = true)
    void updateAgentSetOnlineFalse();

    List<Agent> findAllByOnline(Boolean online);

    @Modifying
    @Query(value = "DELETE FROM `tb_agent` WHERE id IN(?1)", nativeQuery = true)
    void deleteAllByIds(List<String> ids);

}

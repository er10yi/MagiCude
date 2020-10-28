package com.tiji.center.service;

import com.tiji.center.dao.AgentDao;
import com.tiji.center.pojo.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * agent服务层
 *
 * @author 贰拾壹
 */
@Service
public class AgentService {

    @Autowired
    private AgentDao agentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Agent> findAll() {
        return agentDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Agent> findSearch(Map whereMap, int page, int size) {
        Specification<Agent> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return agentDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Agent> findSearch(Map whereMap) {
        Specification<Agent> specification = createSpecification(whereMap);
        return agentDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Agent findById(String id) {
        return agentDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param agent
     */
    public void add(Agent agent) {
        if (Objects.isNull(agent.getId())) {
            agent.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(agent.getOnline())) {
            agent.setOnline(false);
        }
        agentDao.save(agent);
    }

    /**
     * 修改
     *
     * @param agent
     */
    public void update(Agent agent) {
        agentDao.save(agent);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        agentDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        agentDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Agent> createSpecification(Map searchMap) {

        return (Specification<Agent>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // agent编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // agent名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
            }
            // nmap路径
            if (searchMap.get("nmappath") != null && !"".equals(searchMap.get("nmappath"))) {
                predicateList.add(cb.like(root.get("nmappath").as(String.class), "%" + searchMap.get("nmappath") + "%"));
            }
            // mass路径
            if (searchMap.get("masspath") != null && !"".equals(searchMap.get("masspath"))) {
                predicateList.add(cb.like(root.get("masspath").as(String.class), "%" + searchMap.get("masspath") + "%"));
            }
            // ip地址
            if (searchMap.get("ipaddress") != null && !"".equals(searchMap.get("ipaddress"))) {
                predicateList.add(cb.like(root.get("ipaddress").as(String.class), "%" + searchMap.get("ipaddress") + "%"));
            }
            //在线
            if (searchMap.get("online") != null && !"".equals(searchMap.get("online"))) {
                predicateList.add(cb.equal(root.get("online").as(Boolean.class), (searchMap.get("online"))));
            }
            // 超时次数
            if (searchMap.get("timeouts") != null && !"".equals(searchMap.get("timeouts"))) {
                predicateList.add(cb.like(root.get("timeouts").as(String.class), "%" + searchMap.get("timeouts") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[0]));

        };

    }

    /**
     * 根据name,ipaddress查询agent
     *
     * @param name
     * @return
     */
    public Agent findByNameAndIpaddress(String name, String ipaddress) {
        return agentDao.findByNameAndIpaddress(name, ipaddress);
    }


    /**
     * 将agent的online标志置false
     *
     * @return
     */
    @Transactional(value = "masterTransactionManager")
    public void updateAgentSetOnlineFalse() {
        agentDao.updateAgentSetOnlineFalse();
    }

    /**
     * 查询online的agent
     *
     * @return
     */
    public List<Agent> findAllByOnline(Boolean online) {
        return agentDao.findAllByOnline(online);
    }

}

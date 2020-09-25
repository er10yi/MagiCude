package com.tiji.center.service;

import com.tiji.center.dao.RiskserviceDao;
import com.tiji.center.pojo.Riskservice;
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
 * riskservice服务层
 *
 * @author 贰拾壹
 */
@Service
public class RiskserviceService {

    @Autowired
    private RiskserviceDao riskserviceDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Riskservice> findAll() {
        return riskserviceDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Riskservice> findSearch(Map whereMap, int page, int size) {
        Specification<Riskservice> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return riskserviceDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Riskservice> findSearch(Map whereMap) {
        Specification<Riskservice> specification = createSpecification(whereMap);
        return riskserviceDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Riskservice findById(String id) {
        return riskserviceDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param riskservice
     */
    public void add(Riskservice riskservice) {
        if (Objects.isNull(riskservice.getId())) {
            riskservice.setId(idWorker.nextId() + "");
        }
        riskserviceDao.save(riskservice);
    }

    /**
     * 修改
     *
     * @param riskservice
     */
    public void update(Riskservice riskservice) {
        riskserviceDao.save(riskservice);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        riskserviceDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        riskserviceDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Riskservice> createSpecification(Map searchMap) {

        return (Specification<Riskservice>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 高危服务
            if (searchMap.get("service") != null && !"".equals(searchMap.get("service"))) {
                predicateList.add(cb.like(root.get("service").as(String.class), "%" + (String) searchMap.get("service") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 根据service查询实体
     *
     * @param service
     * @return
     */
    public Riskservice findByService(String service) {
        return riskserviceDao.findByService(service);
    }

}

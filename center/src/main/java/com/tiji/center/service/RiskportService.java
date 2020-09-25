package com.tiji.center.service;

import com.tiji.center.dao.RiskportDao;
import com.tiji.center.pojo.Riskport;
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
 * riskport服务层
 *
 * @author 贰拾壹
 */
@Service
public class RiskportService {

    @Autowired
    private RiskportDao riskportDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Riskport> findAll() {
        return riskportDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Riskport> findSearch(Map whereMap, int page, int size) {
        Specification<Riskport> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return riskportDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Riskport> findSearch(Map whereMap) {
        Specification<Riskport> specification = createSpecification(whereMap);
        return riskportDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Riskport findById(String id) {
        return riskportDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param riskport
     */
    public void add(Riskport riskport) {
        if (Objects.isNull(riskport.getId())) {
            riskport.setId(idWorker.nextId() + "");
        }
        riskportDao.save(riskport);
    }

    /**
     * 修改
     *
     * @param riskport
     */
    public void update(Riskport riskport) {
        riskportDao.save(riskport);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        riskportDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        riskportDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Riskport> createSpecification(Map searchMap) {

        return (Specification<Riskport>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 高危端口
            if (searchMap.get("port") != null && !"".equals(searchMap.get("port"))) {
                predicateList.add(cb.like(root.get("port").as(String.class), "%" + (String) searchMap.get("port") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据port查询实体
     *
     * @param port
     * @return
     */
    public Riskport findByPort(String port) {
        return riskportDao.findByPort(port);
    }
}

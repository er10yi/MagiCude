package com.tiji.center.service;

import com.tiji.center.dao.RiskversionDao;
import com.tiji.center.pojo.Riskversion;
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
 * riskversion服务层
 *
 * @author 贰拾壹
 */
@Service
public class RiskversionService {

    @Autowired
    private RiskversionDao riskversionDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Riskversion> findAll() {
        return riskversionDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Riskversion> findSearch(Map whereMap, int page, int size) {
        Specification<Riskversion> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return riskversionDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Riskversion> findSearch(Map whereMap) {
        Specification<Riskversion> specification = createSpecification(whereMap);
        return riskversionDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Riskversion findById(String id) {
        return riskversionDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param riskversion
     */
    public void add(Riskversion riskversion) {
        if (Objects.isNull(riskversion.getId())) {
            riskversion.setId(idWorker.nextId() + "");
        }
        riskversionDao.save(riskversion);
    }

    /**
     * 修改
     *
     * @param riskversion
     */
    public void update(Riskversion riskversion) {
        riskversionDao.save(riskversion);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        riskversionDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        riskversionDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Riskversion> createSpecification(Map searchMap) {

        return (Specification<Riskversion>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 高危版本
            if (searchMap.get("version") != null && !"".equals(searchMap.get("version"))) {
                predicateList.add(cb.like(root.get("version").as(String.class), "%" + (String) searchMap.get("version") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据version查询实体
     *
     * @param version
     * @return
     */
    public Riskversion findByVersion(String version) {
        return riskversionDao.findByVersion(version);
    }

}

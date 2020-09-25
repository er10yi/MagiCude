package com.tiji.center.service;

import com.tiji.center.dao.CronjobDao;
import com.tiji.center.pojo.Cronjob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * cronjob服务层
 *
 * @author 贰拾壹
 */
@Service
public class CronjobService {

    @Autowired
    private CronjobDao cronjobDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Cronjob> findAll() {
        return cronjobDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Cronjob> findSearch(Map whereMap, int page, int size) {
        Specification<Cronjob> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return cronjobDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Cronjob> findSearch(Map whereMap) {
        Specification<Cronjob> specification = createSpecification(whereMap);
        return cronjobDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Cronjob findById(String id) {
        return cronjobDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param cronjob
     */
    public void add(Cronjob cronjob) {
        if (Objects.isNull(cronjob.getId())) {
            cronjob.setId(idWorker.nextId() + "");
        }
        cronjobDao.save(cronjob);
    }

    /**
     * 修改
     *
     * @param cronjob
     */
    public void update(Cronjob cronjob) {
        cronjobDao.save(cronjob);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        cronjobDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Cronjob> createSpecification(Map searchMap) {

        return (Specification<Cronjob>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + (String) searchMap.get("name") + "%"));
            }
            // cron表达式
            if (searchMap.get("cronexpression") != null && !"".equals(searchMap.get("cronexpression"))) {
                predicateList.add(cb.like(root.get("cronexpression").as(String.class), "%" + (String) searchMap.get("cronexpression") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据name查询实体
     *
     * @param name
     * @return
     */
    public Cronjob findByName(String name) {
        return cronjobDao.findByName(name);
    }


}

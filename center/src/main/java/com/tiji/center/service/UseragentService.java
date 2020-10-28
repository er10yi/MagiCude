package com.tiji.center.service;

import com.tiji.center.dao.UseragentDao;
import com.tiji.center.pojo.Useragent;
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
 * useragent服务层
 *
 * @author 贰拾壹
 */
@Service
public class UseragentService {

    @Autowired
    private UseragentDao useragentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Useragent> findAll() {
        return useragentDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Useragent> findSearch(Map whereMap, int page, int size) {
        Specification<Useragent> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return useragentDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Useragent> findSearch(Map whereMap) {
        Specification<Useragent> specification = createSpecification(whereMap);
        return useragentDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Useragent findById(String id) {
        return useragentDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param useragent
     */
    public void add(Useragent useragent) {
        if (Objects.isNull(useragent.getId())) {
            useragent.setId(idWorker.nextId() + "");
        }
        useragentDao.save(useragent);
    }

    /**
     * 修改
     *
     * @param useragent
     */
    public void update(Useragent useragent) {
        useragentDao.save(useragent);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        useragentDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        useragentDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Useragent> createSpecification(Map searchMap) {

        return (Specification<Useragent>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // useragent
            if (searchMap.get("useragent") != null && !"".equals(searchMap.get("useragent"))) {
                predicateList.add(cb.like(root.get("useragent").as(String.class), "%" + searchMap.get("useragent") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 查询全部不重复的agent
     *
     * @return
     */
    public List<String> findAllDistinctUserAgentList() {
        return useragentDao.findAllDistinctUserAgentList();
    }


    /**
     * 根据ua查询实体
     *
     * @param ua
     * @return
     */
    public Useragent findByUseragent(String ua) {
        return useragentDao.findByUseragent(ua);
    }

    /**
     * 批量增加
     *
     * @param useragentList
     */
    public void batchAdd(List<Useragent> useragentList) {
        useragentDao.saveAll(useragentList);
    }
}

package com.tiji.center.service;

import com.tiji.center.dao.DomainwhitelistDao;
import com.tiji.center.pojo.Domainwhitelist;
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
 * domainwhitelist服务层
 *
 * @author 贰拾壹
 */
@Service
public class DomainwhitelistService {

    @Autowired
    private DomainwhitelistDao domainwhitelistDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Domainwhitelist> findAll() {
        return domainwhitelistDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Domainwhitelist> findSearch(Map whereMap, int page, int size) {
        Specification<Domainwhitelist> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return domainwhitelistDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Domainwhitelist> findSearch(Map whereMap) {
        Specification<Domainwhitelist> specification = createSpecification(whereMap);
        return domainwhitelistDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Domainwhitelist findById(String id) {
        return domainwhitelistDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param domainwhitelist
     */
    public void add(Domainwhitelist domainwhitelist) {
        if (Objects.isNull(domainwhitelist.getId())) {
            domainwhitelist.setId(idWorker.nextId() + "");
        }
        domainwhitelistDao.save(domainwhitelist);
    }

    /**
     * 修改
     *
     * @param domainwhitelist
     */
    public void update(Domainwhitelist domainwhitelist) {
        domainwhitelistDao.save(domainwhitelist);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        domainwhitelistDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        domainwhitelistDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Domainwhitelist> createSpecification(Map searchMap) {

        return (Specification<Domainwhitelist>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 参数编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 域名
            if (searchMap.get("domain") != null && !"".equals(searchMap.get("domain"))) {
                predicateList.add(cb.like(root.get("domain").as(String.class), "%" + (String) searchMap.get("domain") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<String> findAllDistinct() {
        return domainwhitelistDao.findAllDistinct();
    }

    /**
     * 根据domain查询实体
     *
     * @param domain
     * @return
     */
    public Domainwhitelist findByDomain(String domain) {
        return domainwhitelistDao.findByDomain(domain);
    }

    /**
     * 批量增加
     *
     * @param domainwhitelistList
     */
    public void batchAdd(List<Domainwhitelist> domainwhitelistList) {
        domainwhitelistDao.saveAll(domainwhitelistList);
    }
}

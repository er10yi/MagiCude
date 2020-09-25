package com.tiji.center.service;

import com.tiji.center.dao.TitlewhitelistDao;
import com.tiji.center.pojo.Titlewhitelist;
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
 * titlewhitelist服务层
 *
 * @author 贰拾壹
 */
@Service
public class TitlewhitelistService {

    @Autowired
    private TitlewhitelistDao titlewhitelistDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Titlewhitelist> findAll() {
        return titlewhitelistDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Titlewhitelist> findSearch(Map whereMap, int page, int size) {
        Specification<Titlewhitelist> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return titlewhitelistDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Titlewhitelist> findSearch(Map whereMap) {
        Specification<Titlewhitelist> specification = createSpecification(whereMap);
        return titlewhitelistDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Titlewhitelist findById(String id) {
        return titlewhitelistDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param titlewhitelist
     */
    public void add(Titlewhitelist titlewhitelist) {
        if (Objects.isNull(titlewhitelist.getId())) {
            titlewhitelist.setId(idWorker.nextId() + "");
        }
        titlewhitelistDao.save(titlewhitelist);
    }

    /**
     * 修改
     *
     * @param titlewhitelist
     */
    public void update(Titlewhitelist titlewhitelist) {
        titlewhitelistDao.save(titlewhitelist);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        titlewhitelistDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        titlewhitelistDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Titlewhitelist> createSpecification(Map searchMap) {

        return (Specification<Titlewhitelist>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 参数编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 标题
            if (searchMap.get("title") != null && !"".equals(searchMap.get("title"))) {
                predicateList.add(cb.like(root.get("title").as(String.class), "%" + (String) searchMap.get("title") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据title查询实体
     *
     * @param title
     * @return
     */
    public Titlewhitelist findByTitle(String title) {
        return titlewhitelistDao.findByTitle(title);
    }

}

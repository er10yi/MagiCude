package com.tiji.center.service;

import com.tiji.center.dao.DictionaryusernameDao;
import com.tiji.center.pojo.Dictionaryusername;
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
 * dictionaryusername服务层
 *
 * @author 贰拾壹
 */
@Service
public class DictionaryusernameService {

    @Autowired
    private DictionaryusernameDao dictionaryusernameDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Dictionaryusername> findAll() {
        return dictionaryusernameDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Dictionaryusername> findSearch(Map whereMap, int page, int size) {
        Specification<Dictionaryusername> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return dictionaryusernameDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Dictionaryusername> findSearch(Map whereMap) {
        Specification<Dictionaryusername> specification = createSpecification(whereMap);
        return dictionaryusernameDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Dictionaryusername findById(String id) {
        return dictionaryusernameDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param dictionaryusername
     */
    public void add(Dictionaryusername dictionaryusername) {
        if (Objects.isNull(dictionaryusername.getId())) {
            dictionaryusername.setId(idWorker.nextId() + "");
        }
        dictionaryusernameDao.save(dictionaryusername);
    }

    /**
     * 修改
     *
     * @param dictionaryusername
     */
    public void update(Dictionaryusername dictionaryusername) {
        dictionaryusernameDao.save(dictionaryusername);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        dictionaryusernameDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        dictionaryusernameDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Dictionaryusername> createSpecification(Map searchMap) {

        return (Specification<Dictionaryusername>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 字典编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 字典用户名
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                predicateList.add(cb.like(root.get("username").as(String.class), "%" + (String) searchMap.get("username") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据username查询实体
     *
     * @param username
     * @return
     */
    public Dictionaryusername findByUsername(String username) {
        return dictionaryusernameDao.findByUsername(username);
    }

    /**
     * 查询所有username
     *
     * @return
     */
    public List<String> findAllUsername() {
        return dictionaryusernameDao.findAllUsername();
    }
}

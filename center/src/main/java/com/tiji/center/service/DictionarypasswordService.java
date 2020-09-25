package com.tiji.center.service;

import com.tiji.center.dao.DictionarypasswordDao;
import com.tiji.center.pojo.Dictionarypassword;
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
 * dictionarypassword服务层
 *
 * @author 贰拾壹
 */
@Service
public class DictionarypasswordService {

    @Autowired
    private DictionarypasswordDao dictionarypasswordDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Dictionarypassword> findAll() {
        return dictionarypasswordDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Dictionarypassword> findSearch(Map whereMap, int page, int size) {
        Specification<Dictionarypassword> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return dictionarypasswordDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Dictionarypassword> findSearch(Map whereMap) {
        Specification<Dictionarypassword> specification = createSpecification(whereMap);
        return dictionarypasswordDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Dictionarypassword findById(String id) {
        return dictionarypasswordDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param dictionarypassword
     */
    public void add(Dictionarypassword dictionarypassword) {
        if (Objects.isNull(dictionarypassword.getId())) {
            dictionarypassword.setId(idWorker.nextId() + "");
        }
        dictionarypasswordDao.save(dictionarypassword);
    }

    /**
     * 修改
     *
     * @param dictionarypassword
     */
    public void update(Dictionarypassword dictionarypassword) {
        dictionarypasswordDao.save(dictionarypassword);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        dictionarypasswordDao.deleteById(id);
    }


    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        dictionarypasswordDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Dictionarypassword> createSpecification(Map searchMap) {

        return (Specification<Dictionarypassword>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 字典编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 字典密码
            if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
                predicateList.add(cb.like(root.get("password").as(String.class), "%" + searchMap.get("password") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据password查询实体
     *
     * @param password
     * @return
     */
    public Dictionarypassword findByPassword(String password) {
        return dictionarypasswordDao.findByPassword(password);
    }

    /**
     * 查询所有password
     *
     * @return
     */
    public List<String> findAllPassword() {
        return dictionarypasswordDao.findAllPassword();
    }
}

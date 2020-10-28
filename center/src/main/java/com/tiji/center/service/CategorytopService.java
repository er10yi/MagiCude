package com.tiji.center.service;

import com.tiji.center.dao.CategorytopDao;
import com.tiji.center.pojo.Categorytop;
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
 * categorytop服务层
 *
 * @author 贰拾壹
 */
@Service
public class CategorytopService {

    @Autowired
    private CategorytopDao categorytopDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Categorytop> findAll() {
        return categorytopDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Categorytop> findSearch(Map whereMap, int page, int size) {
        Specification<Categorytop> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return categorytopDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Categorytop> findSearch(Map whereMap) {
        Specification<Categorytop> specification = createSpecification(whereMap);
        return categorytopDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Categorytop findById(String id) {
        return categorytopDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param categorytop
     */
    public void add(Categorytop categorytop) {
        if (Objects.isNull(categorytop.getId())) {
            categorytop.setId(idWorker.nextId() + "");
        }
        categorytopDao.save(categorytop);
    }

    /**
     * 修改
     *
     * @param categorytop
     */
    public void update(Categorytop categorytop) {
        categorytopDao.save(categorytop);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        categorytopDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        categorytopDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Categorytop> createSpecification(Map searchMap) {

        return (Specification<Categorytop>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 漏洞一级分类编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 漏洞一级分类名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + (String) searchMap.get("name") + "%"));
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
    public Categorytop findByName(String name) {
        return categorytopDao.findByName(name);
    }
}

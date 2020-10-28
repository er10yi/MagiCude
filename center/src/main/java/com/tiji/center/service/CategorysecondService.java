package com.tiji.center.service;

import com.tiji.center.dao.CategorysecondDao;
import com.tiji.center.pojo.Categorysecond;
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
 * categorysecond服务层
 *
 * @author 贰拾壹
 */
@Service
public class CategorysecondService {

    @Autowired
    private CategorysecondDao categorysecondDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Categorysecond> findAll() {
        return categorysecondDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Categorysecond> findSearch(Map whereMap, int page, int size) {
        Specification<Categorysecond> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return categorysecondDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Categorysecond> findSearch(Map whereMap) {
        Specification<Categorysecond> specification = createSpecification(whereMap);
        return categorysecondDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Categorysecond findById(String id) {
        return categorysecondDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param categorysecond
     */
    public void add(Categorysecond categorysecond) {
        if (Objects.isNull(categorysecond.getId())) {
            categorysecond.setId(idWorker.nextId() + "");
        }
        categorysecondDao.save(categorysecond);
    }

    /**
     * 修改
     *
     * @param categorysecond
     */
    public void update(Categorysecond categorysecond) {
        categorysecondDao.save(categorysecond);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        categorysecondDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        categorysecondDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Categorysecond> createSpecification(Map searchMap) {

        return (Specification<Categorysecond>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 漏洞二级分类编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 漏洞一级分类编号
            if (searchMap.get("categorytopid") != null && !"".equals(searchMap.get("categorytopid"))) {
                predicateList.add(cb.like(root.get("categorytopid").as(String.class), "%" + searchMap.get("categorytopid") + "%"));
            }
            // 漏洞二级分类类型
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
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
    public Categorysecond findByName(String name) {
        return categorysecondDao.findByName(name);
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    public List<String> findByIds(String[] ids) {
        List<String> assetIpIdAndIpList = new ArrayList<>();
        for (String id : ids) {
            Categorysecond categorysecond = findById(id);
            if (Objects.isNull(categorysecond)) {
                assetIpIdAndIpList.add(id + "-" + null);
            } else {
                assetIpIdAndIpList.add(id + "-" + categorysecond.getName());
            }
        }
        return assetIpIdAndIpList.isEmpty() ? null : assetIpIdAndIpList;
    }

    /**
     * 根据categorytopid查询实体
     *
     * @param categorytopid
     * @return
     */
    public Categorysecond findByCategorytopid(String categorytopid) {
        return categorysecondDao.findByCategorytopid(categorytopid);
    }

}

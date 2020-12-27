package com.tiji.center.service;

import com.tiji.center.dao.DepartmentDao;
import com.tiji.center.pojo.Department;
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
 * department服务层
 *
 * @author 贰拾壹
 */
@Service
public class DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Department> findAll() {
        return departmentDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Department> findSearch(Map whereMap, int page, int size) {
        Specification<Department> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return departmentDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Department> findSearch(Map whereMap) {
        Specification<Department> specification = createSpecification(whereMap);
        return departmentDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Department findById(String id) {
        return departmentDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param department
     */
    public void add(Department department) {
        if (Objects.isNull(department.getId())) {
            department.setId(idWorker.nextId() + "");
        }
        departmentDao.save(department);
    }

    /**
     * 修改
     *
     * @param department
     */
    public void update(Department department) {
        departmentDao.save(department);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        departmentDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        departmentDao.deleteAllByIds(ids);
    }


    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Department> createSpecification(Map searchMap) {

        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 部门名称
            if (searchMap.get("departmentname") != null && !"".equals(searchMap.get("departmentname"))) {
                predicateList.add(cb.like(root.get("departmentname").as(String.class), "%" + searchMap.get("departmentname") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据部门名称查询
     *
     * @return
     */
    public Department findByDepartmentname(String departmentname) {
        return departmentDao.findByDepartmentname(departmentname);
    }

}

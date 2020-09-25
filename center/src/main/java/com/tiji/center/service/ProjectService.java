package com.tiji.center.service;

import com.tiji.center.dao.ProjectDao;
import com.tiji.center.pojo.Project;
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
 * project服务层
 *
 * @author 贰拾壹
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Project> findAll() {
        return projectDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Project> findSearch(Map whereMap, int page, int size) {
        Specification<Project> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return projectDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Project> findSearch(Map whereMap) {
        Specification<Project> specification = createSpecification(whereMap);
        return projectDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Project findById(String id) {
        return projectDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param project
     */
    public void add(Project project) {
        if (Objects.isNull(project.getId())) {
            project.setId(idWorker.nextId() + "");
        }
        projectDao.save(project);
    }

    /**
     * 修改
     *
     * @param project
     */
    public void update(Project project) {
        projectDao.save(project);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        projectDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        projectDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Project> createSpecification(Map searchMap) {

        return (Specification<Project>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 项目编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 项目名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + (String) searchMap.get("name") + "%"));
            }
            // 项目描述
            if (searchMap.get("description") != null && !"".equals(searchMap.get("description"))) {
                predicateList.add(cb.like(root.get("description").as(String.class), "%" + (String) searchMap.get("description") + "%"));
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
    public Project findByIName(String name) {
        return projectDao.findByName(name);
    }
}

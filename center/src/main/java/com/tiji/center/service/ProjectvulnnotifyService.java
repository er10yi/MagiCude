package com.tiji.center.service;

import com.tiji.center.dao.ProjectvulnnotifyDao;
import com.tiji.center.pojo.Projectvulnnotify;
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
 * projectvulnnotify服务层
 *
 * @author 贰拾壹
 */
@Service
public class ProjectvulnnotifyService {

    @Autowired
    private ProjectvulnnotifyDao projectvulnnotifyDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Projectvulnnotify> findAll() {
        return projectvulnnotifyDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Projectvulnnotify> findSearch(Map whereMap, int page, int size) {
        Specification<Projectvulnnotify> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return projectvulnnotifyDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Projectvulnnotify> findSearch(Map whereMap) {
        Specification<Projectvulnnotify> specification = createSpecification(whereMap);
        return projectvulnnotifyDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Projectvulnnotify findById(String id) {
        return projectvulnnotifyDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param projectvulnnotify
     */
    public void add(Projectvulnnotify projectvulnnotify) {
        if (Objects.isNull(projectvulnnotify.getId())) {
            projectvulnnotify.setId(idWorker.nextId() + "");
        }
        projectvulnnotifyDao.save(projectvulnnotify);
    }

    /**
     * 修改
     *
     * @param projectvulnnotify
     */
    public void update(Projectvulnnotify projectvulnnotify) {
        projectvulnnotifyDao.save(projectvulnnotify);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        projectvulnnotifyDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Projectvulnnotify> createSpecification(Map searchMap) {

        return (Specification<Projectvulnnotify>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 风险等级
            if (searchMap.get("risk") != null && !"".equals(searchMap.get("risk"))) {
                predicateList.add(cb.like(root.get("risk").as(String.class), "%" + (String) searchMap.get("risk") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

}

package com.tiji.center.service;

import com.tiji.center.dao.ProjectportwhitelistDao;
import com.tiji.center.pojo.Projectportwhitelist;
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
 * projectportwhitelist服务层
 *
 * @author 贰拾壹
 */
@Service
public class ProjectportwhitelistService {

    @Autowired
    private ProjectportwhitelistDao projectportwhitelistDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Projectportwhitelist> findAll() {
        return projectportwhitelistDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Projectportwhitelist> findSearch(Map whereMap, int page, int size) {
        Specification<Projectportwhitelist> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return projectportwhitelistDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Projectportwhitelist> findSearch(Map whereMap) {
        Specification<Projectportwhitelist> specification = createSpecification(whereMap);
        return projectportwhitelistDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Projectportwhitelist findById(String id) {
        return projectportwhitelistDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param projectportwhitelist
     */
    public void add(Projectportwhitelist projectportwhitelist) {
        if (Objects.isNull(projectportwhitelist.getId())) {
            projectportwhitelist.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(projectportwhitelist.getCheckwhitelist())) {
            projectportwhitelist.setCheckwhitelist(false);
        }
        if (Objects.isNull(projectportwhitelist.getNotifywhitelist())) {
            projectportwhitelist.setNotifywhitelist(false);
        }
        projectportwhitelistDao.save(projectportwhitelist);
    }

    /**
     * 修改
     *
     * @param projectportwhitelist
     */
    public void update(Projectportwhitelist projectportwhitelist) {
        projectportwhitelistDao.save(projectportwhitelist);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        projectportwhitelistDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        projectportwhitelistDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Projectportwhitelist> createSpecification(Map searchMap) {

        return (Specification<Projectportwhitelist>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 项目信息编号
            if (searchMap.get("projectinfoid") != null && !"".equals(searchMap.get("projectinfoid"))) {
                predicateList.add(cb.like(root.get("projectinfoid").as(String.class), "%" + searchMap.get("projectinfoid") + "%"));
            }
            // 端口
            if (searchMap.get("port") != null && !"".equals(searchMap.get("port"))) {
                predicateList.add(cb.like(root.get("port").as(String.class), "%" + searchMap.get("port") + "%"));
            }
            //安全检测白名单
            if (searchMap.get("checkwhitelist") != null && !"".equals(searchMap.get("checkwhitelist"))) {

                predicateList.add(cb.equal(root.get("checkwhitelist").as(Boolean.class), (searchMap.get("checkwhitelist"))));
            }
            //提醒白名单
            if (searchMap.get("notifywhitelist") != null && !"".equals(searchMap.get("notifywhitelist"))) {

                predicateList.add(cb.equal(root.get("notifywhitelist").as(Boolean.class), (searchMap.get("notifywhitelist"))));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 根据projectInfoId、port查询实体
     *
     * @param projectInfoId
     * @param port
     * @return
     */
    public Projectportwhitelist findByProjectinfoidAndPort(String projectInfoId, String port) {
        return projectportwhitelistDao.findByProjectinfoidAndPort(projectInfoId, port);
    }

    /**
     * 根据projectinfoid批量删除
     *
     * @param projectinfoid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByProjectinfoid(String projectinfoid) {
        projectportwhitelistDao.deleteAllByProjectinfoid(projectinfoid);
    }

    /**
     * 根据projectinfoid查询所有端口
     *
     * @param projectinfoid
     * @return
     */
    public List<String> findAllPortByProjectinfoid(String projectinfoid) {
        return projectportwhitelistDao.findAllPortByProjectinfoid(projectinfoid);
    }

}

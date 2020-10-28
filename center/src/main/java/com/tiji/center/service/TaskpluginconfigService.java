package com.tiji.center.service;

import com.tiji.center.dao.TaskpluginconfigDao;
import com.tiji.center.pojo.Taskpluginconfig;
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
 * taskpluginconfig服务层
 *
 * @author 贰拾壹
 */
@Service
public class TaskpluginconfigService {

    @Autowired
    private TaskpluginconfigDao taskpluginconfigDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Taskpluginconfig> findAll() {
        return taskpluginconfigDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Taskpluginconfig> findSearch(Map whereMap, int page, int size) {
        Specification<Taskpluginconfig> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return taskpluginconfigDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Taskpluginconfig> findSearch(Map whereMap) {
        Specification<Taskpluginconfig> specification = createSpecification(whereMap);
        return taskpluginconfigDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Taskpluginconfig findById(String id) {
        return taskpluginconfigDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param taskpluginconfig
     */
    public void add(Taskpluginconfig taskpluginconfig) {
        if (Objects.isNull(taskpluginconfig.getId())) {
            taskpluginconfig.setId(idWorker.nextId() + "");
        }
        taskpluginconfigDao.save(taskpluginconfig);
    }

    /**
     * 修改
     *
     * @param taskpluginconfig
     */
    public void update(Taskpluginconfig taskpluginconfig) {
        taskpluginconfigDao.save(taskpluginconfig);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        taskpluginconfigDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        taskpluginconfigDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Taskpluginconfig> createSpecification(Map searchMap) {

        return (Specification<Taskpluginconfig>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 任务编号
            if (searchMap.get("taskid") != null && !"".equals(searchMap.get("taskid"))) {
                predicateList.add(cb.equal(root.get("taskid").as(String.class), searchMap.get("taskid")));
            }
            // 插件编号
            if (searchMap.get("pluginconfigid") != null && !"".equals(searchMap.get("pluginconfigid"))) {
                predicateList.add(cb.equal(root.get("pluginconfigid").as(String.class), searchMap.get("pluginconfigid")));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据taskid pluginconfigid查询实体
     *
     * @param taskid
     * @param pluginconfigid
     * @return
     */
    public Taskpluginconfig findByTaskidAndPluginconfigid(String taskid, String pluginconfigid) {
        return taskpluginconfigDao.findByTaskidAndPluginconfigid(taskid, pluginconfigid);
    }


    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Taskpluginconfig> findAllByTaskid(String taskid) {
        return taskpluginconfigDao.findAllByTaskid(taskid);
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<String> findPluginconfigidByTaskid(String taskid) {
        return taskpluginconfigDao.findPluginconfigidByTaskid(taskid);
    }

    /**
     * 删除
     *
     * @param taskid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByTaskid(String taskid) {
        taskpluginconfigDao.deleteAllByTaskid(taskid);
    }

    /**
     * 删除
     *
     * @param pluginconfigid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByPluginconfigid(String pluginconfigid) {
        taskpluginconfigDao.deleteAllByPluginconfigid(pluginconfigid);
    }


    /**
     * 删除
     *
     * @param taskid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByTaskidAndPluginconfigid(String taskid, String pluginconfigid) {
        taskpluginconfigDao.deleteAllByTaskidAndPluginconfigid(taskid, pluginconfigid);
    }


    /**
     * 批量增加
     *
     * @param taskpluginconfigList
     */
    public void batchAdd(List<Taskpluginconfig> taskpluginconfigList) {
        taskpluginconfigDao.saveAll(taskpluginconfigList);
    }
}

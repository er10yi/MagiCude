package com.tiji.center.service;

import com.tiji.center.dao.TaskportDao;
import com.tiji.center.pojo.Taskport;
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
 * taskport服务层
 *
 * @author 贰拾壹
 */
@Service
public class TaskportService {

    @Autowired
    private TaskportDao taskportDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Taskport> findAll() {
        return taskportDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Taskport> findSearch(Map whereMap, int page, int size) {
        Specification<Taskport> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return taskportDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Taskport> findSearch(Map whereMap) {
        Specification<Taskport> specification = createSpecification(whereMap);
        return taskportDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Taskport findById(String id) {
        return taskportDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param taskport
     */
    public void add(Taskport taskport) {
        if (Objects.isNull(taskport.getId())) {
            taskport.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(taskport.getCheckwhitelist())) {
            taskport.setCheckwhitelist(false);
        }
        taskportDao.save(taskport);
    }

    /**
     * 修改
     *
     * @param taskport
     */
    public void update(Taskport taskport) {
        taskportDao.save(taskport);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        taskportDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        taskportDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Taskport> createSpecification(Map searchMap) {

        return (Specification<Taskport>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 端口编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 资产ip编号
            if (searchMap.get("taskipid") != null && !"".equals(searchMap.get("taskipid"))) {
                predicateList.add(cb.like(root.get("taskipid").as(String.class), "%" + searchMap.get("taskipid") + "%"));
            }
            // 端口
            if (searchMap.get("port") != null && !"".equals(searchMap.get("port"))) {
                predicateList.add(cb.like(root.get("port").as(String.class), "%" + searchMap.get("port") + "%"));
            }
            // 端口协议
            if (searchMap.get("protocol") != null && !"".equals(searchMap.get("protocol"))) {
                predicateList.add(cb.like(root.get("protocol").as(String.class), "%" + searchMap.get("protocol") + "%"));
            }
            // 端口开放状态
            if (searchMap.get("state") != null && !"".equals(searchMap.get("state"))) {
                predicateList.add(cb.like(root.get("state").as(String.class), "%" + searchMap.get("state") + "%"));
            }
            // 端口服务
            if (searchMap.get("service") != null && !"".equals(searchMap.get("service"))) {
                predicateList.add(cb.like(root.get("service").as(String.class), "%" + searchMap.get("service") + "%"));
            }
            // 服务版本
            if (searchMap.get("version") != null && !"".equals(searchMap.get("version"))) {
                predicateList.add(cb.like(root.get("version").as(String.class), "%" + searchMap.get("version") + "%"));
            }
            //安全检测白名单
            if (searchMap.get("checkwhitelist") != null && !"".equals(searchMap.get("checkwhitelist"))) {
                predicateList.add(cb.equal(root.get("checkwhitelist").as(Boolean.class), searchMap.get("checkwhitelist")));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param portList
     */
    public void batchAdd(List<Taskport> portList) {
        taskportDao.saveAll(portList);
    }

    /**
     * 根据taskIpId查询实体
     *
     * @param taskIpId
     * @return
     */
    public List<Taskport> findByTaskipid(String taskIpId) {
        return taskportDao.findByTaskipid(taskIpId);
    }

    /**
     * 根据 taskipid,port查询端口
     *
     * @param taskipid
     * @param taskport
     * @return Taskport
     */
    public Taskport findByTaskipidAndPort(String taskipid, String taskport) {
        return taskportDao.findByTaskipidAndPort(taskipid, taskport);
    }

    /**
     * 根据 taskipid,port查询端口
     *
     * @param taskIpId
     * @param state
     * @return list
     */
    public List<Taskport> findByTaskipidAndState(String taskIpId, String state) {
        return taskportDao.findByTaskipidAndState(taskIpId, state);
    }

    /**
     * 查根据service和state查询端口
     *
     * @param taskipid
     * @param service
     * @param state
     * @return
     */

    public List<Taskport> findByTaskipidAndServiceAndState(String taskipid, String service, String state) {
        return taskportDao.findByTaskipidAndServiceAndState(taskipid, service, state);
    }

    /**
     * 查根据version和state查询端口
     *
     * @param taskipid
     * @param version
     * @param state
     * @return
     */

    public List<Taskport> findByTaskipidAndVersionAndState(String taskipid, String version, String state) {
        return taskportDao.findByTaskipidAndVersionAndState(taskipid, version, state);
    }

    /**
     * 根据taskipid批量删除
     *
     * @param taskipid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByTaskipid(String taskipid) {
        taskportDao.deleteAllByTaskipid(taskipid);
    }

}

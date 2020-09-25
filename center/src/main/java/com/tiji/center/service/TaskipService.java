package com.tiji.center.service;

import com.tiji.center.dao.TaskipDao;
import com.tiji.center.pojo.Taskip;
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
 * taskip服务层
 *
 * @author 贰拾壹
 */
@Service
public class TaskipService {

    @Autowired
    private TaskipDao taskipDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Taskip> findAll() {
        return taskipDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Taskip> findSearch(Map whereMap, int page, int size) {
        Specification<Taskip> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return taskipDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Taskip> findSearch(Map whereMap) {
        Specification<Taskip> specification = createSpecification(whereMap);
        return taskipDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Taskip findById(String id) {
        return taskipDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param taskip
     */
    public void add(Taskip taskip) {
        if (Objects.isNull(taskip.getId())) {
            taskip.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(taskip.getCheckwhitelist())) {
            taskip.setCheckwhitelist(false);
        }
        taskipDao.save(taskip);
    }

    /**
     * 修改
     *
     * @param taskip
     */
    public void update(Taskip taskip) {
        taskipDao.save(taskip);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        taskipDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        taskipDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Taskip> createSpecification(Map searchMap) {

        return (Specification<Taskip>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 资产ip编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 任务编号
            if (searchMap.get("taskid") != null && !"".equals(searchMap.get("taskid"))) {
                predicateList.add(cb.like(root.get("taskid").as(String.class), "%" + searchMap.get("taskid") + "%"));
            }
            // ip地址
            if (searchMap.get("ipaddressv4") != null && !"".equals(searchMap.get("ipaddressv4"))) {
                predicateList.add(cb.like(root.get("ipaddressv4").as(String.class), "%" + searchMap.get("ipaddressv4") + "%"));
            }
            // ipaddressv6
            if (searchMap.get("ipaddressv6") != null && !"".equals(searchMap.get("ipaddressv6"))) {
                predicateList.add(cb.like(root.get("ipaddressv6").as(String.class), "%" + searchMap.get("ipaddressv6") + "%"));
            }
            //安全检测白名单
            if (searchMap.get("checkwhitelist") != null && !"".equals(searchMap.get("checkwhitelist"))) {
                predicateList.add(cb.equal(root.get("checkwhitelist").as(Boolean.class), (searchMap.get("checkwhitelist"))));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param assetipList
     */
    public void batchAdd(List<Taskip> assetipList) {
        taskipDao.saveAll(assetipList);
    }


    /**
     * 根据taskID查询实体
     *
     * @param taskId
     * @return
     */
    public List<Taskip> findByTaskId(String taskId) {
        return taskipDao.findByTaskid(taskId);
    }

    /**
     * 根据taskid和ipaddressv4查询ip
     *
     * @return Taskip
     */
    public Taskip findByTaskidAndIpaddressv4(String taskid, String ipaddressv4) {
        return taskipDao.findByTaskidAndIpaddressv4(taskid, ipaddressv4);
    }

    /**
     * 根据taskid查询ip和端口
     *
     * @return List
     */
    public List<String> findTaskIpAndPort(String taskid) {
        return taskipDao.findTaskIpAndPort(taskid);
    }


    /**
     * 根据taskID查询不在check白名单的实体
     *
     * @param taskId
     * @return
     */
    public List<Taskip> findAllByTaskidAndCheckwhitelistIsFalse(String taskId) {
        return taskipDao.findAllByTaskidAndCheckwhitelistIsFalse(taskId);
    }

    /**
     * findAllByHttpLike，state="open"
     *
     * @param taskId
     * @return
     */
    public List<String> findAllByServiceLikeAndCheckwhitelistIsFalse(String taskId, String serviceLike) {
        return taskipDao.findAllByServiceLikeAndCheckwhitelistIsFalse(taskId, "%" + serviceLike + "%");
    }

    /**
     * 查询所有service为空的端口
     *
     * @param taskId
     * @return
     */
    public List<String> findByTaskidAndServiceIsNullAndCheckwhitelistIsFalse(String taskId) {
        return taskipDao.findByTaskidAndServiceIsNullAndCheckwhitelistIsFalse(taskId);
    }

    /**
     * 根据taskId查询所有不在check白名单的，按照searchKey like searchValue的ip和端口、service、version，state="open"
     *
     * @param taskId
     * @return
     */
    public List<String> findByTaskidAndServiceLikeAndCheckwhitelistIsFalse(String taskId, String serviceLike) {
        return taskipDao.findByTaskidAndServiceLikeAndCheckwhitelistIsFalse(taskId, serviceLike);
    }

    /**
     * 根据taskId查询所有不在check白名单的，按照searchKey like searchValue的ip和端口、service、version ，state="open"
     *
     * @param taskId
     * @return
     */
    public List<String> findByTaskidAndVersionLikeAndCheckwhitelistIsFalse(String taskId, String versionLike) {
        return taskipDao.findByTaskidAndVersionLikeAndCheckwhitelistIsFalse(taskId, versionLike);
    }


    /**
     * 查询未下线的ip所有漏洞
     *
     * @return
     */
    public List<List> findAllVulns() {
        return taskipDao.findAllVulns();
    }

    /**
     * 查询未下线的ip，不在白名单的资产
     *
     * @return
     */
    public List<List> findAllAssets() {
        return taskipDao.findAllAssets();
    }

    /**
     * 查询需要发邮件资产的数量
     *
     * @return
     */
    public long findAllAssetsCount() {
        return taskipDao.findAllAssetsCount();
    }

    /**
     * 分页查询未下线的ip，不在白名单的资产
     *
     * @return
     */
    public List<List<String>> findAllAssetsByPage(long offset, long rows) {
        return taskipDao.findAllAssetsByPage(offset, rows);
    }

    /**
     * 分页查询未下线的ip，不在白名单的资产
     *
     * @return
     */
    public List<List<String>> findAllAssetsByPageNew(long offset, long rows) {
        return taskipDao.findAllAssetsByPageNew(offset, rows);
    }


    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    public List<String> findByIds(String[] ids) {
        List<String> taskIpIdAndIpList = new ArrayList<>();
        for (String taskIpId : ids) {
            Taskip taskip = findById(taskIpId);
            if (Objects.isNull(taskip)) {
                taskIpIdAndIpList.add(taskIpId + "-" + null);
            } else {
                taskIpIdAndIpList.add(taskIpId + "-" + taskip.getIpaddressv4());
            }
        }
        return taskIpIdAndIpList.isEmpty() ? null : taskIpIdAndIpList;
    }

    /**
     * 查询所有没有端口的ip
     *
     * @return
     */
    public List<String> findAllAssetipNoPort() {
        return taskipDao.findAllAssetipNoPort();
    }


    /**
     * 查询未修复的检查结果数量
     *
     * @return
     */
    public long findAllVulnsCount() {
        return taskipDao.findAllVulnsCount();
    }

    /**
     * 分页查询未修复的检查结果
     *
     * @return
     */
    public List<List<String>> findAllVulnsByPage(long offset, long rows) {
        return taskipDao.findAllVulnsByPage(offset, rows);
    }


}

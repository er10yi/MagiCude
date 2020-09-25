package com.tiji.center.service;

import com.tiji.center.dao.PluginassetserviceDao;
import com.tiji.center.pojo.Pluginassetservice;
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
 * assetservice服务层
 *
 * @author 贰拾壹
 */
@Service
public class PluginassetserviceService {

    @Autowired
    private PluginassetserviceDao pluginassetserviceDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Pluginassetservice> findAll() {
        return pluginassetserviceDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Pluginassetservice> findSearch(Map whereMap, int page, int size) {
        Specification<Pluginassetservice> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return pluginassetserviceDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Pluginassetservice> findSearch(Map whereMap) {
        Specification<Pluginassetservice> specification = createSpecification(whereMap);
        return pluginassetserviceDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Pluginassetservice findById(String id) {
        return pluginassetserviceDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param assetservice
     */
    public void add(Pluginassetservice assetservice) {
        if (Objects.isNull(assetservice.getId())) {
            assetservice.setId(idWorker.nextId() + "");
        }
        pluginassetserviceDao.save(assetservice);
    }

    /**
     * 修改
     *
     * @param assetservice
     */
    public void update(Pluginassetservice assetservice) {
        pluginassetserviceDao.save(assetservice);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        pluginassetserviceDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        pluginassetserviceDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Pluginassetservice> createSpecification(Map searchMap) {

        return (Specification<Pluginassetservice>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 资产服务编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.equal(root.get("id").as(String.class), searchMap.get("id")));
            }
            // 插件配置编号
            if (searchMap.get("pluginconfigid") != null && !"".equals(searchMap.get("pluginconfigid"))) {
                predicateList.add(cb.equal(root.get("pluginconfigid").as(String.class), searchMap.get("pluginconfigid")));
            }
            // 资产服务
            if (searchMap.get("assetservice") != null && !"".equals(searchMap.get("assetservice"))) {
                predicateList.add(cb.like(root.get("assetservice").as(String.class), "%" + searchMap.get("assetservice") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param assetServiceList
     */
    public void batchAdd(List<Pluginassetservice> assetServiceList) {
        pluginassetserviceDao.saveAll(assetServiceList);
    }

    /**
     * 根据pluginConfigI和serviceName查询实体
     *
     * @param pluginConfigId
     * @param serviceName
     * @return
     */
    public Pluginassetservice findByPluginconfigidAndService(String pluginConfigId, String serviceName) {
        return pluginassetserviceDao.findByPluginconfigidAndAssetservice(pluginConfigId, serviceName);
    }

    /**
     * 根据pluginconfigId批量删除
     *
     * @param pluginconfigId
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByPluginconfigid(String pluginconfigId) {
        pluginassetserviceDao.deleteAllByPluginconfigid(pluginconfigId);
    }

    /**
     * 根据pluginId查询
     *
     * @param pluginconfigid pluginId
     * @return
     */
    public List<Pluginassetservice> findAllByPluginId(String pluginconfigid) {
        return pluginassetserviceDao.findAllByPluginconfigid(pluginconfigid);
    }


    /**
     * 根据taskId查询任务开启的插件
     *
     * @return
     */
    public List<Pluginassetservice> findPluginassetserviceByTaskid(String taskId) {
        return pluginassetserviceDao.findPluginassetserviceByTaskid(taskId);
    }
}

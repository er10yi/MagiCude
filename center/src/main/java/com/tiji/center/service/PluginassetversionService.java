package com.tiji.center.service;

import com.tiji.center.dao.PluginassetversionDao;
import com.tiji.center.pojo.Pluginassetversion;
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
 * assetversion服务层
 *
 * @author 贰拾壹
 */
@Service
public class PluginassetversionService {

    @Autowired
    private PluginassetversionDao pluginassetversionDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Pluginassetversion> findAll() {
        return pluginassetversionDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Pluginassetversion> findSearch(Map whereMap, int page, int size) {
        Specification<Pluginassetversion> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return pluginassetversionDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Pluginassetversion> findSearch(Map whereMap) {
        Specification<Pluginassetversion> specification = createSpecification(whereMap);
        return pluginassetversionDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Pluginassetversion findById(String id) {
        return pluginassetversionDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param assetversion
     */
    public void add(Pluginassetversion assetversion) {
        if (Objects.isNull(assetversion.getId())) {
            assetversion.setId(idWorker.nextId() + "");
        }
        pluginassetversionDao.save(assetversion);
    }

    /**
     * 修改
     *
     * @param assetversion
     */
    public void update(Pluginassetversion assetversion) {
        pluginassetversionDao.save(assetversion);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        pluginassetversionDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        pluginassetversionDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Pluginassetversion> createSpecification(Map searchMap) {

        return (Specification<Pluginassetversion>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 资产版本编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 插件配置编号
            if (searchMap.get("pluginconfigid") != null && !"".equals(searchMap.get("pluginconfigid"))) {
                predicateList.add(cb.like(root.get("pluginconfigid").as(String.class), "%" + (String) searchMap.get("pluginconfigid") + "%"));
            }
            // 资产版本
            if (searchMap.get("assetversion") != null && !"".equals(searchMap.get("assetversion"))) {
                predicateList.add(cb.like(root.get("assetversion").as(String.class), "%" + (String) searchMap.get("assetversion") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param assetVersionList
     */
    public void batchAdd(List<Pluginassetversion> assetVersionList) {
        pluginassetversionDao.saveAll(assetVersionList);
    }


    /**
     * 根据pluginConfigI和versionName查询实体
     *
     * @param pluginConfigId
     * @param versionName
     * @return
     */
    public Pluginassetversion findByPluginconfigidAndService(String pluginConfigId, String versionName) {
        return pluginassetversionDao.findByPluginconfigidAndAssetversion(pluginConfigId, versionName);
    }

    /**
     * 根据pluginconfigId批量删除
     *
     * @param pluginconfigId
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByPluginconfigid(String pluginconfigId) {
        pluginassetversionDao.deleteAllByPluginconfigid(pluginconfigId);
    }

    /**
     * 根据pluginId查询
     *
     * @param pluginconfigid pluginconfigid
     * @return
     */
    public List<Pluginassetversion> findAllByPluginId(String pluginconfigid) {
        return pluginassetversionDao.findAllByPluginconfigid(pluginconfigid);
    }


    /**
     * 根据taskId查询任务开启的插件
     *
     * @return
     */
    public List<Pluginassetversion> findPluginassetversionByTaskid(String taskId) {
        return pluginassetversionDao.findPluginassetversionByTaskid(taskId);
    }
}

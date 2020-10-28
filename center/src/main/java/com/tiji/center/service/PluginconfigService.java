package com.tiji.center.service;

import com.tiji.center.dao.PluginconfigDao;
import com.tiji.center.pojo.Pluginconfig;
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
 * pluginconfig服务层
 *
 * @author 贰拾壹
 */
@Service
public class PluginconfigService {

    @Autowired
    private PluginconfigDao pluginconfigDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Pluginconfig> findAll() {
        return pluginconfigDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Pluginconfig> findSearch(Map whereMap, int page, int size) {
        Specification<Pluginconfig> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return pluginconfigDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Pluginconfig> findSearch(Map whereMap) {
        Specification<Pluginconfig> specification = createSpecification(whereMap);
        return pluginconfigDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Pluginconfig findById(String id) {
        return pluginconfigDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param pluginconfig
     */
    public void add(Pluginconfig pluginconfig) {
        if (Objects.isNull(pluginconfig.getId())) {
            pluginconfig.setId(idWorker.nextId() + "");
        }
        pluginconfigDao.save(pluginconfig);
    }

    /**
     * 修改
     *
     * @param pluginconfig
     */
    public void update(Pluginconfig pluginconfig) {
        pluginconfigDao.save(pluginconfig);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        pluginconfigDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        pluginconfigDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Pluginconfig> createSpecification(Map searchMap) {

        return (Specification<Pluginconfig>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 插件配置编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 插件名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
            }
            // 插件参数
            if (searchMap.get("args") != null && !"".equals(searchMap.get("args"))) {
                predicateList.add(cb.like(root.get("args").as(String.class), "%" + searchMap.get("args") + "%"));
            }
            // 插件风险级别
            if (searchMap.get("risk") != null && !"".equals(searchMap.get("risk"))) {
                predicateList.add(cb.like(root.get("risk").as(String.class), "%" + searchMap.get("risk") + "%"));
            }
            // 插件类型：nse或者自定义
            if (searchMap.get("type") != null && !"".equals(searchMap.get("type"))) {
                predicateList.add(cb.like(root.get("type").as(String.class), "%" + searchMap.get("type") + "%"));
            }
            // 插件超时
            if (searchMap.get("timeout") != null && !"".equals(searchMap.get("timeout"))) {
                predicateList.add(cb.like(root.get("timeout").as(String.class), "%" + searchMap.get("timeout") + "%"));
            }
            // 插件代码
            if (searchMap.get("plugincode") != null && !"".equals(searchMap.get("plugincode"))) {
                predicateList.add(cb.like(root.get("plugincode").as(String.class), "%" + searchMap.get("plugincode") + "%"));
            }
            // 辅助验证类型
            if (searchMap.get("validatetype") != null && !"".equals(searchMap.get("validatetype"))) {
                predicateList.add(cb.like(root.get("validatetype").as(String.class), "%" + searchMap.get("plugincode") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param pluginConfigList
     */
    public void batchAdd(List<Pluginconfig> pluginConfigList) {
        pluginconfigDao.saveAll(pluginConfigList);
    }


    /**
     * 根据pluginConfigName和pluginConfigType查询Pluginconfig
     *
     * @param pluginConfigName
     * @param pluginConfigType
     */
    public Pluginconfig findByNameAndType(String pluginConfigName, String pluginConfigType) {
        return pluginconfigDao.findByNameAndType(pluginConfigName, pluginConfigType);
    }


    /**
     * 根据pluginConfigName和pluginConfigType查询Pluginconfig
     *
     * @param pluginConfigName
     */
    public Pluginconfig findByName(String pluginConfigName) {
        return pluginconfigDao.findByName(pluginConfigName);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @param type
     * @return
     */
    public Pluginconfig findByIdAndType(String id, String type) {
        return pluginconfigDao.findByIdAndType(id, type);
    }

}

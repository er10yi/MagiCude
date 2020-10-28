package com.tiji.center.service;

import com.tiji.center.dao.VulnpluginconfigDao;
import com.tiji.center.pojo.Vuln;
import com.tiji.center.pojo.Vulnpluginconfig;
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
 * vulnpluginconfig服务层
 *
 * @author 贰拾壹
 */
@Service
public class VulnpluginconfigService {

    @Autowired
    private VulnpluginconfigDao vulnpluginconfigDao;
    @Autowired
    private VulnService vulnService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Vulnpluginconfig> findAll() {
        return vulnpluginconfigDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Vulnpluginconfig> findSearch(Map whereMap, int page, int size) {
        Specification<Vulnpluginconfig> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return vulnpluginconfigDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Vulnpluginconfig> findSearch(Map whereMap) {
        Specification<Vulnpluginconfig> specification = createSpecification(whereMap);
        return vulnpluginconfigDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Vulnpluginconfig findById(String id) {
        return vulnpluginconfigDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param vulnpluginconfig
     */
    public void add(Vulnpluginconfig vulnpluginconfig) {
        if (Objects.isNull(vulnpluginconfig.getId())) {
            vulnpluginconfig.setId(idWorker.nextId() + "");
        }
        vulnpluginconfigDao.save(vulnpluginconfig);
    }

    /**
     * 修改
     *
     * @param vulnpluginconfig
     */
    public void update(Vulnpluginconfig vulnpluginconfig) {
        vulnpluginconfigDao.save(vulnpluginconfig);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        vulnpluginconfigDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        vulnpluginconfigDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Vulnpluginconfig> createSpecification(Map searchMap) {

        return (Specification<Vulnpluginconfig>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 漏洞编号
            if (searchMap.get("vulnid") != null && !"".equals(searchMap.get("vulnid"))) {
                predicateList.add(cb.like(root.get("vulnid").as(String.class), "%" + (String) searchMap.get("vulnid") + "%"));
            }
            // 插件配置编号
            if (searchMap.get("pluginconfigid") != null && !"".equals(searchMap.get("pluginconfigid"))) {
                predicateList.add(cb.like(root.get("pluginconfigid").as(String.class), "%" + (String) searchMap.get("pluginconfigid") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据pluginConfigId查询所有的
     *
     * @param pluginConfigId
     * @return
     */
    public List<Vulnpluginconfig> findAllByPluginConfigId(String pluginConfigId) {
        return vulnpluginconfigDao.findAllByPluginconfigid(pluginConfigId);
    }

    /**
     * 根据pluginconfigId批量删除
     *
     * @param pluginconfigId
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByPluginconfigid(String pluginconfigId) {
        vulnpluginconfigDao.deleteAllByPluginconfigid(pluginconfigId);
    }

    /**
     * 根据pluginId查询
     *
     * @param pluginId pluginId
     * @return
     */
    public List<Vuln> findAllByPluginconfigid(String pluginId) {
        List<String> vulnidList = new ArrayList<>();
        List<Vulnpluginconfig> vulnpluginconfigList = vulnpluginconfigDao.findAllByPluginconfigid(pluginId);
        vulnpluginconfigList.forEach(vulnpluginconfig -> {
            String vulnid = vulnpluginconfig.getVulnid();
            vulnidList.add(vulnid);
        });
        return  vulnService.findByVulnIds(vulnidList);
    }

    /**
     * 根据vulnid、pluginId查询
     *
     * @param vulnid         vulnid
     * @param pluginconfigid pluginconfigid
     * @return
     */
    public Vulnpluginconfig findByVulnidAndPluginconfigid(String vulnid, String pluginconfigid) {
        return vulnpluginconfigDao.findByVulnidAndPluginconfigid(vulnid, pluginconfigid);
    }
}

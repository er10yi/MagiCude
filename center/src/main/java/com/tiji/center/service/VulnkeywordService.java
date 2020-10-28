package com.tiji.center.service;

import com.tiji.center.dao.VulnkeywordDao;
import com.tiji.center.pojo.Vulnkeyword;
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
 * vulnkeyword服务层
 *
 * @author 贰拾壹
 */
@Service
public class VulnkeywordService {

    @Autowired
    private VulnkeywordDao vulnkeywordDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Vulnkeyword> findAll() {
        return vulnkeywordDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Vulnkeyword> findSearch(Map whereMap, int page, int size) {
        Specification<Vulnkeyword> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return vulnkeywordDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Vulnkeyword> findSearch(Map whereMap) {
        Specification<Vulnkeyword> specification = createSpecification(whereMap);
        return vulnkeywordDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Vulnkeyword findById(String id) {
        return vulnkeywordDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param vulnkeyword
     */
    public void add(Vulnkeyword vulnkeyword) {
        if (Objects.isNull(vulnkeyword.getId())) {
            vulnkeyword.setId(idWorker.nextId() + "");
        }
        vulnkeywordDao.save(vulnkeyword);
    }

    /**
     * 修改
     *
     * @param vulnkeyword
     */
    public void update(Vulnkeyword vulnkeyword) {
        vulnkeywordDao.save(vulnkeyword);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        vulnkeywordDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        vulnkeywordDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Vulnkeyword> createSpecification(Map searchMap) {

        return (Specification<Vulnkeyword>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 漏洞关键字编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 插件配置编号
            if (searchMap.get("pluginconfigid") != null && !"".equals(searchMap.get("pluginconfigid"))) {
                predicateList.add(cb.like(root.get("pluginconfigid").as(String.class), "%" + searchMap.get("pluginconfigid") + "%"));
            }
            // 漏洞关键字
            if (searchMap.get("keyword") != null && !"".equals(searchMap.get("keyword"))) {
                predicateList.add(cb.like(root.get("keyword").as(String.class), "%" + searchMap.get("keyword") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param vulnKeywordList
     */
    public void batchAdd(List<Vulnkeyword> vulnKeywordList) {
        vulnkeywordDao.saveAll(vulnKeywordList);
    }

    /**
     * 根据pluginConfigI和vulnKeywordName查询实体
     *
     * @param pluginConfigId
     * @param vulnKeywordName
     * @return Vulnkeyword
     */
    public Vulnkeyword findByPluginconfigidAndService(String pluginConfigId, String vulnKeywordName) {
        return vulnkeywordDao.findByPluginconfigidAndKeyword(pluginConfigId, vulnKeywordName);
    }

    /**
     * 根据pluginconfigId批量删除
     *
     * @param pluginconfigId
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByPluginconfigid(String pluginconfigId) {
        vulnkeywordDao.deleteAllByPluginconfigid(pluginconfigId);
    }

    /**
     * 根据pluginId查询
     *
     * @param pluginconfigid pluginconfigid
     * @return
     */
    public List<Vulnkeyword> findAllByPluginId(String pluginconfigid) {
        return vulnkeywordDao.findAllByPluginconfigid(pluginconfigid);
    }

    /**
     * 查询所有并去重
     *
     * @return List
     */
    public List<String> findAllDistinctVulnKeyword() {
        return vulnkeywordDao.findAllDistinctVulnKeyword();
    }
}

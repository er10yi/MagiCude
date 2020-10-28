package com.tiji.center.service;

import com.tiji.center.dao.VulnDao;
import com.tiji.center.pojo.Vuln;
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
 * vuln服务层
 *
 * @author 贰拾壹
 */
@Service
public class VulnService {

    @Autowired
    private VulnDao vulnDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Vuln> findAll() {
        return vulnDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Vuln> findSearch(Map whereMap, int page, int size) {
        Specification<Vuln> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return vulnDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Vuln> findSearch(Map whereMap) {
        Specification<Vuln> specification = createSpecification(whereMap);
        return vulnDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Vuln findById(String id) {
        return vulnDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param vuln
     */
    public void add(Vuln vuln) {
        if (Objects.isNull(vuln.getId())) {
            vuln.setId(idWorker.nextId() + "");
        }
        vulnDao.save(vuln);
    }

    /**
     * 修改
     *
     * @param vuln
     */
    public void update(Vuln vuln) {
        vulnDao.save(vuln);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        vulnDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        vulnDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Vuln> createSpecification(Map searchMap) {

        return (Specification<Vuln>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 漏洞编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }

            // 漏洞二级分类编号
            if (searchMap.get("categorysecondid") != null && !"".equals(searchMap.get("categorysecondid"))) {
                predicateList.add(cb.equal(root.get("categorysecondid").as(String.class), searchMap.get("categorysecondid")));
            }

            // 漏洞名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + (String) searchMap.get("name") + "%"));
            }
            // 漏洞描述
            if (searchMap.get("description") != null && !"".equals(searchMap.get("description"))) {
                predicateList.add(cb.like(root.get("description").as(String.class), "%" + (String) searchMap.get("description") + "%"));
            }
            // 漏洞风险级别
            if (searchMap.get("risk") != null && !"".equals(searchMap.get("risk"))) {
                predicateList.add(cb.like(root.get("risk").as(String.class), "%" + (String) searchMap.get("risk") + "%"));
            }
            // 参考
            if (searchMap.get("refer") != null && !"".equals(searchMap.get("refer"))) {
                predicateList.add(cb.like(root.get("refer").as(String.class), "%" + (String) searchMap.get("refer") + "%"));
            }
            // impactscope
            if (searchMap.get("impactscope") != null && !"".equals(searchMap.get("impactscope"))) {
                predicateList.add(cb.like(root.get("impactscope").as(String.class), "%" + (String) searchMap.get("impactscope") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据name查询实体
     *
     * @param name
     * @return
     */
    public Vuln findByName(String name) {
        return vulnDao.findByName(name);
    }


    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    public List<String> findByIds(String[] ids) {
        List<String> vulnIdAndVulnNameList = new ArrayList<>();
        for (String id : ids) {
            Vuln vuln = findById(id);
            if (Objects.isNull(vuln)) {
                vulnIdAndVulnNameList.add(id + "-" + null);
            } else {
                vulnIdAndVulnNameList.add(id + "-" + vuln.getName());
            }
        }
        return vulnIdAndVulnNameList.isEmpty() ? null : vulnIdAndVulnNameList;
    }

    /**
     * 根据id数组查询漏洞
     *
     * @param vulnids
     * @return
     */
    public List<Vuln> findByVulnIds(List<String> vulnids) {
        return vulnDao.findAllById(vulnids);
    }
}

package com.tiji.center.service;

import com.tiji.center.dao.SolutionDao;
import com.tiji.center.pojo.Solution;
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
 * solution服务层
 *
 * @author 贰拾壹
 */
@Service
public class SolutionService {

    @Autowired
    private SolutionDao solutionDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Solution> findAll() {
        return solutionDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Solution> findSearch(Map whereMap, int page, int size) {
        Specification<Solution> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return solutionDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Solution> findSearch(Map whereMap) {
        Specification<Solution> specification = createSpecification(whereMap);
        return solutionDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Solution findById(String id) {
        return solutionDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param solution
     */
    public void add(Solution solution) {
        if (Objects.isNull(solution.getId())) {
            solution.setId(idWorker.nextId() + "");
        }
        solutionDao.save(solution);
    }

    /**
     * 修改
     *
     * @param solution
     */
    public void update(Solution solution) {
        solutionDao.save(solution);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        solutionDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        solutionDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Solution> createSpecification(Map searchMap) {

        return (Specification<Solution>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 修复方案编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 漏洞编号
            if (searchMap.get("vulnid") != null && !"".equals(searchMap.get("vulnid"))) {
                predicateList.add(cb.like(root.get("vulnid").as(String.class), "%" + (String) searchMap.get("vulnid") + "%"));
            }
            // 修复方案
            if (searchMap.get("solution") != null && !"".equals(searchMap.get("solution"))) {
                predicateList.add(cb.like(root.get("solution").as(String.class), "%" + (String) searchMap.get("solution") + "%"));
            }
            // 修复代码示例
            if (searchMap.get("codedemo") != null && !"".equals(searchMap.get("codedemo"))) {
                predicateList.add(cb.like(root.get("codedemo").as(String.class), "%" + (String) searchMap.get("codedemo") + "%"));
            }
            // 修复配置示例
            if (searchMap.get("configdemo") != null && !"".equals(searchMap.get("configdemo"))) {
                predicateList.add(cb.like(root.get("configdemo").as(String.class), "%" + (String) searchMap.get("configdemo") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据vulnId查询
     *
     * @param vulnId
     * @return
     */
    public List<Solution> findAllByVulnId(String vulnId) {
        return solutionDao.findAllByVulnid(vulnId);
    }

    /**
     * 根据vulnId批量删除
     *
     * @param vulnId
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByVulnId(String vulnId) {
        solutionDao.deleteAllByVulnid(vulnId);
    }
}

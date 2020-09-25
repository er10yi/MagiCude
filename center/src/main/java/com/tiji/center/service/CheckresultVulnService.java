package com.tiji.center.service;

import com.tiji.center.dao.CheckresultVulnDao;
import com.tiji.center.pojo.CheckresultVuln;
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
 * checkresultVuln服务层
 *
 * @author 贰拾壹
 */
@Service
public class CheckresultVulnService {

    @Autowired
    private CheckresultVulnDao checkresultVulnDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<CheckresultVuln> findAll() {
        return checkresultVulnDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<CheckresultVuln> findSearch(Map whereMap, int page, int size) {
        Specification<CheckresultVuln> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return checkresultVulnDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<CheckresultVuln> findSearch(Map whereMap) {
        Specification<CheckresultVuln> specification = createSpecification(whereMap);
        return checkresultVulnDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public CheckresultVuln findById(String id) {
        return checkresultVulnDao.findById(id).isPresent() ? checkresultVulnDao.findById(id).get() : null;
    }

    /**
     * 增加
     *
     * @param checkresultVuln
     */
    public void add(CheckresultVuln checkresultVuln) {
        if (Objects.isNull(checkresultVuln.getId())) {
            checkresultVuln.setId(idWorker.nextId() + "");
        }
        checkresultVulnDao.save(checkresultVuln);
    }

    /**
     * 修改
     *
     * @param checkresultVuln
     */
    public void update(CheckresultVuln checkresultVuln) {
        checkresultVulnDao.save(checkresultVuln);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        checkresultVulnDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<CheckresultVuln> createSpecification(Map searchMap) {

        return (Specification<CheckresultVuln>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.equal(root.get("id").as(String.class), searchMap.get("id")));
            }
            // 检测结果编号
            if (searchMap.get("checkresultid") != null && !"".equals(searchMap.get("checkresultid"))) {
                predicateList.add(cb.equal(root.get("checkresultid").as(String.class), searchMap.get("checkresultid")));
            }
            // 漏洞编号
            if (searchMap.get("vulnid") != null && !"".equals(searchMap.get("vulnid"))) {
                predicateList.add(cb.equal(root.get("vulnid").as(String.class), searchMap.get("vulnid")));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    public List<String> findByIds(String[] ids) {
        List<String> checkresultVulnIdList = new ArrayList<>();
        for (String checkresultid : ids) {
            List<CheckresultVuln> checkresultVulnList = checkresultVulnDao.findAllByCheckresultid(checkresultid);
            if (Objects.isNull(checkresultVulnList)) {
                checkresultVulnIdList.add(checkresultid + "-" + null);
            } else {
                for (CheckresultVuln checkresultVuln : checkresultVulnList) {
                    checkresultVulnIdList.add(checkresultid + "-" + checkresultVuln.getVulnid());
                }
            }
        }
        return checkresultVulnIdList.isEmpty() ? null : checkresultVulnIdList;
    }

    /**
     * 删除
     *
     * @param checkresultid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByCheckresultid(String checkresultid) {
        checkresultVulnDao.deleteAllByCheckresultid(checkresultid);
    }

    public List<CheckresultVuln> findAllByCheckresultid(String id) {
        return checkresultVulnDao.findAllByCheckresultid(id);
    }
}

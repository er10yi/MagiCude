package com.tiji.center.service;

import com.tiji.center.dao.DemocodeDao;
import com.tiji.center.pojo.Democode;
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
 * democode服务层
 *
 * @author 贰拾壹
 */
@Service
public class DemocodeService {

    @Autowired
    private DemocodeDao democodeDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Democode> findAll() {
        return democodeDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Democode> findSearch(Map whereMap, int page, int size) {
        Specification<Democode> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return democodeDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Democode> findSearch(Map whereMap) {
        Specification<Democode> specification = createSpecification(whereMap);
        return democodeDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Democode findById(String id) {
        return democodeDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param democode
     */
    public void add(Democode democode) {
        if (Objects.isNull(democode.getId())) {
            democode.setId(idWorker.nextId() + "");
        }
        democodeDao.save(democode);
    }

    /**
     * 修改
     *
     * @param democode
     */
    public void update(Democode democode) {
        democodeDao.save(democode);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        democodeDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        democodeDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Democode> createSpecification(Map searchMap) {

        return (Specification<Democode>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 漏洞示例代码编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 漏洞编号
            if (searchMap.get("vulnid") != null && !"".equals(searchMap.get("vulnid"))) {
                predicateList.add(cb.equal(root.get("vulnid").as(String.class), searchMap.get("vulnid")));
            }
            // 漏洞示例代码
            if (searchMap.get("democode") != null && !"".equals(searchMap.get("democode"))) {
                predicateList.add(cb.like(root.get("democode").as(String.class), "%" + (String) searchMap.get("democode") + "%"));
            }
            // 漏洞poc
            if (searchMap.get("poc") != null && !"".equals(searchMap.get("poc"))) {
                predicateList.add(cb.like(root.get("poc").as(String.class), "%" + (String) searchMap.get("poc") + "%"));
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
    public List<Democode> findAllByVulnId(String vulnId) {
        return democodeDao.findAllByVulnid(vulnId);
    }

    /**
     * 根据vulnId批量删除
     *
     * @param vulnId
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByVulnId(String vulnId) {
        democodeDao.deleteAllByVulnid(vulnId);
    }

}

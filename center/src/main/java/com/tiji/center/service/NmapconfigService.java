package com.tiji.center.service;

import com.tiji.center.dao.NmapconfigDao;
import com.tiji.center.pojo.Nmapconfig;
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
 * nmapconfig服务层
 *
 * @author 贰拾壹
 */
@Service
public class NmapconfigService {

    @Autowired
    private NmapconfigDao nmapconfigDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Nmapconfig> findAll() {
        return nmapconfigDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Nmapconfig> findSearch(Map whereMap, int page, int size) {
        Specification<Nmapconfig> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return nmapconfigDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Nmapconfig> findSearch(Map whereMap) {
        Specification<Nmapconfig> specification = createSpecification(whereMap);
        return nmapconfigDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Nmapconfig findById(String id) {
        return nmapconfigDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param nmapconfig
     */
    public void add(Nmapconfig nmapconfig) {
        if (Objects.isNull(nmapconfig.getId())) {
            nmapconfig.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(nmapconfig.getThreadnumber()) || nmapconfig.getThreadnumber().isEmpty()) {
            nmapconfig.setThreadnumber("10");
        }
        if (Objects.isNull(nmapconfig.getSingleipscantime()) || nmapconfig.getSingleipscantime().isEmpty()) {
            nmapconfig.setSingleipscantime("1");
        }
        if (Objects.isNull(nmapconfig.getAdditionoption())) {
            nmapconfig.setAdditionoption("-Pn -n -sV --max-retries=1");
        }
        nmapconfigDao.save(nmapconfig);
    }

    /**
     * 修改
     *
     * @param nmapconfig
     */
    public void update(Nmapconfig nmapconfig) {
        nmapconfigDao.save(nmapconfig);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteById(String id) {
        nmapconfigDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        nmapconfigDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Nmapconfig> createSpecification(Map searchMap) {

        return (Specification<Nmapconfig>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // nmap配置编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 任务编号
            if (searchMap.get("taskid") != null && !"".equals(searchMap.get("taskid"))) {
                predicateList.add(cb.like(root.get("taskid").as(String.class), "%" + (String) searchMap.get("taskid") + "%"));
            }
            // 线程数量，在mass2Nmap模式下使用
            if (searchMap.get("threadnumber") != null && !"".equals(searchMap.get("threadnumber"))) {
                predicateList.add(cb.like(root.get("threadnumber").as(String.class), "%" + (String) searchMap.get("threadnumber") + "%"));
            }
            // 单个ip扫描次数，在mass2Nmap模式下使用
            if (searchMap.get("singleipscantime") != null && !"".equals(searchMap.get("singleipscantime"))) {
                predicateList.add(cb.like(root.get("singleipscantime").as(String.class), "%" + (String) searchMap.get("singleipscantime") + "%"));
            }
            // 附加选项，在mass2Nmap模式下使用
            if (searchMap.get("additionoption") != null && !"".equals(searchMap.get("additionoption"))) {
                predicateList.add(cb.like(root.get("additionoption").as(String.class), "%" + (String) searchMap.get("additionoption") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据taskid查询实体
     *
     * @param taskid
     * @return
     */
    public Nmapconfig findByTaskid(String taskid) {
        return nmapconfigDao.findByTaskid(taskid);
    }

    /**
     * 根据taskid删除实体
     *
     * @param Taskid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByTaskid(String Taskid) {
        nmapconfigDao.deleteAllByTaskid(Taskid);
    }

}

package com.tiji.center.service;

import com.tiji.center.dao.WebrawdataDao;
import com.tiji.center.pojo.Webrawdata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * webrawdata 服务层
 *
 * @author 贰拾壹
 */
@Service
public class WebrawdataService {

    private final WebrawdataDao webrawdataDao;
    private final IdWorker idWorker;

    @Autowired
    public WebrawdataService(WebrawdataDao webrawdataDao, IdWorker idWorker) {
        this.webrawdataDao = webrawdataDao;
        this.idWorker = idWorker;
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Webrawdata> findAll() {
        return webrawdataDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Webrawdata> findSearch(Map whereMap, int page, int size) {
        Specification<Webrawdata> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return webrawdataDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Webrawdata> findSearch(Map whereMap) {
        Specification<Webrawdata> specification = createSpecification(whereMap);
        return webrawdataDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Webrawdata findById(String id) {
        return webrawdataDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param webrawdata
     */
    public void add(Webrawdata webrawdata) {
        if (Objects.isNull(webrawdata.getId())) {
            webrawdata.setId(idWorker.nextId() + "");
        }
        webrawdataDao.save(webrawdata);
    }

    /**
     * 修改
     *
     * @param webrawdata
     */
    public void update(Webrawdata webrawdata) {
        webrawdataDao.save(webrawdata);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        webrawdataDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        webrawdataDao.deleteAllByIds(ids);
    }


    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Webrawdata> createSpecification(Map searchMap) {

        return (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 编号
            if (!StringUtils.isEmpty(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // webinfo编号
            if (!StringUtils.isEmpty(searchMap.get("webinfoid"))) {
                predicateList.add(cb.like(root.get("webinfoid").as(String.class), "%" + searchMap.get("webinfoid") + "%"));
            }
            // 响应头
            if (!StringUtils.isEmpty(searchMap.get("header"))) {
                predicateList.add(cb.like(root.get("header").as(String.class), "%" + searchMap.get("header") + "%"));
            }
            // 响应
            if (!StringUtils.isEmpty(searchMap.get("response"))) {
                predicateList.add(cb.like(root.get("response").as(String.class), "%" + searchMap.get("response") + "%"));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };

    }

    /**
     * 根据webinfoid批量删除
     *
     * @param webinfoid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByWebinfoid(String webinfoid) {
        webrawdataDao.deleteAllByWebinfoid(webinfoid);
    }
    /**
     * 根据webinfoid查询
     *
     * @param webinfoid webinfoid
     * @return
     */
    public List<Webrawdata> findAllByWebinfoid(String webinfoid) {
        return webrawdataDao.findAllByWebinfoid(webinfoid);
    }
}

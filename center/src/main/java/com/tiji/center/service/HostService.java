package com.tiji.center.service;

import com.tiji.center.dao.HostDao;
import com.tiji.center.pojo.Host;
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
 * host服务层
 *
 * @author 贰拾壹
 */
@Service
public class HostService {

    @Autowired
    private HostDao hostDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Host> findAll() {
        return hostDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Host> findSearch(Map whereMap, int page, int size) {
        Specification<Host> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return hostDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Host> findSearch(Map whereMap) {
        Specification<Host> specification = createSpecification(whereMap);
        return hostDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Host findById(String id) {
        return hostDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param host
     */
    public void add(Host host) {
        if (Objects.isNull(host.getId())) {
            host.setId(idWorker.nextId() + "");
        }
        hostDao.save(host);
    }

    /**
     * 修改
     *
     * @param host
     */
    public void update(Host host) {
        hostDao.save(host);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        hostDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        hostDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Host> createSpecification(Map searchMap) {

        return (Specification<Host>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 主机编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 资产ip编号
            if (searchMap.get("assetipid") != null && !"".equals(searchMap.get("assetipid"))) {
                predicateList.add(cb.like(root.get("assetipid").as(String.class), "%" + searchMap.get("assetipid") + "%"));
            }
            // mac地址
            if (searchMap.get("macaddress") != null && !"".equals(searchMap.get("macaddress"))) {
                predicateList.add(cb.like(root.get("macaddress").as(String.class), "%" + searchMap.get("macaddress") + "%"));
            }
            // 主机名
            if (searchMap.get("hostname") != null && !"".equals(searchMap.get("hostname"))) {
                predicateList.add(cb.like(root.get("hostname").as(String.class), "%" + searchMap.get("hostname") + "%"));
            }
            // 操作系统类型
            if (searchMap.get("ostype") != null && !"".equals(searchMap.get("ostype"))) {
                predicateList.add(cb.like(root.get("ostype").as(String.class), "%" + searchMap.get("ostype") + "%"));
            }
            // 操作系统版本
            if (searchMap.get("osversion") != null && !"".equals(searchMap.get("osversion"))) {
                predicateList.add(cb.like(root.get("osversion").as(String.class), "%" + searchMap.get("osversion") + "%"));
            }
            // 主机类型
            if (searchMap.get("type") != null && !"".equals(searchMap.get("type"))) {
                predicateList.add(cb.like(root.get("type").as(String.class), "%" + searchMap.get("type") + "%"));
            }
            // 主机所有者
            if (searchMap.get("owner") != null && !"".equals(searchMap.get("owner"))) {
                predicateList.add(cb.like(root.get("owner").as(String.class), "%" + searchMap.get("owner") + "%"));
            }
            //端口发现时间
            if (searchMap.get("activetime") != null && !"".equals(searchMap.get("activetime"))) {
                List<String> activetimeList = (List<String>) searchMap.get("activetime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("activetime").as(String.class), activetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("activetime").as(String.class), activetimeList.get(1)));
                //predicateList.add(cb.like(root.get("activetime").as(String.class), "%" + searchMap.get("activetime") + "%"));
            }
            // 备注，标记非dns反向解析
            if (searchMap.get("remark") != null && !"".equals(searchMap.get("remark"))) {
                predicateList.add(cb.like(root.get("remark").as(String.class), "%" + searchMap.get("remark") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param hostList
     */
    public void batchAdd(List<Host> hostList) {
        hostDao.saveAll(hostList);
    }

    /**
     * 根据assetipid查询实体
     *
     * @param assetipid
     * @return
     */
    public List<Host> findByAssetIpId(String assetipid) {
        return hostDao.findByassetipid(assetipid);
    }

    /**
     * 根据assetipid批量删除
     *
     * @param assetipid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByAssetipid(String assetipid) {
        hostDao.deleteAllByAssetipid(assetipid);
    }

    /**
     * 根据hostname查询实体
     *
     * @param hostname
     * @return
     */
    public Host findByHostname(String hostname) {
        return hostDao.findByHostname(hostname);
    }

    /**
     * 根据assetipid查询
     *
     * @param assetipid assetipid
     * @return
     */
    public List<Host> findAllByAssetipid(String assetipid) {
        return hostDao.findAllByAssetipid(assetipid);
    }

    /**
     * 根据assetipid查询
     *
     * @param assetipid assetipid
     * @param hostname  hostname
     * @return
     */
    public Host findByAssetipidAndHostname(String assetipid, String hostname) {
        return hostDao.findByAssetipidAndHostname(assetipid, hostname);
    }
}

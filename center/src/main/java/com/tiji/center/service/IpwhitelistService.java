package com.tiji.center.service;

import com.tiji.center.dao.IpwhitelistDao;
import com.tiji.center.pojo.Ipwhitelist;
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
 * ipwhitelist服务层
 *
 * @author 贰拾壹
 */
@Service
public class IpwhitelistService {

    @Autowired
    private IpwhitelistDao ipwhitelistDao;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private IpportwhitelistService ipportwhitelistService;


    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Ipwhitelist> findAll() {
        return ipwhitelistDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Ipwhitelist> findSearch(Map whereMap, int page, int size) {
        Specification<Ipwhitelist> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ipwhitelistDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Ipwhitelist> findSearch(Map whereMap) {
        Specification<Ipwhitelist> specification = createSpecification(whereMap);
        return ipwhitelistDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Ipwhitelist findById(String id) {
        return ipwhitelistDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param ipwhitelist
     */
    public void add(Ipwhitelist ipwhitelist) {
        if (Objects.isNull(ipwhitelist.getId())) {
            ipwhitelist.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(ipwhitelist.getCheckwhitelist())) {
            ipwhitelist.setCheckwhitelist(false);
        }
        if (Objects.isNull(ipwhitelist.getNotifywhitelist())) {
            ipwhitelist.setNotifywhitelist(false);
        }
        ipwhitelistDao.save(ipwhitelist);
    }

    /**
     * 修改
     *
     * @param ipwhitelist
     */
    public void update(Ipwhitelist ipwhitelist) {
        ipwhitelistDao.save(ipwhitelist);
    }


    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        ipwhitelistDao.deleteById(id);
        //同时删除ip-端口白名单
        ipportwhitelistService.deleteAllByipwhitelistid(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        ipwhitelistDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Ipwhitelist> createSpecification(Map searchMap) {

        return (Specification<Ipwhitelist>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // ip
            if (searchMap.get("ip") != null && !"".equals(searchMap.get("ip"))) {
                predicateList.add(cb.like(root.get("ip").as(String.class), "%" + (String) searchMap.get("ip") + "%"));
            }
            //检测白名单
            if (searchMap.get("checkwhitelist") != null && !"".equals(searchMap.get("checkwhitelist"))) {
                predicateList.add(cb.equal(root.get("checkwhitelist").as(Boolean.class), searchMap.get("checkwhitelist")));
            }
            //提醒白名单
            if (searchMap.get("notifywhitelist") != null && !"".equals(searchMap.get("notifywhitelist"))) {
                predicateList.add(cb.equal(root.get("notifywhitelist").as(Boolean.class), searchMap.get("notifywhitelist")));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 批量增加
     *
     * @param ipWhitelistList
     */
    public void batchAdd(List<Ipwhitelist> ipWhitelistList) {
        ipwhitelistDao.saveAll(ipWhitelistList);
    }

    /**
     * 查根据ip查询
     *
     * @return
     */
    public Ipwhitelist findByIp(String ip) {
        return ipwhitelistDao.findByIp(ip);
    }
}

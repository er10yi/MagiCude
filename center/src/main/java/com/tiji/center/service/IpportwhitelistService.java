package com.tiji.center.service;

import com.tiji.center.dao.IpportwhitelistDao;
import com.tiji.center.pojo.Ipportwhitelist;
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
 * ipportwhitelist服务层
 *
 * @author 贰拾壹
 */
@Service
public class IpportwhitelistService {

    @Autowired
    private IpportwhitelistDao ipportwhitelistDao;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private IpwhitelistService ipwhitelistService;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Ipportwhitelist> findAll() {
        return ipportwhitelistDao.findAll();
    }

    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Ipportwhitelist> findSearch(Map whereMap, int page, int size) {
        Specification<Ipportwhitelist> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return ipportwhitelistDao.findAll(specification, pageRequest);
    }

    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Ipportwhitelist> findSearch(Map whereMap) {
        Specification<Ipportwhitelist> specification = createSpecification(whereMap);
        return ipportwhitelistDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Ipportwhitelist findById(String id) {
        return ipportwhitelistDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param ipportwhitelist
     */
    public void add(Ipportwhitelist ipportwhitelist) {
        if (Objects.isNull(ipportwhitelist.getId())) {
            ipportwhitelist.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(ipportwhitelist.getCheckwhitelist())) {
            ipportwhitelist.setCheckwhitelist(false);
        }
        if (Objects.isNull(ipportwhitelist.getNotifywhitelist())) {
            ipportwhitelist.setNotifywhitelist(false);
        }
        ipportwhitelistDao.save(ipportwhitelist);

        //去掉ip白名单
        Ipwhitelist ipwhitelist = ipwhitelistService.findById(ipportwhitelist.getIpwhitelistid());
        if (ipportwhitelist.getCheckwhitelist()) {
            ipwhitelist.setCheckwhitelist(false);
        }
        if (ipportwhitelist.getNotifywhitelist()) {
            ipwhitelist.setNotifywhitelist(false);
        }
        ipwhitelistService.update(ipwhitelist);
    }

    /**
     * 修改
     *
     * @param ipportwhitelist
     */
    public void update(Ipportwhitelist ipportwhitelist) {
        ipportwhitelistDao.save(ipportwhitelist);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        ipportwhitelistDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        ipportwhitelistDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Ipportwhitelist> createSpecification(Map searchMap) {

        return (Specification<Ipportwhitelist>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 端口
            if (searchMap.get("port") != null && !"".equals(searchMap.get("port"))) {
                predicateList.add(cb.like(root.get("port").as(String.class), "%" + searchMap.get("port") + "%"));
            }
            // ip白名单编号
            if (searchMap.get("ipwhitelistid") != null && !"".equals(searchMap.get("ipwhitelistid"))) {
                predicateList.add(cb.equal(root.get("ipwhitelistid").as(String.class), searchMap.get("ipwhitelistid")));
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
     * @param ipportwhitelistList
     */
    public void batchAdd(List<Ipportwhitelist> ipportwhitelistList) {
        ipportwhitelistDao.saveAll(ipportwhitelistList);
    }

    /**
     * 根据ipwhitelistid、port查询实体
     *
     * @param ipwhitelistid
     * @param port
     * @return
     */
    public Ipportwhitelist findByIpwhitelistidAndPort(String ipwhitelistid, String port) {
        return ipportwhitelistDao.findByIpwhitelistidAndPort(ipwhitelistid, port);
    }


    /**
     * 根据ipwhitelistid查询所有端口
     *
     * @param ipwhitelistid
     * @return
     */
    public List<String> findAllPortByIpwhitelistid(String ipwhitelistid) {
        return ipportwhitelistDao.findAllPortByIpwhitelistid(ipwhitelistid);
    }

    /**
     * 根据ipwhitelistid批量删除
     *
     * @param ipwhitelistid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByipwhitelistid(String ipwhitelistid) {
        ipportwhitelistDao.deleteAllByipwhitelistid(ipwhitelistid);
    }
}

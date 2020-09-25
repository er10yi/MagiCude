package com.tiji.center.service;

import com.tiji.center.dao.AssetipDao;
import com.tiji.center.pojo.Assetip;
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
 * assetip服务层
 *
 * @author 贰拾壹
 */
@Service
public class AssetipService {

    @Autowired
    private AssetipDao assetipDao;

    @Autowired
    private IdWorker idWorker;


    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Assetip> findAll() {
        return assetipDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Assetip> findSearch(Map whereMap, int page, int size) {
        Specification<Assetip> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return assetipDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Assetip> findSearch(Map whereMap) {
        Specification<Assetip> specification = createSpecification(whereMap);
        return assetipDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Assetip findById(String id) {
        return assetipDao.findById(id).isPresent() ? assetipDao.findById(id).get() : null;
    }

    /**
     * 增加
     *
     * @param assetip
     */
    public void add(Assetip assetip) {
        if (Objects.isNull(assetip.getId())) {
            assetip.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(assetip.getCheckwhitelist())) {
            assetip.setCheckwhitelist(false);
        }
        if (Objects.isNull(assetip.getAssetnotifywhitelist())) {
            assetip.setAssetnotifywhitelist(false);
        }
        assetipDao.save(assetip);
    }

    /**
     * 修改
     *
     * @param assetip
     */
    public void update(Assetip assetip) {
        assetipDao.save(assetip);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        assetipDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        assetipDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Assetip> createSpecification(Map searchMap) {

        return (Specification<Assetip>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 资产ip编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 项目信息编号
            if (searchMap.get("projectinfoid") != null && !"".equals(searchMap.get("projectinfoid"))) {
                predicateList.add(cb.equal(root.get("projectinfoid").as(String.class), searchMap.get("projectinfoid")));
            }
            // ipv4地址
            if (searchMap.get("ipaddressv4") != null && !"".equals(searchMap.get("ipaddressv4"))) {
                predicateList.add(cb.like(root.get("ipaddressv4").as(String.class), "%" + searchMap.get("ipaddressv4") + "%"));
            }
            // ipv6地址
            if (searchMap.get("ipaddressv6") != null && !"".equals(searchMap.get("ipaddressv6"))) {
                predicateList.add(cb.like(root.get("ipaddressv6").as(String.class), "%" + searchMap.get("ipaddressv6") + "%"));
            }
            // 备注
            if (searchMap.get("remark") != null && !"".equals(searchMap.get("remark"))) {
                predicateList.add(cb.like(root.get("remark").as(String.class), "%" + searchMap.get("remark") + "%"));
            }
            //安全检测白名单
            if (searchMap.get("checkwhitelist") != null && !"".equals(searchMap.get("checkwhitelist"))) {
                predicateList.add(cb.equal(root.get("checkwhitelist").as(Boolean.class), (searchMap.get("checkwhitelist"))));
            }
            //资产提醒白名单
            if (searchMap.get("assetnotifywhitelist") != null && !"".equals(searchMap.get("assetnotifywhitelist"))) {
                predicateList.add(cb.equal(root.get("assetnotifywhitelist").as(Boolean.class), searchMap.get("assetnotifywhitelist")));
            }
            //ip发现时间
            if (searchMap.get("activetime") != null && !"".equals(searchMap.get("activetime"))) {
                List<String> activetimeList = (List<String>) searchMap.get("activetime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("activetime").as(String.class), activetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("activetime").as(String.class), activetimeList.get(1)));
                //predicateList.add(cb.like(root.get("activetime").as(String.class), "%" + searchMap.get("activetime") + "%"));
            }
            //ip下线时间
            if (searchMap.get("passivetime") != null && !"".equals(searchMap.get("passivetime"))) {
                List<String> passivetimeList = (List<String>) searchMap.get("passivetime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("passivetime").as(String.class), passivetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("passivetime").as(String.class), passivetimeList.get(1)));
                //predicateList.add(cb.like(root.get("passivetime").as(String.class), "%" + searchMap.get("passivetime") + "%"));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 批量增加
     *
     * @param assetipList
     */
    public void batchAdd(List<Assetip> assetipList) {
        assetipDao.saveAll(assetipList);
    }


    /**
     * 查询所有Passivetime为空的ipv4
     *
     * @return Assetip list
     */
    public List<Assetip> findAllIpv4ByPassivetimeIsNull() {
        return assetipDao.findAllByPassivetimeIsNull();
    }

    /**
     * 查询所有未下线的ipv4
     *
     * @return String list
     */
    public List<String> findAllDistinctIpaddressv4ListAndPassivetimeIsNull() {
        return assetipDao.findAllDistinctIpaddressv4ListAndPassivetimeIsNull();
    }


    /**
     * 根据ip查询passivetime为空的ip
     *
     * @return Assetip
     */
    public Assetip findByIpaddressv4AndPassivetimeIsNull(String ip) {
        return assetipDao.findByIpaddressv4AndPassivetimeIsNull(ip);
    }

    /**
     * 根据ip查询passivetime为空的ip
     *
     * @return List
     */
    //for test
    //getContactByIp
    public List<String> findContactInfoByIpv4(String ip) {
        return assetipDao.findContactInfoByIpv4(ip);
    }

    /**
     * 根据ID查询未下线且不在check白名单的ip
     *
     * @param id
     * @return
     */
    public Assetip findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(String id) {
        return assetipDao.findByIdAndCheckwhitelistIsFalseAndPassivetimeIsNull(id);
    }


    /**
     * 修改
     *
     * @param projectinfoid
     * @param checkwhitelist
     * @param assetNotifywhitelist
     */
    @Transactional(value = "masterTransactionManager")
    public void updateByProjectinfoidAndCheckwhitelistAndAssetNotifywhitelist(String projectinfoid, Boolean checkwhitelist, Boolean assetNotifywhitelist) {
        assetipDao.updateByProjectinfoidAndCheckwhitelistAndAssetNotifywhitelist(projectinfoid, checkwhitelist, assetNotifywhitelist);
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    public List<String> findByIds(String[] ids) {
        List<String> assetIpIdAndIpList = new ArrayList<>();
        for (String assetIpId : ids) {
            Assetip assetip = findById(assetIpId);
            if (Objects.isNull(assetip)) {
                assetIpIdAndIpList.add(assetIpId + "-" + null);
            } else {
                assetIpIdAndIpList.add(assetIpId + "-" + assetip.getIpaddressv4());
            }
        }
        return assetIpIdAndIpList.isEmpty() ? null : assetIpIdAndIpList;
    }


    /**
     * 根据projectinfoid将projectinfoid置空
     *
     * @param projectinfoid
     * @return
     */
    @Transactional(value = "masterTransactionManager")
    public void updateAssetipByProjectinfoidSetProjectinfoid2Null(String projectinfoid) {
        assetipDao.updateAssetipByProjectinfoidSetProjectinfoid2Null(projectinfoid);
    }

    /**
     * 将所有projectinfoid置空
     */
    @Transactional(value = "masterTransactionManager")
    public void updateAssetipSetProjectinfoidNull() {
        assetipDao.updateAssetipSetProjectinfoidNull();
    }

}

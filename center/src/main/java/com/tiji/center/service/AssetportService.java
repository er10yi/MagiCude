package com.tiji.center.service;

import com.tiji.center.dao.AssetportDao;
import com.tiji.center.pojo.Assetport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * assetport服务层
 *
 * @author 贰拾壹
 */
@Service
public class AssetportService {

    @Autowired
    private AssetportDao assetportDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Assetport> findAll() {
        return assetportDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Assetport> findSearch(Map whereMap, int page, int size) {
        Specification<Assetport> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return assetportDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Assetport> findSearch(Map whereMap) {
        Specification<Assetport> specification = createSpecification(whereMap);
        return assetportDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Assetport findById(String id) {
        return assetportDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param assetport
     */
    public void add(Assetport assetport) {
        if (Objects.isNull(assetport.getId())) {
            assetport.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(assetport.getCheckwhitelist())) {
            assetport.setCheckwhitelist(false);
        }
        if (Objects.isNull(assetport.getAssetnotifywhitelist())) {
            assetport.setAssetnotifywhitelist(false);
        }
        assetportDao.save(assetport);
    }

    /**
     * 修改
     *
     * @param assetport
     */
    public void update(Assetport assetport) {
        assetportDao.save(assetport);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        assetportDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Assetport> createSpecification(Map searchMap) {

        return (Specification<Assetport>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 端口编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 资产ip编号
            //if (searchMap.get("assetipid") != null && !"".equals(searchMap.get("assetipid"))) {
            //    predicateList.add(cb.equal(root.get("assetipid").as(String.class), searchMap.get("assetipid")));
            //}
            if (searchMap.get("assetipid") != null && !"".equals(searchMap.get("assetipid"))) {
                predicateList.add(cb.in(root.get("assetipid")).value(searchMap.get("assetipid")));
            }
            // 端口
            if (searchMap.get("port") != null && !"".equals(searchMap.get("port"))) {
                predicateList.add(cb.like(root.get("port").as(String.class), "%" + searchMap.get("port") + "%"));
            }
            // 端口协议
            if (searchMap.get("protocol") != null && !"".equals(searchMap.get("protocol"))) {
                predicateList.add(cb.like(root.get("protocol").as(String.class), "%" + searchMap.get("protocol") + "%"));
            }
            // 端口开放状态
            if (searchMap.get("state") != null && !"".equals(searchMap.get("state"))) {
                predicateList.add(cb.like(root.get("state").as(String.class), "%" + searchMap.get("state") + "%"));
            }
            // 端口服务
            if (searchMap.get("service") != null && !"".equals(searchMap.get("service"))) {
                predicateList.add(cb.like(root.get("service").as(String.class), "%" + searchMap.get("service") + "%"));
            }
            // 服务版本
            if (searchMap.get("version") != null && !"".equals(searchMap.get("version"))) {
                predicateList.add(cb.like(root.get("version").as(String.class), "%" + searchMap.get("version") + "%"));
            }
            //安全检测白名单
            if (searchMap.get("checkwhitelist") != null && !"".equals(searchMap.get("checkwhitelist"))) {
                predicateList.add(cb.equal(root.get("checkwhitelist").as(Boolean.class), searchMap.get("checkwhitelist")));
            }
            //资产提醒白名单
            if (searchMap.get("assetnotifywhitelist") != null && !"".equals(searchMap.get("assetnotifywhitelist"))) {
                predicateList.add(cb.equal(root.get("assetnotifywhitelist").as(Boolean.class), searchMap.get("assetnotifywhitelist")));
            }

            //端口发现时间
            if (searchMap.get("uptime") != null && !"".equals(searchMap.get("uptime"))) {
                List<String> uptimeList = (List<String>) searchMap.get("uptime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("uptime").as(String.class), uptimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("uptime").as(String.class), uptimeList.get(1)));
            }

            //端口关闭时间
            if (searchMap.get("downtime") != null && !"".equals(searchMap.get("downtime"))) {
                List<String> downtimeList = (List<String>) searchMap.get("downtime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("downtime").as(String.class), downtimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("downtime").as(String.class), downtimeList.get(1)));
            }
            //端口修改时间
            if (searchMap.get("changedtime") != null && !"".equals(searchMap.get("changedtime"))) {
                List<String> changedtimeList = (List<String>) searchMap.get("changedtime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("changedtime").as(String.class), changedtimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("changedtime").as(String.class), changedtimeList.get(1)));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 批量增加
     *
     * @param portList
     */
    public void batchAdd(List<Assetport> portList) {
        assetportDao.saveAll(portList);
    }

    /**
     * 根据assetipid,port查询未下线的端口
     *
     * @param assetipid
     * @param port
     * @return Assetport
     */
    public Assetport findByAssetipidAndPortAndDowntimeIsNull(String assetipid, String port) {
        return assetportDao.findByAssetipidAndPortAndDowntimeIsNull(assetipid, port);
    }

    /**
     * 根据assetipid所有查询未下线的端口
     *
     * @param assetipid
     * @return List
     */
    public List<Assetport> findAllByAssetipidAndDowntimeIsNull(String assetipid) {
        return assetportDao.findAllByAssetipidAndDowntimeIsNull(assetipid);
    }

    /**
     * 根据service查询未下线的端口
     *
     * @param assetService
     * @return List
     */
    public List<Assetport> findByServiceLikeAndDowntimeIsNull(String assetService) {
        return assetportDao.findByServiceLikeAndDowntimeIsNull(assetService);
    }

    /**
     * 根据version查询未下线的端口
     *
     * @param assetVersion
     * @return List
     */
    public List<Assetport> findByVersionLikeAndDowntimeIsNull(String assetVersion) {
        return assetportDao.findByVersionLikeAndDowntimeIsNull(assetVersion);
    }


    /**
     * 查询所有service为空且未下线的端口
     *
     * @return
     */
    public List<Assetport> findAllByServiceAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(String service) {
        return assetportDao.findAllByServiceAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(service);
    }

    /**
     * 查询所有version为空且未下线的端口
     *
     * @return
     */
    public List<Assetport> findAllByVersionAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(String version) {
        return assetportDao.findAllByVersionAndServiceIsNullAndCheckwhitelistIsFalseAndDowntimeIsNull(version);
    }


    public List<Assetport> findByVersionLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(String versionName, String state) {
        return assetportDao.findByVersionLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(versionName, state);
    }

    public List<Assetport> findByServiceLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(String serviceName, String state) {
        return assetportDao.findByServiceLikeAndDowntimeIsNullAndCheckwhitelistIsFalseAndStateEquals(serviceName, state);
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    public List<String> findByIds(String[] ids) {
        List<String> assetPortIdAndAssetIpIdList = new ArrayList<>();

        for (String assetportid : ids) {
            Assetport assetport = findById(assetportid);
            if (Objects.isNull(assetport)) {
                assetPortIdAndAssetIpIdList.add(assetportid + "-" + null + "-" + null);
            } else {
                assetPortIdAndAssetIpIdList.add(assetportid + "-" + assetport.getAssetipid() + "-" + assetport.getPort());
            }
        }
        return assetPortIdAndAssetIpIdList.isEmpty() ? null : assetPortIdAndAssetIpIdList;
    }

    /**
     * 根据assetipid批量删除
     *
     * @param assetipid
     */
    @Transactional(value = "masterTransactionManager")
    public List<Assetport> deleteAllByAssetipid(String assetipid) {
        return assetportDao.deleteAllByAssetipid(assetipid);
    }


    /**
     * 查询service并去重
     *
     * @return List
     */
    public List<String> findAllDistinctService() {
        return assetportDao.findAllDistinctService();
    }

    /**
     * 查询version并去重
     *
     * @return List
     */
    public List<String> findAllDistinctVersion() {
        return assetportDao.findAllDistinctVersion();
    }

    /**
     * 根据assetipid查询
     *
     * @param assetipid assetipid
     * @return
     */

    public List<Assetport> findAllByAssetipid(String assetipid) {
        return assetportDao.findAllByAssetipid(assetipid);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        assetportDao.deleteAllByIds(ids);
    }

    /**
     * 根据id数组查询数量
     *
     * @param ids
     * @return
     */
    public List<String> findCountByIds(List<String> ids) {
        List<String> idAndCount = new ArrayList<>();
        List<String> vulnCountList = assetportDao.findVulnCountByIds(ids);
        List<String> vulnCountOnlineList = assetportDao.findVulnCountOnlineByIds(ids);

        Map<String, String> idVulnCountMap = new LinkedHashMap<>();
        Map<String, String> idVulnCountOnlineMap = new LinkedHashMap<>();

        vulnCountList.parallelStream().forEach(temp -> {
            String id = temp.split(",")[0];
            String vulnCount = temp.split(",")[1];
            idVulnCountMap.put(id, vulnCount);
        });
        vulnCountOnlineList.parallelStream().forEach(temp -> {
            String id = temp.split(",")[0];
            String vulnCount = temp.split(",")[1];
            idVulnCountOnlineMap.put(id, vulnCount);
        });

        // id - vuln count - vuln count online
        ids.forEach(id -> {
            String temp;
            temp = idVulnCountMap.getOrDefault(id, "0");
            temp += ":" + idVulnCountOnlineMap.getOrDefault(id, "0");
            idAndCount.add(temp);

        });
        return idAndCount.isEmpty() ? null : idAndCount;
    }
}

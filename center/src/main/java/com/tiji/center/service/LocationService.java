package com.tiji.center.service;

import com.tiji.center.dao.LocationDao;
import com.tiji.center.pojo.Location;
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
 * location服务层
 *
 * @author 贰拾壹
 */
@Service
public class LocationService {

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Location> findAll() {
        return locationDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Location> findSearch(Map whereMap, int page, int size) {
        Specification<Location> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return locationDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Location> findSearch(Map whereMap) {
        Specification<Location> specification = createSpecification(whereMap);
        return locationDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Location findById(String id) {
        return locationDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param location
     */
    public void add(Location location) {
        if (Objects.isNull(location.getId())) {
            location.setId(idWorker.nextId() + "");
        }
        locationDao.save(location);
    }

    /**
     * 修改
     *
     * @param location
     */
    public void update(Location location) {
        locationDao.save(location);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        locationDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        locationDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Location> createSpecification(Map searchMap) {

        return (Specification<Location>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 位置编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 资产ip编号
            if (searchMap.get("assetipid") != null && !"".equals(searchMap.get("assetipid"))) {
                predicateList.add(cb.like(root.get("assetipid").as(String.class), "%" + (String) searchMap.get("assetipid") + "%"));
            }
            // 国家
            if (searchMap.get("country") != null && !"".equals(searchMap.get("country"))) {
                predicateList.add(cb.like(root.get("country").as(String.class), "%" + (String) searchMap.get("country") + "%"));
            }
            // 省份
            if (searchMap.get("province") != null && !"".equals(searchMap.get("province"))) {
                predicateList.add(cb.like(root.get("province").as(String.class), "%" + (String) searchMap.get("province") + "%"));
            }
            // 道路
            if (searchMap.get("road") != null && !"".equals(searchMap.get("road"))) {
                predicateList.add(cb.like(root.get("road").as(String.class), "%" + (String) searchMap.get("road") + "%"));
            }
            // 大厦
            if (searchMap.get("building") != null && !"".equals(searchMap.get("building"))) {
                predicateList.add(cb.like(root.get("building").as(String.class), "%" + (String) searchMap.get("building") + "%"));
            }
            // 楼层
            if (searchMap.get("floor") != null && !"".equals(searchMap.get("floor"))) {
                predicateList.add(cb.like(root.get("floor").as(String.class), "%" + (String) searchMap.get("floor") + "%"));
            }
            // 方位
            if (searchMap.get("direction") != null && !"".equals(searchMap.get("direction"))) {
                predicateList.add(cb.like(root.get("direction").as(String.class), "%" + (String) searchMap.get("direction") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据assetipid批量删除
     *
     * @param assetipid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByAssetipid(String assetipid) {
        locationDao.deleteAllByAssetipid(assetipid);
    }

}

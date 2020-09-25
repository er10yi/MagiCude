package com.tiji.center.service;

import com.tiji.center.dao.WebinfoDao;
import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Webinfo;
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
 * webinfo服务层
 *
 * @author 贰拾壹
 */
@Service
public class WebinfoService {

    @Autowired
    private WebinfoDao webinfoDao;
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Webinfo> findAll() {
        return webinfoDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Webinfo> findSearch(Map whereMap, int page, int size) {
        Specification<Webinfo> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return webinfoDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Webinfo> findSearch(Map whereMap) {
        Specification<Webinfo> specification = createSpecification(whereMap);
        return webinfoDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Webinfo findById(String id) {
        return webinfoDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param webinfo
     */
    public void add(Webinfo webinfo) {
        if (Objects.isNull(webinfo.getId())) {
            webinfo.setId(idWorker.nextId() + "");
        }
        webinfoDao.save(webinfo);
    }

    /**
     * 修改
     *
     * @param webinfo
     */
    public void update(Webinfo webinfo) {
        webinfoDao.save(webinfo);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        webinfoDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        webinfoDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Webinfo> createSpecification(Map searchMap) {

        return (Specification<Webinfo>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // web信息编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 端口编号
            if (searchMap.get("portid") != null && !"".equals(searchMap.get("portid"))) {
                predicateList.add(cb.like(root.get("portid").as(String.class), "%" + searchMap.get("portid") + "%"));
            }
            // 标题白名单编号
            if (searchMap.get("titlewhitelistid") != null && !"".equals(searchMap.get("titlewhitelistid"))) {
                predicateList.add(cb.like(root.get("titlewhitelistid").as(String.class), "%" + searchMap.get("titlewhitelistid") + "%"));
            }
            // 页面标题
            if (searchMap.get("title") != null && !"".equals(searchMap.get("title"))) {
                predicateList.add(cb.like(root.get("title").as(String.class), "%" + searchMap.get("title") + "%"));
            }
            // body子节点文本内容
            if (searchMap.get("bodychildrenstextcontent") != null && !"".equals(searchMap.get("bodychildrenstextcontent"))) {
                predicateList.add(cb.like(root.get("bodychildrenstextcontent").as(String.class), "%" + searchMap.get("bodychildrenstextcontent") + "%"));
            }
            // 响应头中的服务
            if (searchMap.get("server") != null && !"".equals(searchMap.get("server"))) {
                predicateList.add(cb.like(root.get("server").as(String.class), "%" + searchMap.get("server") + "%"));
            }
            // xpoweredby
            if (searchMap.get("xpoweredby") != null && !"".equals(searchMap.get("xpoweredby"))) {
                predicateList.add(cb.like(root.get("xpoweredby").as(String.class), "%" + searchMap.get("xpoweredby") + "%"));
            }
            // 设置cookie
            if (searchMap.get("setcookie") != null && !"".equals(searchMap.get("setcookie"))) {
                predicateList.add(cb.like(root.get("setcookie").as(String.class), "%" + searchMap.get("setcookie") + "%"));
            }
            // 认证方式
            if (searchMap.get("wwwauthenticate") != null && !"".equals(searchMap.get("wwwauthenticate"))) {
                predicateList.add(cb.like(root.get("wwwauthenticate").as(String.class), "%" + searchMap.get("wwwauthenticate") + "%"));
            }
            // appname
            if (searchMap.get("appname") != null && !"".equals(searchMap.get("appname"))) {
                predicateList.add(cb.like(root.get("appname").as(String.class), "%" + searchMap.get("appname") + "%"));
            }
            // 应用版本
            if (searchMap.get("appversion") != null && !"".equals(searchMap.get("appversion"))) {
                predicateList.add(cb.like(root.get("appversion").as(String.class), "%" + searchMap.get("appversion") + "%"));
            }
            // devlanguage
            if (searchMap.get("devlanguage") != null && !"".equals(searchMap.get("devlanguage"))) {
                predicateList.add(cb.like(root.get("devlanguage").as(String.class), "%" + searchMap.get("devlanguage") + "%"));
            }
            //页面抓取时间
            if (searchMap.get("crawltime") != null && !"".equals(searchMap.get("crawltime"))) {
                List<String> activetimeList = (List<String>) searchMap.get("crawltime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("crawltime").as(String.class), activetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("crawltime").as(String.class), activetimeList.get(1)));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据ID查询实体
     *
     * @param portId
     * @return
     */
    public List<Webinfo> findByPortId(String portId) {
        return webinfoDao.findByPortid(portId);
    }


    /**
     * 根据assetportId批量删除
     *
     * @param assetportId
     */
    @Transactional(value = "masterTransactionManager")
    public List<Webinfo> deleteAllByPortid(String assetportId) {
        return webinfoDao.deleteAllByPortid(assetportId);
    }

    /**
     * 根据assetportid查询
     *
     * @param assetportid assetportid
     * @return
     */
    public List<Webinfo> findAllByAssetportid(String assetportid) {
        return webinfoDao.findAllByPortid(assetportid);
    }

    /**
     * 根据assetportids查询
     *
     * @param assetportids assetportids
     * @return
     */
    public List<Webinfo> findAllByAssetportIds(String[] assetportids) {
        List<Webinfo> resultList = new ArrayList<>();
        for (String id : assetportids) {
            List<Webinfo> webinfoList = webinfoDao.findAllByPortid(id);
            for (Webinfo webinfo : webinfoList) {
                String assetportid = webinfo.getPortid();
                Assetport assetport = assetportService.findById(assetportid);
                webinfo.setPortid(assetport.getPort());
                resultList.add(webinfo);
            }
        }
        return resultList;
    }
}

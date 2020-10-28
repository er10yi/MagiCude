package com.tiji.center.service;

import com.tiji.center.dao.UrlDao;
import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Url;
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
 * url服务层
 *
 * @author 贰拾壹
 */
@Service
public class UrlService {

    @Autowired
    private UrlDao urlDao;

    @Autowired
    private IdWorker idWorker;
    /**
     * 根据webinfoids查询所有链接pojo，webinfoid替换成端口
     *
     * @param webinfoids
     * @return
     */
    @Autowired
    private WebinfoService webinfoService;
    @Autowired
    private AssetportService assetportService;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Url> findAll() {
        return urlDao.findAll();
    }

    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Url> findSearch(Map whereMap, int page, int size) {
        Specification<Url> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return urlDao.findAll(specification, pageRequest);
    }

    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Url> findSearch(Map whereMap) {
        Specification<Url> specification = createSpecification(whereMap);
        return urlDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Url findById(String id) {
        return urlDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param url
     */
    public void add(Url url) {
        if (Objects.isNull(url.getId())) {
            url.setId(idWorker.nextId() + "");
        }
        urlDao.save(url);
    }

    /**
     * 修改
     *
     * @param url
     */
    public void update(Url url) {
        urlDao.save(url);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        urlDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        urlDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Url> createSpecification(Map searchMap) {

        return (Specification<Url>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // url编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // web信息编号
            if (searchMap.get("webinfoid") != null && !"".equals(searchMap.get("webinfoid"))) {
                predicateList.add(cb.like(root.get("webinfoid").as(String.class), "%" + searchMap.get("webinfoid") + "%"));
            }
            // 名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
            }
            // url
            if (searchMap.get("url") != null && !"".equals(searchMap.get("url"))) {
                predicateList.add(cb.like(root.get("url").as(String.class), "%" + searchMap.get("url") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[0]));

        };

    }

    /**
     * 根据webinfoid批量删除
     *
     * @param webinfoid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByWebinfoid(String webinfoid) {
        urlDao.deleteAllByWebinfoid(webinfoid);
    }

    /**
     * 根据webinfoid查询
     *
     * @param webinfoid webinfoid
     * @return
     */
    public List<Url> findAllByWebinfoid(String webinfoid) {
        return urlDao.findAllByWebinfoid(webinfoid);
    }

    /**
     * 根据webinfoids查询所有链接pojo
     *
     * @param webinfoids
     * @return
     */
    public List<Url> findAllByWebinfoids(String[] webinfoids) {
        List<Url> urlList = new ArrayList<>();
        for (String webinfoid : webinfoids) {
            urlList.addAll(urlDao.findAllByWebinfoid(webinfoid));
        }
        return urlList;
    }

    public List<Url> findAllByWebinfoIds2Port(String[] webinfoids) {
        List<Url> resultList = new ArrayList<>();
        for (String webinfoid : webinfoids) {
            List<Url> urlList = urlDao.findAllByWebinfoid(webinfoid);
            for (Url url : urlList) {
                Webinfo webinfo = webinfoService.findById(webinfoid);
                String portid = webinfo.getPortid();
                Assetport assetport = assetportService.findById(portid);
                url.setWebinfoid(assetport.getPort());
                resultList.add(url);
            }
        }
        return resultList;
    }

    public StringBuilder findLinksByWebinfoId(String webinfoid) {
        StringBuilder result = new StringBuilder();
        List<Url> urlList = urlDao.findAllByWebinfoid(webinfoid);
        urlList.parallelStream().forEach(url -> result.append(url.getName()).append(" ").append(url.getUrl()).append("\n"));
        return result;
    }
}

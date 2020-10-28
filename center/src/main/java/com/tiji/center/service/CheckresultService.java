package com.tiji.center.service;

import com.tiji.center.dao.CheckresultDao;
import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Checkresult;
import com.tiji.center.pojo.CheckresultVuln;
import com.tiji.center.pojo.Vuln;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * checkresult服务层
 *
 * @author 贰拾壹
 */
@Service
public class CheckresultService {

    @Autowired
    private CheckresultDao checkresultDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private CheckresultVulnService checkresultVulnService;
    @Autowired
    private VulnService vulnService;
    @Autowired
    private AssetportService assetportService;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Checkresult> findAll() {
        return checkresultDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Checkresult> findSearch(Map whereMap, int page, int size) {
        Specification<Checkresult> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return checkresultDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Checkresult> findSearch(Map whereMap) {
        Specification<Checkresult> specification = createSpecification(whereMap);
        return checkresultDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Checkresult findById(String id) {
        return checkresultDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param checkresult
     */
    public void add(Checkresult checkresult) {
        if (Objects.isNull(checkresult.getId())) {
            checkresult.setId(idWorker.nextId() + "");
        }
        checkresultDao.save(checkresult);
    }

    /**
     * 修改
     *
     * @param checkresult
     */
    public void update(Checkresult checkresult) {
        checkresultDao.save(checkresult);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        checkresultDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        checkresultDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Checkresult> createSpecification(Map searchMap) {

        return (Specification<Checkresult>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // 检测结果编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.in(root.get("id")).value(searchMap.get("id")));
            }
            // 端口编号
            if (searchMap.get("assetportid") != null && !"".equals(searchMap.get("assetportid"))) {
                predicateList.add(cb.in(root.get("assetportid")).value(searchMap.get("assetportid")));
            }
            // 检测结果名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
            }
            // 检测结果
            if (searchMap.get("result") != null && !"".equals(searchMap.get("result"))) {
                predicateList.add(cb.like(root.get("result").as(String.class), "%" + searchMap.get("result") + "%"));
            }
            // 缺陷风险级别
            if (searchMap.get("risk") != null && !"".equals(searchMap.get("risk"))) {
                predicateList.add(cb.like(root.get("risk").as(String.class), "%" + searchMap.get("risk") + "%"));
            }

            //发现时间
            if (searchMap.get("activetime") != null && !"".equals(searchMap.get("activetime"))) {
                List<String> activetimeList = (List<String>) searchMap.get("activetime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("activetime").as(String.class), activetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("activetime").as(String.class), activetimeList.get(1)));
            }

            //修复时间
            if (searchMap.get("passivetime") != null && !"".equals(searchMap.get("passivetime"))) {
                List<String> passivetimeList = (List<String>) searchMap.get("passivetime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("passivetime").as(String.class), passivetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("passivetime").as(String.class), passivetimeList.get(1)));
            }

            // 备注
            if (searchMap.get("remark") != null && !"".equals(searchMap.get("remark"))) {
                predicateList.add(cb.like(root.get("remark").as(String.class), "%" + searchMap.get("remark") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[0]));

        };

    }

    /**
     * 根据assetPortId、插件Name查询未修复的漏洞端口
     *
     * @param assetPortId
     * @param pluginName
     * @return Checkresult
     */
    public Checkresult findByAssetportidAndNameAndPassivetimeIsNull(String assetPortId, String pluginName) {
        return checkresultDao.findByAssetportidAndNameAndPassivetimeIsNull(assetPortId, pluginName);
    }


    /**
     * 根据name查询实体
     *
     * @param name
     * @return
     */
    public List<Checkresult> findAllByName(String name) {
        return checkresultDao.findAllByName(name);
    }


    /**
     * 根据assetportId批量删除
     *
     * @param assetportId
     */
    @Transactional(value = "masterTransactionManager")
    public List<Checkresult> deleteAllByAssetportid(String assetportId) {
        return checkresultDao.deleteAllByAssetportid(assetportId);
    }

    /**
     * 根据assetportid查询
     *
     * @param assetportid assetportid
     * @return
     */
    public List<Checkresult> findAllByAssetportid(String assetportid) {
        return checkresultDao.findAllByAssetportid(assetportid);
    }


    /**
     * 根据ids查询漏洞
     *
     * @param ids ids
     * @return id-漏洞名称
     */
    public List<String> findAllByIds(String[] ids) {
        List<String> resultList = new LinkedList<>();
        for (String id : ids) {
            List<CheckresultVuln> allByCheckresultid = checkresultVulnService.findAllByCheckresultid(id);
            if (!allByCheckresultid.isEmpty()) {
                allByCheckresultid.parallelStream().forEach(checkresultVuln -> {
                    String vulnid = checkresultVuln.getVulnid();
                    Vuln vuln = vulnService.findById(vulnid);
                    if (Objects.isNull(vuln)) {
                        resultList.add(id + "-" + null);
                    } else {
                        resultList.add(id + "-" + vuln.getName());
                    }

                });
            }
        }
        return resultList;
    }

    /**
     * 根据assetportids查询
     *
     * @param assetportids assetportids
     * @return
     */
    public List<Checkresult> findAllByAssetportIds(String[] assetportids) {
        List<Checkresult> resultList = new LinkedList<>();
        for (String id : assetportids) {
            List<Checkresult> checkresultList = checkresultDao.findAllByAssetportid(id);
            checkresultList.parallelStream().forEach(checkresult -> {
                String assetportid = checkresult.getAssetportid();
                Assetport assetport = assetportService.findById(assetportid);
                checkresult.setAssetportid(assetport.getPort());
                resultList.add(checkresult);
            });
        }
        return resultList;
    }

    public String findVulNameById(String id) {
        List<CheckresultVuln> checkresultVulnList = checkresultVulnService.findAllByCheckresultid(id);
        String vulnid = checkresultVulnList.get(0).getVulnid();
        return vulnService.findById(vulnid).getName();
    }
}

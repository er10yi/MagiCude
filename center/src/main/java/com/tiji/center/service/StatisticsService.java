package com.tiji.center.service;

import com.tiji.center.dao.StatisticsDao;
import com.tiji.center.pojo.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * statistics服务层
 *
 * @author 贰拾壹
 */
@Service
public class StatisticsService {

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Statistics> findAll() {
        return statisticsDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Statistics> findSearch(Map whereMap, int page, int size) {
        Specification<Statistics> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return statisticsDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Statistics> findSearch(Map whereMap) {
        Specification<Statistics> specification = createSpecification(whereMap);
        return statisticsDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Statistics findById(String id) {
        return statisticsDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param statistics
     */
    public void add(Statistics statistics) {
        if (Objects.isNull(statistics.getId())) {
            statistics.setId(idWorker.nextId() + "");
        }
        statisticsDao.save(statistics);
    }

    /**
     * 修改
     *
     * @param statistics
     */
    public void update(Statistics statistics) {
        statisticsDao.save(statistics);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        statisticsDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Statistics> createSpecification(Map searchMap) {

        return (Specification<Statistics>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // ip数
            if (searchMap.get("ipcount") != null && !"".equals(searchMap.get("ipcount"))) {
                predicateList.add(cb.like(root.get("ipcount").as(String.class), "%" + (String) searchMap.get("ipcount") + "%"));
            }
            // 未下线ip数
            if (searchMap.get("ipcountonline") != null && !"".equals(searchMap.get("ipcountonline"))) {
                predicateList.add(cb.like(root.get("ipcountonline").as(String.class), "%" + (String) searchMap.get("ipcountonline") + "%"));
            }
            // 端口数
            if (searchMap.get("portcount") != null && !"".equals(searchMap.get("portcount"))) {
                predicateList.add(cb.like(root.get("portcount").as(String.class), "%" + (String) searchMap.get("portcount") + "%"));
            }
            // 未下线端口数
            if (searchMap.get("portcountonline") != null && !"".equals(searchMap.get("portcountonline"))) {
                predicateList.add(cb.like(root.get("portcountonline").as(String.class), "%" + (String) searchMap.get("portcountonline") + "%"));
            }
            // 检测结果数
            if (searchMap.get("checkresultcount") != null && !"".equals(searchMap.get("checkresultcount"))) {
                predicateList.add(cb.like(root.get("checkresultcount").as(String.class), "%" + (String) searchMap.get("checkresultcount") + "%"));
            }
            // 未修复检测结果数
            if (searchMap.get("checkresultcountonline") != null && !"".equals(searchMap.get("checkresultcountonline"))) {
                predicateList.add(cb.like(root.get("checkresultcountonline").as(String.class), "%" + (String) searchMap.get("checkresultcountonline") + "%"));
            }
            // 信息检测结果数
            if (searchMap.get("infocount") != null && !"".equals(searchMap.get("infocount"))) {
                predicateList.add(cb.like(root.get("infocount").as(String.class), "%" + (String) searchMap.get("infocount") + "%"));
            }
            // 低危检测结果数
            if (searchMap.get("lowcount") != null && !"".equals(searchMap.get("lowcount"))) {
                predicateList.add(cb.like(root.get("lowcount").as(String.class), "%" + (String) searchMap.get("lowcount") + "%"));
            }
            // 中危检测结果数
            if (searchMap.get("mediumcount") != null && !"".equals(searchMap.get("mediumcount"))) {
                predicateList.add(cb.like(root.get("mediumcount").as(String.class), "%" + (String) searchMap.get("mediumcount") + "%"));
            }
            // 高危检测结果数
            if (searchMap.get("highcount") != null && !"".equals(searchMap.get("highcount"))) {
                predicateList.add(cb.like(root.get("highcount").as(String.class), "%" + (String) searchMap.get("highcount") + "%"));
            }
            // 严重检测结果数
            if (searchMap.get("criticalcount") != null && !"".equals(searchMap.get("criticalcount"))) {
                predicateList.add(cb.like(root.get("criticalcount").as(String.class), "%" + (String) searchMap.get("criticalcount") + "%"));
            }
            // 致命检测结果数
            if (searchMap.get("fatalcount") != null && !"".equals(searchMap.get("fatalcount"))) {
                predicateList.add(cb.like(root.get("fatalcount").as(String.class), "%" + (String) searchMap.get("fatalcount") + "%"));
            }
            // 未修复信息检测结果数
            if (searchMap.get("infocountonline") != null && !"".equals(searchMap.get("infocountonline"))) {
                predicateList.add(cb.like(root.get("infocountonline").as(String.class), "%" + (String) searchMap.get("infocountonline") + "%"));
            }
            // 未修复低危检测结果数
            if (searchMap.get("lowcountonline") != null && !"".equals(searchMap.get("lowcountonline"))) {
                predicateList.add(cb.like(root.get("lowcountonline").as(String.class), "%" + (String) searchMap.get("lowcountonline") + "%"));
            }
            // 未修复中危检测结果数
            if (searchMap.get("mediumcountonline") != null && !"".equals(searchMap.get("mediumcountonline"))) {
                predicateList.add(cb.like(root.get("mediumcountonline").as(String.class), "%" + (String) searchMap.get("mediumcountonline") + "%"));
            }
            // 未修复高危检测结果数
            if (searchMap.get("highcountonline") != null && !"".equals(searchMap.get("highcountonline"))) {
                predicateList.add(cb.like(root.get("highcountonline").as(String.class), "%" + (String) searchMap.get("highcountonline") + "%"));
            }
            // 未修复严重检测结果数
            if (searchMap.get("criticalcountonline") != null && !"".equals(searchMap.get("criticalcountonline"))) {
                predicateList.add(cb.like(root.get("criticalcountonline").as(String.class), "%" + (String) searchMap.get("criticalcountonline") + "%"));
            }
            // 未修复致命检测结果数
            if (searchMap.get("fatalcountonline") != null && !"".equals(searchMap.get("fatalcountonline"))) {
                predicateList.add(cb.like(root.get("fatalcountonline").as(String.class), "%" + (String) searchMap.get("fatalcountonline") + "%"));
            }
            // 高危端口数
            if (searchMap.get("riskportcount") != null && !"".equals(searchMap.get("riskportcount"))) {
                predicateList.add(cb.like(root.get("riskportcount").as(String.class), "%" + (String) searchMap.get("riskportcount") + "%"));
            }
            // 未下线高危端口数
            if (searchMap.get("riskportcountonline") != null && !"".equals(searchMap.get("riskportcountonline"))) {
                predicateList.add(cb.like(root.get("riskportcountonline").as(String.class), "%" + (String) searchMap.get("riskportcountonline") + "%"));
            }
            // 高危服务数
            if (searchMap.get("riskservicecount") != null && !"".equals(searchMap.get("riskservicecount"))) {
                predicateList.add(cb.like(root.get("riskservicecount").as(String.class), "%" + (String) searchMap.get("riskservicecount") + "%"));
            }
            // 未下线高危服务数
            if (searchMap.get("riskservicecountonline") != null && !"".equals(searchMap.get("riskservicecountonline"))) {
                predicateList.add(cb.like(root.get("riskservicecountonline").as(String.class), "%" + (String) searchMap.get("riskservicecountonline") + "%"));
            }
            // 高危版本数
            if (searchMap.get("riskversioncount") != null && !"".equals(searchMap.get("riskversioncount"))) {
                predicateList.add(cb.like(root.get("riskversioncount").as(String.class), "%" + (String) searchMap.get("riskversioncount") + "%"));
            }
            // 未下线高危版本数
            if (searchMap.get("riskversioncountonline") != null && !"".equals(searchMap.get("riskversioncountonline"))) {
                predicateList.add(cb.like(root.get("riskversioncountonline").as(String.class), "%" + (String) searchMap.get("riskversioncountonline") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 根据查询ip的端口数
     *
     * @return
     */
    public List<String> findIpPortCount() {
        return statisticsDao.findIpPortCount();
    }

    /**
     * 查询未下线ip的端口数
     *
     * @return
     */
    public List<String> findIpPortCountOnline() {
        return statisticsDao.findIpPortCountOnline();
    }

    /**
     * 查询高危端口数量
     *
     * @return
     */
    public List<String> findRiskPortCount() {
        return statisticsDao.findRiskPortCount();
    }

    /**
     * 查询未下线高危端口数量
     *
     * @return
     */
    public List<String> findRiskPortCountOnline() {
        return statisticsDao.findRiskPortCountOnline();
    }

    /**
     * 查询高危服务数量
     *
     * @return
     */
    public List<String> findRiskServiceCount() {
        return statisticsDao.findRiskServiceCount();
    }

    /**
     * 查询未下线高危服务数量
     *
     * @return
     */
    public List<String> findRiskServiceCountOnline() {
        return statisticsDao.findRiskServiceCountOnline();
    }

    /**
     * 查询高危版本数量
     *
     * @return
     */
    public List<String> findRiskVersionCount() {
        return statisticsDao.findRiskVersionCount();
    }

    /**
     * 查询未下线高危版本数量
     *
     * @return
     */
    public List<String> findRiskVersionCountOnline() {
        return statisticsDao.findRiskVersionCountOnline();
    }

    /**
     * 查询服务数量
     *
     * @return
     */
    public List<String> findServiceCount() {
        return statisticsDao.findServiceCount();
    }

    /**
     * 查询未下线服务数量
     *
     * @return
     */
    public List<String> findServiceCountOnline() {
        return statisticsDao.findServiceCountOnline();
    }

    /**
     * 查询version数量
     *
     * @return
     */
    public List<String> findVersionCount() {
        return statisticsDao.findVersionCount();
    }

    /**
     * 查询未下线version数量
     *
     * @return
     */
    public List<String> findVersionCountOnline() {
        return statisticsDao.findVersionCountOnline();
    }

    /**
     * 查询webinfo中server数量
     *
     * @return
     */
    public List<String> findWebinfoServerCount() {
        return statisticsDao.findWebinfoServerCount();
    }

    /**
     * 查询所有风险数
     *
     * @return
     */
    public List<String> findRiskCount() {
        return statisticsDao.findRiskCount();
    }

    /**
     * 查询未修复风险数
     *
     * @return
     */
    public List<String> findRiskCountOnline() {
        return statisticsDao.findRiskCountOnline();
    }

    /**
     * 查询所有漏洞数
     *
     * @return
     */
    public List<String> findRiskVulnCount() {
        return statisticsDao.findRiskVulnCount();
    }

    /**
     * 查询未修复漏洞数
     *
     * @return
     */
    public List<String> findRiskVulnCountOnline() {
        return statisticsDao.findRiskVulnCountOnline();
    }

    /**
     * 查询ip数
     *
     * @return
     */
    public String findIpCount() {
        return statisticsDao.findIpCount();
    }

    /**
     * 查询未下线ip数
     *
     * @return
     */
    public String findIpCountOnline() {
        return statisticsDao.findIpCountOnline();
    }

    /**
     * 查询端口数
     *
     * @return
     */
    public String findPortCount() {
        return statisticsDao.findPortCount();
    }

    /**
     * 查询未下线端口数
     *
     * @return
     */
    public String findPortCountOnline() {
        return statisticsDao.findPortCountOnline();
    }

    /**
     * 查询检测结果数
     *
     * @return
     */
    public Map<String, String> findCheckresultCountMap() {
        List<String> checkresultCountList = statisticsDao.findCheckresultCount();
        Map<String, String> checkresultCountMap = new HashMap<>();
        for (String checkresultCount : checkresultCountList) {
            String risk = checkresultCount.split(",")[0];
            String count = checkresultCount.split(",")[1];
            checkresultCountMap.put(risk, count);
        }
        return checkresultCountMap;
    }

    /**
     * 查询未修复检测结果数
     *
     * @return
     */
    public Map<String, String> findCheckresultCountOnlineMap() {
        List<String> checkresultCountOnlineList = statisticsDao.findCheckresultCountOnline();
        Map<String, String> checkresultCountMap = new HashMap<>();
        for (String checkresultCount : checkresultCountOnlineList) {
            String risk = checkresultCount.split(",")[0];
            String count = checkresultCount.split(",")[1];
            checkresultCountMap.put(risk, count);
        }
        return checkresultCountMap;
    }


    /**
     * 查询高危端口数
     *
     * @return
     */
    public String findRiskportCount() {
        return statisticsDao.findRiskportCount();
    }

    /**
     * 查询未下线高危端口数
     *
     * @return
     */
    public String findRiskportCountOnline() {
        return statisticsDao.findRiskportCountOnline();
    }

    /**
     * 查询高危服务数
     *
     * @return
     */
    public String findRiskserviceCount() {
        return statisticsDao.findRiskserviceCount();
    }

    /**
     * 查询未下线高危服务数
     *
     * @return
     */
    public String findRiskserviceCountOnline() {
        return statisticsDao.findRiskserviceCountOnline();
    }

    /**
     * 查询高危版本数
     *
     * @return
     */
    public String findRiskversionCount() {
        return statisticsDao.findRiskversionCount();
    }

    /**
     * 查询未下线高危版本数
     *
     * @return
     */
    public String findRiskversionCountOnline() {
        return statisticsDao.findRiskversionCountOnline();
    }

}

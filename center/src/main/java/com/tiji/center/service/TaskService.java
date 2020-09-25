package com.tiji.center.service;

import com.tiji.center.dao.TaskDao;
import com.tiji.center.pojo.Task;
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
 * task服务层
 *
 * @author 贰拾壹
 */
@Service
public class TaskService {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private NmapconfigService nmapconfigService;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Task> findAll() {
        return taskDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Task> findSearch(Map whereMap, int page, int size) {
        Specification<Task> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return taskDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Task> findSearch(Map whereMap) {
        Specification<Task> specification = createSpecification(whereMap);
        return taskDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Task findById(String id) {
        return taskDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param task
     */
    public void add(Task task) {
        if (Objects.isNull(task.getId())) {
            task.setId(idWorker.nextId() + "");
        }
        //新增任务时设置默认值，后续丢到AOP?
        if ((task.getWorktype().equals("mass") || task.getWorktype().equals("mass2Nmap")) && (Objects.isNull(task.getRate()) || task.getRate().isEmpty())) {
            task.setRate("1000");
        }
        if (Objects.isNull(task.getIpslicesize()) || task.getIpslicesize().isEmpty()) {
            if (task.getWorktype().equals("nmap") && !task.getTargetip().equals("unknownPortSerVer") && !task.getTargetip().equals("ipAllPort")) {
                task.setIpslicesize("255");
            }
        }
        if ((Objects.isNull(task.getAdditionoption()) || task.getAdditionoption().isEmpty())) {
            if (task.getWorktype().equals("nmap") || task.getWorktype().equals("nse")) {
                if (task.getTargetip().equals("unknownPortSerVer") || task.getTargetip().equals("ipAllPort")) {
                    // 不能带open..否则扫不到关掉端口
                    task.setAdditionoption("-Pn -n -sV --max-retries=1");
                } else {
                    task.setAdditionoption("-Pn -n -sV --max-retries=1 --open");
                }
            } else {
                task.setAdditionoption("");
            }
        }
        if (Objects.isNull(task.getCrontask())) {
            task.setCrontask(false);
        }
        if (Objects.isNull(task.getDbipisexcludeip())) {
            task.setDbipisexcludeip(false);
        }
        if (Objects.isNull(task.getMerge2asset())) {
            task.setMerge2asset(true);
        }
        if (Objects.isNull(task.getThreadnumber()) || task.getThreadnumber().isEmpty()) {
            task.setThreadnumber("4");
        }
        if (Objects.isNull(task.getSingleipscantime()) || task.getSingleipscantime().isEmpty()) {
            task.setSingleipscantime("1");
        }

        if (task.getWorktype().equals("nmap") && (Objects.isNull(task.getTargetport()) || task.getTargetport().isEmpty())) {
            if ((Objects.isNull(task.getPortslicesize()) || task.getPortslicesize().isEmpty()) && !task.getTargetip().equals("unknownPortSerVer") && !task.getTargetip().equals("ipAllPort")) {
                task.setPortslicesize("1000");
            }

        }


        taskDao.save(task);
    }

    /**
     * 修改
     *
     * @param task
     */
    public void update(Task task) {
        taskDao.save(task);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteById(String id) {

        taskDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        ids.forEach(id -> {
            Task task = findById(id);
            if (task.getWorktype().equals("mass2Nmap")) {
                //根据taskId删除nmap配置
                nmapconfigService.deleteAllByTaskid(id);
            }
        });
        taskDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Task> createSpecification(Map searchMap) {

        return (Specification<Task>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 任务编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 任务父编号
            if (searchMap.get("taskparentid") != null && !"".equals(searchMap.get("taskparentid"))) {
                predicateList.add(cb.like(root.get("taskparentid").as(String.class), "%" + searchMap.get("taskparentid") + "%"));
            }
            // 项目编号
            if (searchMap.get("projectid") != null && !"".equals(searchMap.get("projectid"))) {
                predicateList.add(cb.like(root.get("projectid").as(String.class), "%" + searchMap.get("projectid") + "%"));
            }
            // 任务名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
            }
            // 任务描述
            if (searchMap.get("description") != null && !"".equals(searchMap.get("description"))) {
                predicateList.add(cb.like(root.get("description").as(String.class), "%" + searchMap.get("description") + "%"));
            }
            // cron表达式
            if (searchMap.get("cronexpression") != null && !"".equals(searchMap.get("cronexpression"))) {
                predicateList.add(cb.like(root.get("cronexpression").as(String.class), "%" + searchMap.get("cronexpression") + "%"));
            }
            //cron任务
            if (searchMap.get("crontask") != null && !"".equals(searchMap.get("crontask"))) {
                predicateList.add(cb.equal(root.get("crontask").as(Boolean.class), (searchMap.get("crontask"))));
            }
            //任务开始时间
            if (searchMap.get("starttime") != null && !"".equals(searchMap.get("starttime"))) {
                List<String> starttimeList = (List<String>) searchMap.get("starttime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("starttime").as(String.class), starttimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("starttime").as(String.class), starttimeList.get(1)));
            }
            //任务结束时间
            if (searchMap.get("endtime") != null && !"".equals(searchMap.get("endtime"))) {
                List<String> endtimeList = (List<String>) searchMap.get("endtime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("endtime").as(String.class), endtimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("endtime").as(String.class), endtimeList.get(1)));
            }
            // 任务类型
            if (searchMap.get("worktype") != null && !"".equals(searchMap.get("worktype"))) {
                predicateList.add(cb.like(root.get("worktype").as(String.class), "%" + searchMap.get("worktype") + "%"));
            }
            // 检测类型
            if (searchMap.get("checktype") != null && !"".equals(searchMap.get("checktype"))) {
                predicateList.add(cb.like(root.get("checktype").as(String.class), "%" + searchMap.get("checktype") + "%"));
            }
            // 线程数量
            if (searchMap.get("threadnumber") != null && !"".equals(searchMap.get("threadnumber"))) {
                predicateList.add(cb.like(root.get("threadnumber").as(String.class), "%" + searchMap.get("threadnumber") + "%"));
            }
            // 单个ip扫描次数
            if (searchMap.get("singleipscantime") != null && !"".equals(searchMap.get("singleipscantime"))) {
                predicateList.add(cb.like(root.get("singleipscantime").as(String.class), "%" + searchMap.get("singleipscantime") + "%"));
            }
            // 任务附加选项
            if (searchMap.get("additionoption") != null && !"".equals(searchMap.get("additionoption"))) {
                predicateList.add(cb.like(root.get("additionoption").as(String.class), "%" + searchMap.get("additionoption") + "%"));
            }
            // 扫描速率
            if (searchMap.get("rate") != null && !"".equals(searchMap.get("rate"))) {
                predicateList.add(cb.like(root.get("rate").as(String.class), "%" + searchMap.get("rate") + "%"));
            }
            // 目标ip
            if (searchMap.get("targetip") != null && !"".equals(searchMap.get("targetip"))) {
                predicateList.add(cb.like(root.get("targetip").as(String.class), "%" + searchMap.get("targetip") + "%"));
            }
            // 目标端口，为空为所有端口，regular为nmap默认端口，端口格式:80,443
            if (searchMap.get("targetport") != null && !"".equals(searchMap.get("targetport"))) {
                predicateList.add(cb.like(root.get("targetport").as(String.class), "%" + searchMap.get("targetport") + "%"));
            }
            // 排除ip
            if (searchMap.get("excludeip") != null && !"".equals(searchMap.get("excludeip"))) {
                predicateList.add(cb.like(root.get("excludeip").as(String.class), "%" + searchMap.get("excludeip") + "%"));
            }
            // 分组大小
            if (searchMap.get("ipslicesize") != null && !"".equals(searchMap.get("ipslicesize"))) {
                predicateList.add(cb.like(root.get("ipslicesize").as(String.class), "%" + searchMap.get("ipslicesize") + "%"));
            }
            // 端口分组大小，nmap全端口模式时，如果该字段有值，则进行端口分组，分组大小范围：1000-10000
            if (searchMap.get("portslicesize") != null && !"".equals(searchMap.get("portslicesize"))) {
                predicateList.add(cb.like(root.get("portslicesize").as(String.class), "%" + searchMap.get("portslicesize") + "%"));
            }
            //db中ip作为排除ip
            if (searchMap.get("dbipisexcludeip") != null && !"".equals(searchMap.get("dbipisexcludeip"))) {
                predicateList.add(cb.equal(root.get("dbipisexcludeip").as(Boolean.class), (searchMap.get("dbipisexcludeip"))));
            }
            //扫描结果合并到资产
            if (searchMap.get("merge2asset") != null && !"".equals(searchMap.get("merge2asset"))) {
                predicateList.add(cb.equal(root.get("merge2asset").as(Boolean.class), (searchMap.get("merge2asset"))));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 查询starttime不为空的task
     *
     * @return
     */
    public List<Task> findAllByEndtimeIsNullAndStarttimeIsNotNull() {
        return taskDao.findAllByEndtimeIsNullAndStarttimeIsNotNull();
    }


    /**
     * 根据worktype查询starttime endtime不为空的task
     *
     * @return
     */
    public List<Task> findAllByWorktypeAndEndtimeIsNotNullAndStarttimeIsNotNull(String worktype) {
        return taskDao.findAllByWorktypeAndEndtimeIsNotNullAndStarttimeIsNotNull(worktype);
    }

    /**
     * 查询所有crontask
     *
     * @return
     */
    public List<Task> findAllByCrontaskIsTrueAndTaskparentidIsNull() {
        return taskDao.findAllByCrontaskIsTrueAndTaskparentidIsNull();
    }

    /**
     * 根据taskParentId查询实体
     *
     * @param taskParentId
     * @return
     */
    public List<Task> findAllByTaskparentid(String taskParentId) {
        return taskDao.findAllByTaskparentid(taskParentId);
    }

    /**
     * 根据name查询实体
     *
     * @param name
     * @return
     */
    public Task findByName(String name) {
        return taskDao.findByName(name);
    }
}

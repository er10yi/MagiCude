package com.tiji.center.service;

import com.tiji.center.dao.NotifylogDao;
import com.tiji.center.pojo.Notifylog;
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
 * notifylog服务层
 *
 * @author 贰拾壹
 */
@Service
public class NotifylogService {

    @Autowired
    private NotifylogDao notifylogDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Notifylog> findAll() {
        return notifylogDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Notifylog> findSearch(Map whereMap, int page, int size) {
        Specification<Notifylog> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return notifylogDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Notifylog> findSearch(Map whereMap) {
        Specification<Notifylog> specification = createSpecification(whereMap);
        return notifylogDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Notifylog findById(String id) {
        return notifylogDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param notifylog
     */
    public void add(Notifylog notifylog) {
        if (Objects.isNull(notifylog.getId())) {
            notifylog.setId(idWorker.nextId() + "");
        }
        notifylogDao.save(notifylog);
    }

    /**
     * 修改
     *
     * @param notifylog
     */
    public void update(Notifylog notifylog) {
        notifylogDao.save(notifylog);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        notifylogDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        notifylogDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Notifylog> createSpecification(Map searchMap) {

        return (Specification<Notifylog>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 类型
            if (searchMap.get("type") != null && !"".equals(searchMap.get("type"))) {
                predicateList.add(cb.like(root.get("type").as(String.class), "%" + (String) searchMap.get("type") + "%"));
            }
            // 接收人
            if (searchMap.get("recipient") != null && !"".equals(searchMap.get("recipient"))) {
                predicateList.add(cb.like(root.get("recipient").as(String.class), "%" + (String) searchMap.get("recipient") + "%"));
            }
            // 接收账户
            if (searchMap.get("receiveuser") != null && !"".equals(searchMap.get("receiveuser"))) {
                predicateList.add(cb.like(root.get("receiveuser").as(String.class), "%" + (String) searchMap.get("receiveuser") + "%"));
            }
            // 内容
            if (searchMap.get("content") != null && !"".equals(searchMap.get("content"))) {
                predicateList.add(cb.like(root.get("content").as(String.class), "%" + (String) searchMap.get("content") + "%"));
            }
            //发送成功
            if (searchMap.get("success") != null && !"".equals(searchMap.get("success"))) {
                predicateList.add(cb.equal(root.get("success").as(Boolean.class), (searchMap.get("success"))));
            }
            // 异常消息
            if (searchMap.get("exception") != null && !"".equals(searchMap.get("exception"))) {
                predicateList.add(cb.like(root.get("exception").as(String.class), "%" + (String) searchMap.get("exception") + "%"));
            }
            //发送时间
            if (searchMap.get("sendtime") != null && !"".equals(searchMap.get("sendtime"))) {
                List<String> sendtimeList = (List<String>) searchMap.get("sendtime");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("sendtime").as(String.class), sendtimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("sendtime").as(String.class), sendtimeList.get(1)));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 批量增加
     *
     * @param notifylogList
     */
    public void batchAdd(List<Notifylog> notifylogList) {
        notifylogDao.saveAll(notifylogList);
    }

}

package com.tiji.center.service;

import com.tiji.center.dao.ImvulnnotifyDao;
import com.tiji.center.pojo.Imvulnnotify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * imvulnnotify服务层
 *
 * @author 贰拾壹
 */
@Service
public class ImvulnnotifyService {

    @Autowired
    private ImvulnnotifyDao imvulnnotifyDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Imvulnnotify> findAll() {
        return imvulnnotifyDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Imvulnnotify> findSearch(Map whereMap, int page, int size) {
        Specification<Imvulnnotify> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return imvulnnotifyDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Imvulnnotify> findSearch(Map whereMap) {
        Specification<Imvulnnotify> specification = createSpecification(whereMap);
        return imvulnnotifyDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Imvulnnotify findById(String id) {
        return imvulnnotifyDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param imvulnnotify
     */
    public void add(Imvulnnotify imvulnnotify) {
        if (Objects.isNull(imvulnnotify.getId())) {
            imvulnnotify.setId(idWorker.nextId() + "");
        }
        imvulnnotifyDao.save(imvulnnotify);
    }

    /**
     * 修改
     *
     * @param imvulnnotify
     */
    public void update(Imvulnnotify imvulnnotify) {
        imvulnnotifyDao.save(imvulnnotify);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        imvulnnotifyDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Imvulnnotify> createSpecification(Map searchMap) {

        return (Specification<Imvulnnotify>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 秘钥
            if (searchMap.get("secret") != null && !"".equals(searchMap.get("secret"))) {
                predicateList.add(cb.like(root.get("secret").as(String.class), "%" + (String) searchMap.get("secret") + "%"));
            }
            // 风险等级
            if (searchMap.get("risk") != null && !"".equals(searchMap.get("risk"))) {
                predicateList.add(cb.like(root.get("risk").as(String.class), "%" + (String) searchMap.get("risk") + "%"));
            }
            // 接收人列表
            if (searchMap.get("receiver") != null && !"".equals(searchMap.get("receiver"))) {
                predicateList.add(cb.like(root.get("receiver").as(String.class), "%" + (String) searchMap.get("receiver") + "%"));
            }
            // 消息地址
            if (searchMap.get("messageurl") != null && !"".equals(searchMap.get("messageurl"))) {
                predicateList.add(cb.like(root.get("messageurl").as(String.class), "%" + (String) searchMap.get("messageurl") + "%"));
            }
            // 消息标题
            if (searchMap.get("messagetitle") != null && !"".equals(searchMap.get("messagetitle"))) {
                predicateList.add(cb.like(root.get("messagetitle").as(String.class), "%" + (String) searchMap.get("messagetitle") + "%"));
            }
            // 消息前缀
            if (searchMap.get("messageprefix") != null && !"".equals(searchMap.get("messageprefix"))) {
                predicateList.add(cb.like(root.get("messageprefix").as(String.class), "%" + (String) searchMap.get("messageprefix") + "%"));
            }
            // 消息后缀
            if (searchMap.get("messagesuffix") != null && !"".equals(searchMap.get("messagesuffix"))) {
                predicateList.add(cb.like(root.get("messagesuffix").as(String.class), "%" + (String) searchMap.get("messagesuffix") + "%"));
            }
            // 消息编码
            if (searchMap.get("messagecharset") != null && !"".equals(searchMap.get("messagecharset"))) {
                predicateList.add(cb.like(root.get("messagecharset").as(String.class), "%" + (String) searchMap.get("messagecharset") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


}

package com.tiji.center.service;

import com.tiji.center.dao.SendmailconfigDao;
import com.tiji.center.pojo.Sendmailconfig;
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
 * sendmailconfig服务层
 *
 * @author 贰拾壹
 */
@Service
public class SendmailconfigService {

    @Autowired
    private SendmailconfigDao sendmailconfigDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Sendmailconfig> findAll() {
        return sendmailconfigDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Sendmailconfig> findSearch(Map whereMap, int page, int size) {
        Specification<Sendmailconfig> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return sendmailconfigDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Sendmailconfig> findSearch(Map whereMap) {
        Specification<Sendmailconfig> specification = createSpecification(whereMap);
        return sendmailconfigDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Sendmailconfig findById(String id) {
        return sendmailconfigDao.findById(id).orElse(null);
    }

    /**
     * 增加
     *
     * @param sendmailconfig
     */
    public void add(Sendmailconfig sendmailconfig) {
        if (Objects.isNull(sendmailconfig.getId())) {
            sendmailconfig.setId(idWorker.nextId() + "");
        }
        sendmailconfigDao.save(sendmailconfig);
    }

    /**
     * 修改
     *
     * @param sendmailconfig
     */
    public void update(Sendmailconfig sendmailconfig) {
        sendmailconfigDao.save(sendmailconfig);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        sendmailconfigDao.deleteById(id);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Sendmailconfig> createSpecification(Map searchMap) {

        return (Specification<Sendmailconfig>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 邮箱host
            if (searchMap.get("sendhost") != null && !"".equals(searchMap.get("sendhost"))) {
                predicateList.add(cb.like(root.get("sendhost").as(String.class), "%" + (String) searchMap.get("sendhost") + "%"));
            }
            // 密码
            if (searchMap.get("sendpassword") != null && !"".equals(searchMap.get("sendpassword"))) {
                predicateList.add(cb.like(root.get("sendpassword").as(String.class), "%" + (String) searchMap.get("sendpassword") + "%"));
            }

            // 发件人
            if (searchMap.get("sendfrom") != null && !"".equals(searchMap.get("sendfrom"))) {
                predicateList.add(cb.like(root.get("sendfrom").as(String.class), "%" + (String) searchMap.get("sendfrom") + "%"));
            }
            //提醒邮箱，强制提醒，不管是否在提醒白名单里，提醒包括所有资产和在收件人列表接收漏洞风险中的漏洞
            if (searchMap.get("sendto") != null && !"".equals(searchMap.get("sendto"))) {
                predicateList.add(cb.like(root.get("sendto").as(String.class), "%" + (String) searchMap.get("sendto") + "%"));
            }
            //收件人列表接收漏洞风险
            if (searchMap.get("sendtorisk") != null && !"".equals(searchMap.get("sendtorisk"))) {
                predicateList.add(cb.like(root.get("sendtorisk").as(String.class), "%" + (String) searchMap.get("sendtorisk") + "%"));
            }
            // 漏洞邮件主题
            if (searchMap.get("vulnsubject") != null && !"".equals(searchMap.get("vulnsubject"))) {
                predicateList.add(cb.like(root.get("vulnsubject").as(String.class), "%" + (String) searchMap.get("vulnsubject") + "%"));
            }
            // 资产邮件主题
            if (searchMap.get("assetsubject") != null && !"".equals(searchMap.get("assetsubject"))) {
                predicateList.add(cb.like(root.get("assetsubject").as(String.class), "%" + (String) searchMap.get("assetsubject") + "%"));
            }
            // 漏洞邮件内容
            if (searchMap.get("vulncontent") != null && !"".equals(searchMap.get("vulncontent"))) {
                predicateList.add(cb.like(root.get("vulncontent").as(String.class), "%" + (String) searchMap.get("vulncontent") + "%"));
            }
            // 资产邮件内容
            if (searchMap.get("assetcontent") != null && !"".equals(searchMap.get("assetcontent"))) {
                predicateList.add(cb.like(root.get("assetcontent").as(String.class), "%" + (String) searchMap.get("assetcontent") + "%"));
            }
            // excel作者
            if (searchMap.get("excelauthor") != null && !"".equals(searchMap.get("excelauthor"))) {
                predicateList.add(cb.like(root.get("excelauthor").as(String.class), "%" + (String) searchMap.get("excelauthor") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

}

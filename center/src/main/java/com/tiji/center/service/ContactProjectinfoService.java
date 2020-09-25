package com.tiji.center.service;

import com.tiji.center.dao.ContactProjectinfoDao;
import com.tiji.center.pojo.ContactProjectinfo;
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
 * contactProjectinfo服务层
 *
 * @author 贰拾壹
 */
@Service
public class ContactProjectinfoService {

    @Autowired
    private ContactProjectinfoDao contactProjectinfoDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<ContactProjectinfo> findAll() {
        return contactProjectinfoDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<ContactProjectinfo> findSearch(Map whereMap, int page, int size) {
        Specification<ContactProjectinfo> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return contactProjectinfoDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<ContactProjectinfo> findSearch(Map whereMap) {
        Specification<ContactProjectinfo> specification = createSpecification(whereMap);
        return contactProjectinfoDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public ContactProjectinfo findById(String id) {
        return contactProjectinfoDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param contactProjectinfo
     */
    public void add(ContactProjectinfo contactProjectinfo) {
        if (Objects.isNull(contactProjectinfo.getId())) {
            contactProjectinfo.setId(idWorker.nextId() + "");
        }
        contactProjectinfoDao.save(contactProjectinfo);
    }

    /**
     * 修改
     *
     * @param contactProjectinfo
     */
    public void update(ContactProjectinfo contactProjectinfo) {
        contactProjectinfoDao.save(contactProjectinfo);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        contactProjectinfoDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        contactProjectinfoDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<ContactProjectinfo> createSpecification(Map searchMap) {

        return (Specification<ContactProjectinfo>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + (String) searchMap.get("id") + "%"));
            }
            // 联系编号
            if (searchMap.get("contactid") != null && !"".equals(searchMap.get("contactid"))) {
                predicateList.add(cb.equal(root.get("contactid").as(String.class), searchMap.get("contactid")));
            }
            // 项目信息编号
            if (searchMap.get("projectinfoid") != null && !"".equals(searchMap.get("projectinfoid"))) {
                predicateList.add(cb.equal(root.get("projectinfoid").as(String.class), searchMap.get("projectinfoid")));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }

    /**
     * 删除
     *
     * @param contactid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByContactid(String contactid) {
        contactProjectinfoDao.deleteAllByContactid(contactid);
    }

    /**
     * 删除
     *
     * @param projectinfoid
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByProjectinfoid(String projectinfoid) {
        contactProjectinfoDao.deleteAllByProjectinfoid(projectinfoid);
    }

    /**
     * 根据projectinfoid查询
     *
     * @param projectinfoid
     * @return
     */
    public List<ContactProjectinfo> findAllByProjectinfoid(String projectinfoid) {
        return contactProjectinfoDao.findAllByProjectinfoid(projectinfoid);
    }

    /**
     * 根据contactid、projectinfoid查询
     *
     * @param contactid
     * @param projectinfoid
     * @return
     */
    public ContactProjectinfo findByContactidAndProjectinfoid(String contactid, String projectinfoid) {
        return contactProjectinfoDao.findByContactidAndProjectinfoid(contactid, projectinfoid);
    }
}

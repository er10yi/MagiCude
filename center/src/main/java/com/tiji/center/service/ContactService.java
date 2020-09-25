package com.tiji.center.service;

import com.tiji.center.dao.ContactDao;
import com.tiji.center.pojo.Contact;
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
 * contact服务层
 *
 * @author 贰拾壹
 */
@Service
public class ContactService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Contact> findAll() {
        return contactDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<Contact> findSearch(Map whereMap, int page, int size) {
        Specification<Contact> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return contactDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<Contact> findSearch(Map whereMap) {
        Specification<Contact> specification = createSpecification(whereMap);
        return contactDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Contact findById(String id) {
        return contactDao.findById(id).get();
    }

    /**
     * 增加
     *
     * @param contact
     */
    public void add(Contact contact) {
        if (Objects.isNull(contact.getId())) {
            contact.setId(idWorker.nextId() + "");
        }
        contactDao.save(contact);
    }

    /**
     * 修改
     *
     * @param contact
     */
    public void update(Contact contact) {
        contactDao.save(contact);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        contactDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        contactDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<Contact> createSpecification(Map searchMap) {

        return (Specification<Contact>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 联系人
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                predicateList.add(cb.like(root.get("name").as(String.class), "%" + searchMap.get("name") + "%"));
            }
            // 邮箱
            if (searchMap.get("email") != null && !"".equals(searchMap.get("email"))) {
                predicateList.add(cb.like(root.get("email").as(String.class), "%" + searchMap.get("email") + "%"));
            }
            // 电话，座机或手机
            if (searchMap.get("phone") != null && !"".equals(searchMap.get("phone"))) {
                predicateList.add(cb.like(root.get("phone").as(String.class), "%" + searchMap.get("phone") + "%"));
            }

            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 根据name和email查询实体
     *
     * @param name
     * @param email
     * @return
     */
    public Contact findByNameAndEmail(String name, String email) {
        return contactDao.findByNameAndEmail(name, email);
    }


}

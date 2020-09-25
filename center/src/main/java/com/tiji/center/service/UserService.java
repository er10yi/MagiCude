package com.tiji.center.service;

import com.tiji.center.dao.UserDao;
import com.tiji.center.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * user服务层
 *
 * @author 贰拾壹
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<User> findAll() {
        return userDao.findAll();
    }


    /**
     * 条件查询+分页
     *
     * @param whereMap
     * @param page
     * @param size
     * @return
     */
    public Page<User> findSearch(Map whereMap, int page, int size) {
        Specification<User> specification = createSpecification(whereMap);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return userDao.findAll(specification, pageRequest);
    }


    /**
     * 条件查询
     *
     * @param whereMap
     * @return
     */
    public List<User> findSearch(Map whereMap) {
        Specification<User> specification = createSpecification(whereMap);
        return userDao.findAll(specification);
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public User findById(String id) {
        return userDao.findById(id).isPresent() ? userDao.findById(id).get() : null;
    }


    /**
     * 增加
     *
     * @param user
     */
    public void add(User user) {
        if (Objects.isNull(user.getId())) {
            user.setId(idWorker.nextId() + "");
        }
        if (Objects.isNull(user.getAdmin())) {
            user.setAdmin(false);
        }
        if (Objects.isNull(user.getActive())) {
            user.setActive(false);
        }
        String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        userDao.save(user);
    }

    /**
     * 修改
     *
     * @param user
     */
    public void update(User user) {
        userDao.save(user);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void deleteById(String id) {
        userDao.deleteById(id);
    }

    /**
     * 根据id数组删除
     *
     * @param ids
     */
    @Transactional(value = "masterTransactionManager")
    public void deleteAllByIds(List<String> ids) {
        userDao.deleteAllByIds(ids);
    }

    /**
     * 动态条件构建
     *
     * @param searchMap
     * @return
     */
    private Specification<User> createSpecification(Map searchMap) {

        return (Specification<User>) (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<Predicate>();
            // 用户编号
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                predicateList.add(cb.like(root.get("id").as(String.class), "%" + searchMap.get("id") + "%"));
            }
            // 用户名
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                predicateList.add(cb.like(root.get("username").as(String.class), "%" + searchMap.get("username") + "%"));
            }
            //// 密码
            //if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
            //    predicateList.add(cb.like(root.get("password").as(String.class), "%" + searchMap.get("password") + "%"));
            //}
            // 头像地址
            if (searchMap.get("avatar") != null && !"".equals(searchMap.get("avatar"))) {
                predicateList.add(cb.like(root.get("avatar").as(String.class), "%" + searchMap.get("avatar") + "%"));
            }
            //最后登录时间
            if (searchMap.get("lastdate") != null && !"".equals(searchMap.get("lastdate"))) {
                List<String> activetimeList = (List<String>) searchMap.get("lastdate");
                //开始
                predicateList.add(cb.greaterThanOrEqualTo(root.get("lastdate").as(String.class), activetimeList.get(0)));
                //结束
                predicateList.add(cb.lessThanOrEqualTo(root.get("lastdate").as(String.class), activetimeList.get(1)));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));

        };

    }


    /**
     * 登录
     *
     * @param user
     * @return
     */
    public User login(User user) {
        User loginUser = userDao.findByUsername(user.getUsername());
        if (loginUser != null && bCryptPasswordEncoder.matches(user.getPassword(), loginUser.getPassword())) {
            return loginUser;
        } else {
            return null;
        }
    }


    /**
     * 根据username查询
     *
     * @return
     */
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

}

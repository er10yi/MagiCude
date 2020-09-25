package com.tiji.center.controller;

import com.tiji.center.pojo.User;
import com.tiji.center.service.DictionarypasswordService;
import com.tiji.center.service.UserService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * user控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private DictionarypasswordService dictionarypasswordService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", userService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<User> pageList = userService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<User>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", userService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param user
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody User user) {
        String password = user.getPassword();
        if (Objects.isNull(password) || password.isEmpty() || password.length() < 8) {
            return new Result(false, StatusCode.ERROR, "密码为空或长度少于8位");
        }
        List<String> allPassword = dictionarypasswordService.findAllPassword();
        if (allPassword.contains(password)) {
            return new Result(false, StatusCode.ERROR, "密码在密码字典中，请重新设置");
        }

        userService.add(user);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody User user, @PathVariable String id) {
        user.setId(id);
        String oldPassword = user.getPassword();
        if (Objects.isNull(oldPassword) || oldPassword.isEmpty() || oldPassword.length() < 8) {
            return new Result(false, StatusCode.ERROR, "密码为空或长度少于8位");
        }
        if (oldPassword.startsWith("$2a$10$")) {
            User user1 = userService.findById(id);
            user.setPassword(user1.getPassword());
        } else {
            //非BCrypt加密的，改密码
            List<String> allPassword = dictionarypasswordService.findAllPassword();
            if (allPassword.contains(oldPassword)) {
                return new Result(false, StatusCode.ERROR, "密码在密码字典中，请重新设置");
            }
            String newPassword = bCryptPasswordEncoder.encode(oldPassword);
            user.setPassword(newPassword);
        }

        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        userService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        userService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

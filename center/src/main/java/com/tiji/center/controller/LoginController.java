package com.tiji.center.controller;

import com.tiji.center.pojo.User;
import com.tiji.center.service.DictionarypasswordService;
import com.tiji.center.service.UserService;
import com.tiji.center.util.JwtUtil;
import entity.Result;
import entity.StatusCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author 贰拾壹
 * @create 2020-01-08 11:33
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private DictionarypasswordService dictionarypasswordService;

    /**
     * 登录
     * 返回token
     */
    /*
     * {
     * "code": 20000,
     * "data": {
     * "token": "admin"
     * }
     * }
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody User user) {
        //认证
        User loginUser = userService.login(user);

        if (loginUser == null) {
            //登录失败
            return new Result(false, StatusCode.LOGIN_ERROR, "登录失败：用户名或密码错误");
        } else {
            //登录成功
            //判断是否有效
            Boolean active = loginUser.getActive();
            if (Objects.isNull(active) || !active) {
                return new Result(false, StatusCode.LOGIN_ERROR, "登录失败：用户已失效");
            }
            //判断是否首次登陆
            Date lastdate = loginUser.getLastdate();

            if (!Objects.isNull(lastdate)) {
                List<String> allPassword = dictionarypasswordService.findAllPassword();
                //非首次登陆，密码在密码字典中
                if (allPassword.contains(user.getPassword())) {
                    return new Result(false, StatusCode.LOGIN_ERROR, "登录失败：当前密码在密码字典中且首次登录后未修改密码，请将数据库user表当前用户的lastdate置空，再重新登录");
                }
            }

            //授权
            String token;
            Boolean admin = loginUser.getAdmin();
            if (!Objects.isNull(admin) && admin) {
                //管理员
                token = jwtUtil.createJWT(loginUser.getId(), loginUser.getUsername(), "admin");
            } else {
                //普通用户
                token = jwtUtil.createJWT(loginUser.getId(), loginUser.getUsername(), "user");
            }

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            //记录登录日期
            loginUser.setLastdate(new Date());
            userService.update(loginUser);
            return new Result(true, StatusCode.OK, "登录成功", data);
        }
    }

    /**
     * 根据token返回用户信息
     *
     * @return
     */
    /*
     * {
     * "code": 20000,
     * "data": {
     * "name": "admin",
     * "avatar": ""
     * }
     * }
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public Result getInfo() {
        String token = request.getParameter("token");
        try {
            Claims body;
            body = jwtUtil.parseJWT(token);
            //Token合法
            Object roles = body.get("roles");
            Object userId = body.get("jti");
            Object userName = body.get("sub");
            User user = userService.findById((String) userId);
            Map<String, Object> data = new HashMap<>();
            data.put("name", user.getUsername());
            data.put("avatar", user.getAvatar());
            return new Result(true, StatusCode.OK, "", data);
        } catch (ExpiredJwtException e) {
            return new Result(true, StatusCode.TOKEN_EXPIRED_ERROR, "token已过期");
        } catch (JwtException e) {
            return new Result(true, StatusCode.ILLEGAL_TOKEN_ERROR, "非法token");
        }

    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result logout() {
        String authInRequestHeader = request.getHeader("Authorization");
        try {
            Claims body;
            String token = authInRequestHeader.substring(7);
            body = jwtUtil.parseJWT(token);
            //Token合法
            //TODO 以下回到session时代
            //当前toten加到黑名单
            //然后Request的时候要判断token是否在黑名单...有点麻烦..先不搞
            return new Result(true, StatusCode.OK, "成功退出");
        } catch (JwtException e) {
            //直接退出
            return new Result(true, StatusCode.OK, "成功退出");
        }
    }
}

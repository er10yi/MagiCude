package com.tiji.center.aspect;

import com.tiji.center.exception.AssessDeniedException;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 贰拾壹
 * @create 2020-05-09 11:12
 */
@Aspect
@Component
public class AssessAspect {
    @Autowired
    private HttpServletRequest request;

    //用户管理需要管理员权限
    @Pointcut("execution(public * com.tiji.center.controller.UserController.*(..))")
    public void controllerUser() {
    }

    @Before("controllerUser()")
    public void beforeUserController() {
        Claims claims = (Claims) request.getAttribute("admin_claims");
        if (claims == null) {
            //没有权限
            throw new AssessDeniedException("无权访问");
        }
    }
}

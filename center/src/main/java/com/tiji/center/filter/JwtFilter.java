package com.tiji.center.filter;


import com.tiji.center.exception.ExpiredTokenException;
import com.tiji.center.exception.IllegalTokenException;
import com.tiji.center.exception.LoginException;
import com.tiji.center.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JWT权限拦截器
 *
 * @author 贰拾壹
 * @create 2019-09-05 15:27
 */
@Component
public class JwtFilter extends HandlerInterceptorAdapter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 进行Controller的方法之前执行
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String method = request.getMethod();
        if (method.equals("OPTIONS")) {
            return true;
        }
        String authInRequestHeader = request.getHeader("Authorization");
        if (!Objects.isNull(authInRequestHeader) && authInRequestHeader.startsWith("Bearer") && authInRequestHeader.length() > 6) {
            String token = authInRequestHeader.substring(7);

            //解析Token字符串是否合法（是否过期）
            Claims body;
            try {
                body = jwtUtil.parseJWT(token);
            } catch (ExpiredJwtException e) {
                throw new ExpiredTokenException("token已过期");
            } catch (JwtException e) {
                throw new IllegalTokenException("非法token");
            }

            //Token合法
            Object roles = body.get("roles");
            Object userId = body.get("jti");
            Object userName = body.get("sub");
            //System.out.println("auth: " + authInRequestHeader);
            //System.out.println("body: " + body);
            //System.out.println("roles: " + roles);
            //System.out.println("userId: " + userId);
            //System.out.println("userName: " + userName);
            //标记roles
            if ("admin".equals(roles)) {
                request.setAttribute("admin_claims", body);
            }
            if ("user".equals(roles)) {
                request.setAttribute("user_claims", body);
            }
        } else {
            //未登录，跳转到登录页面
            //response.sendRedirect(request.getContextPath()+"user/login");
            //response.getWriter().write("login");
            throw new LoginException("未登录");
            //return false;
        }
        //放行
        return true;
    }

    @Override
    //记录日志
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        super.afterCompletion(request, response, handler, ex);
    }
}


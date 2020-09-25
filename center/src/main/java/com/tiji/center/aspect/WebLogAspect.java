package com.tiji.center.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author 贰拾壹
 * @create 2019-10-11 10:29
 */
@Aspect
@Component
public class WebLogAspect {
    private final static Logger logger = LoggerFactory.getLogger(WebLogAspect.class);

    //controller日志记录
    @Pointcut("execution(public * com.tiji.center.controller.*.*(..))")//切入点描述 这个是controller包的切入点
    public void controllerLog() {
    }

    @Before("controllerLog()") //在切入点的方法run之前要干的
    public void logBeforeController(JoinPoint joinPoint) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//这个RequestContextHolder是Springmvc提供来获得请求的东西
        assert requestAttributes != null;
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        logger.info(
                request.getRemoteAddr() + " " +
                        request.getMethod() + " " +
                        request.getRequestURI() + " " +
                        request.getHeader("User-Agent") + " " +
                        request.getHeader("Referer") + " " +
                        Arrays.toString(joinPoint.getArgs()) + " " +
                        joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()
        );
    }
}

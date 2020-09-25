package com.tiji.center.controller;

import entity.Result;
import entity.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import util.ExcpUtil;

import java.util.Objects;

/**
 * 统一异常处理类
 *
 * @author 贰拾壹
 */
@ControllerAdvice
public class BaseExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(BaseExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        if (!Objects.isNull(e)) {
            logger.error(ExcpUtil.buildErrorMessage(e));
            //e.printStackTrace();

            if (e.toString().contains("ObjectAlreadyExistsException")) {
                return new Result(false, StatusCode.REMOTE_ERROR, "cron任务已存在");
            }
            if ("无权访问".equals(e.getMessage())) {
                return new Result(false, StatusCode.ACCESS_ERROR, "无权访问");
            }
            if ("token已过期".equals(e.getMessage())) {
                return new Result(false, StatusCode.TOKEN_EXPIRED_ERROR, "token已过期");
            }
            if ("非法token".equals(e.getMessage())) {
                return new Result(false, StatusCode.ILLEGAL_TOKEN_ERROR, "非法token");
            }
            if ("未登录".equals(e.getMessage())) {
                return new Result(false, StatusCode.ACCESS_ERROR, "未登录");
            }
        }
        return new Result(false, StatusCode.ERROR, "执行出错");
    }
}

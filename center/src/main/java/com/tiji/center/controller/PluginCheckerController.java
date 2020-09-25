package com.tiji.center.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * PluginCheckerController
 *
 * @author 贰拾壹
 * @create 2020-08-08 18:29
 */

@RestController
@CrossOrigin
@RequestMapping("/pluginchecker")

public class PluginCheckerController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 插件HTTP辅助验证
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result check() {
        //从redis获取key，并返回
        String httpValidateKey = redisTemplate.opsForValue().get("HttpValidateKey_");
        return new Result(true, StatusCode.OK, "存在漏洞, vulnerable", httpValidateKey);
    }
}

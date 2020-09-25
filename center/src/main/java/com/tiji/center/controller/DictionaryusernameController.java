package com.tiji.center.controller;

import com.tiji.center.pojo.Dictionaryusername;
import com.tiji.center.service.DictionaryusernameService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * dictionaryusername控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/dictionaryusername")
public class DictionaryusernameController {

    @Autowired
    private DictionaryusernameService dictionaryusernameService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", dictionaryusernameService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", dictionaryusernameService.findById(id));
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
        Page<Dictionaryusername> pageList = dictionaryusernameService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Dictionaryusername>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", dictionaryusernameService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param dictionaryusername
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Dictionaryusername dictionaryusername) {

        String username = dictionaryusername.getUsername();
        Dictionaryusername dictionaryusernameInDb = dictionaryusernameService.findByUsername(username);
        if (Objects.isNull(dictionaryusernameInDb)) {
            dictionaryusernameService.add(dictionaryusername);
            //更新redis缓存
            freshDictUsernameRedisCache();
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "字典用户名重复");
        }
    }

    /**
     * 修改
     *
     * @param dictionaryusername
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Dictionaryusername dictionaryusername, @PathVariable String id) {
        dictionaryusername.setId(id);
        dictionaryusernameService.update(dictionaryusername);
        //更新redis缓存
        freshDictUsernameRedisCache();
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        dictionaryusernameService.deleteById(id);
        //更新redis缓存
        freshDictUsernameRedisCache();
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        dictionaryusernameService.deleteAllByIds(ids);
        //更新redis缓存
        freshDictUsernameRedisCache();
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入字典
     */
    @RequestMapping(value = "/batchAdd", method = RequestMethod.POST)
    public Result batchAddDictionaryUsername(@RequestParam("file") MultipartFile file) {
        if (Objects.isNull(file) || file.getSize() == 0) {
            return new Result(false, StatusCode.ERROR, "文件为空");
        }
        long fileSize = file.getSize();
        if (fileSize / 1024 / 1024 > 3) {
            return new Result(false, StatusCode.ERROR, "文件大小不能超过 3M");
        }
        String fileContentType = file.getContentType();
        assert fileContentType != null;
        if (!"text/plain".equals(fileContentType)) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }
        String fileOriginalFilename = file.getOriginalFilename();
        assert fileOriginalFilename != null;
        String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf(".") + 1);
        if (!"txt".equals(suffix)) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }
        String line;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = bf.readLine()) != null) {
                Dictionaryusername username = dictionaryusernameService.findByUsername(line);
                if (Objects.isNull(username)) {
                    dictionaryusernameService.add(new Dictionaryusername(idWorker.nextId() + "", line));
                }
            }
        } catch (IOException ignored) {
        }
        //更新redis缓存
        freshDictUsernameRedisCache();
        return new Result(true, StatusCode.OK, "字典用户名已上传处理，请稍后查看");
    }

    private void freshDictUsernameRedisCache() {
        String redisDictUsername = "dictUsernameList_";
        redisTemplate.delete(redisDictUsername);
        List<String> allUsername = dictionaryusernameService.findAllUsername();
        if (!allUsername.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(redisDictUsername, allUsername);
        }
        //redisTemplate.opsForValue().set(redisDictUsername, allUsername);
        //如果设置过期时间，发任务时，如果缓存过期了，要重新加载数据到redis，有点多此一举
        //redisTemplate.expire(redisDictUsername, 2, TimeUnit.HOURS);

    }
}

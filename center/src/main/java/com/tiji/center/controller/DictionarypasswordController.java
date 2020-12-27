package com.tiji.center.controller;

import com.tiji.center.pojo.Dictionarypassword;
import com.tiji.center.service.DictionarypasswordService;
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
 * dictionarypassword控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/dictionarypassword")
public class DictionarypasswordController {

    @Autowired
    private DictionarypasswordService dictionarypasswordService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", dictionarypasswordService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", dictionarypasswordService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
     @PostMapping(value = "/search/{page}/{size}")
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Dictionarypassword> pageList = dictionarypasswordService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Dictionarypassword>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", dictionarypasswordService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param dictionarypassword
     */
    @PostMapping
    public Result add(@RequestBody Dictionarypassword dictionarypassword) {
        String password = dictionarypassword.getPassword();
        Dictionarypassword dictionarypasswordInDb = dictionarypasswordService.findByPassword(password);
        if (Objects.isNull(dictionarypasswordInDb)) {
            dictionarypasswordService.add(dictionarypassword);
            //更新redis缓存
            freshDictPasswdRedisCache();
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "字典密码重复");
        }
    }

    /**
     * 修改
     *
     * @param dictionarypassword
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Dictionarypassword dictionarypassword, @PathVariable String id) {
        dictionarypassword.setId(id);
        dictionarypasswordService.update(dictionarypassword);
        //更新redis缓存
        freshDictPasswdRedisCache();
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        dictionarypasswordService.deleteById(id);
        //更新redis缓存
        freshDictPasswdRedisCache();
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        dictionarypasswordService.deleteAllByIds(ids);
        //更新redis缓存
        freshDictPasswdRedisCache();
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入字典
     */
    @PostMapping(value = "/batchAdd")
    public Result batchAddDictionaryPassword(@RequestParam("file") MultipartFile file) {
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
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {
            while ((line = bf.readLine()) != null) {
                Dictionarypassword password = dictionarypasswordService.findByPassword(line);
                if (Objects.isNull(password)) {
                    dictionarypasswordService.add(new Dictionarypassword(idWorker.nextId() + "", line));
                }
            }
        } catch (IOException ignored) {
        }
        //更新redis缓存
        freshDictPasswdRedisCache();
        return new Result(true, StatusCode.OK, "字典密码已上传处理，请稍后查看");
    }

    private void freshDictPasswdRedisCache() {
        String redisDictPassword = "dictPasswordList_";
        redisTemplate.delete(redisDictPassword);
        List<String> allPassword = dictionarypasswordService.findAllPassword();
        if (!allPassword.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(redisDictPassword, allPassword);
        }
        //redisTemplate.opsForValue().set(redisDictPassword, allPassword);

        //如果设置过期时间，发任务时，如果缓存过期了，要重新加载数据到redis，有点多此一举
        //redisTemplate.expire(redisDictPassword, 2, TimeUnit.HOURS);
    }
}

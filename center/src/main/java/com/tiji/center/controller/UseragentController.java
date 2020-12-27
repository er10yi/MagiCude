package com.tiji.center.controller;

import com.tiji.center.pojo.Useragent;
import com.tiji.center.service.UseragentService;
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
 * useragent控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/useragent")
public class UseragentController {

    @Autowired
    private UseragentService useragentService;
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
        return new Result(true, StatusCode.OK, "查询成功", useragentService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", useragentService.findById(id));
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
        Page<Useragent> pageList = useragentService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Useragent>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", useragentService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param useragent
     */
    @PostMapping
    public Result add(@RequestBody Useragent useragent) {
        String ua = useragent.getUseragent();
        Useragent useragentInDb = useragentService.findByUseragent(ua);
        if (Objects.isNull(useragentInDb)) {
            useragentService.add(useragent);
            //更新redis缓存
            freshUserAgentRedisCache();
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：ua重复");
        }
    }

    /**
     * 修改
     *
     * @param useragent
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Useragent useragent, @PathVariable String id) {
        useragent.setId(id);
        useragentService.update(useragent);
        //更新redis缓存
        freshUserAgentRedisCache();
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        useragentService.deleteById(id);
        //更新redis缓存
        freshUserAgentRedisCache();
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        useragentService.deleteAllByIds(ids);
        //更新redis缓存
        freshUserAgentRedisCache();
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入ua
     */
    @PostMapping(value = "/batchAdd")
    public Result batchAdd(@RequestParam("file") MultipartFile file) throws IOException {
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

        //List<Useragent> useragentList = new ArrayList<>();

        String line;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = bf.readLine()) != null) {
                Useragent useragent = useragentService.findByUseragent(line);
                if (Objects.isNull(useragent)) {
                    useragentService.add(new Useragent(idWorker.nextId() + "", line));
                }
            }
        }
        //if (!useragentList.isEmpty()) {
        //    useragentService.batchAdd(useragentList);
        //}
        //更新redis缓存
        freshUserAgentRedisCache();
        return new Result(true, StatusCode.OK, "ua已上传处理，请稍后查看");

    }

    /**
     * 设置ua
     */
    private void freshUserAgentRedisCache() {

        String redisUserAgent = "userAgentSet_";
        redisTemplate.delete(redisUserAgent);
        List<String> allUserAgent = useragentService.findAllDistinctUserAgentList();
        allUserAgent.parallelStream().forEach(ua -> redisTemplate.opsForSet().add(redisUserAgent, ua));
    }

}

package com.tiji.center.controller;

import com.tiji.center.pojo.Riskservice;
import com.tiji.center.service.RiskserviceService;
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
 * riskservice控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/riskservice")
public class RiskserviceController {

    @Autowired
    private RiskserviceService riskserviceService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private String riskServiceSetKey = "riskServiceSet";
    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", riskserviceService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", riskserviceService.findById(id));
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
        Page<Riskservice> pageList = riskserviceService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Riskservice>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", riskserviceService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param riskservice
     */
    @PostMapping
    public Result add(@RequestBody Riskservice riskservice) {
        String service = riskservice.getService();
        Riskservice riskserviceInDb = riskserviceService.findByService(service);
        if (Objects.isNull(riskserviceInDb)) {
            riskserviceService.add(riskservice);
            redisTemplate.delete(riskServiceSetKey);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：服务重复");
        }
    }

    /**
     * 修改
     *
     * @param riskservice
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Riskservice riskservice, @PathVariable String id) {
        riskservice.setId(id);
        riskserviceService.update(riskservice);
        redisTemplate.delete(riskServiceSetKey);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        riskserviceService.deleteById(id);
        redisTemplate.delete(riskServiceSetKey);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        riskserviceService.deleteAllByIds(ids);
        redisTemplate.delete(riskServiceSetKey);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入高危服务
     */
    @PostMapping(value = "/batchAdd")
    public Result batchAdd(@RequestParam("file") MultipartFile file) {
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
                Riskservice riskservice = riskserviceService.findByService(line);
                if (Objects.isNull(riskservice)) {
                    riskserviceService.add(new Riskservice(idWorker.nextId() + "", line));
                }
            }
        } catch (IOException ignored) {
        }
        redisTemplate.delete(riskServiceSetKey);
        return new Result(true, StatusCode.OK, "高危服务已上传处理，请稍后查看");

    }
}

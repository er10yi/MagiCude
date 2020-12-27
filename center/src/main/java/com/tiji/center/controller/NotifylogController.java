package com.tiji.center.controller;

import com.tiji.center.pojo.Notifylog;
import com.tiji.center.service.NotifylogService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * notifylog控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/notifylog")
public class NotifylogController {

    @Autowired
    private NotifylogService notifylogService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", notifylogService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", notifylogService.findById(id));
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
        Page<Notifylog> pageList = notifylogService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Notifylog>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", notifylogService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param notifylog
     */
    @PostMapping
    public Result add(@RequestBody Notifylog notifylog) {
        notifylogService.add(notifylog);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param notifylog
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Notifylog notifylog, @PathVariable String id) {
        notifylog.setId(id);
        notifylogService.update(notifylog);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        notifylogService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        notifylogService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

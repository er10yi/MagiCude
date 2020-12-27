package com.tiji.center.controller;

import com.tiji.center.pojo.Projectvulnnotify;
import com.tiji.center.service.ProjectvulnnotifyService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * projectvulnnotify控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/projectvulnnotify")
public class ProjectvulnnotifyController {

    @Autowired
    private ProjectvulnnotifyService projectvulnnotifyService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", projectvulnnotifyService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", projectvulnnotifyService.findById(id));
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
        Page<Projectvulnnotify> pageList = projectvulnnotifyService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Projectvulnnotify>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", projectvulnnotifyService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param projectvulnnotify
     */
    @PostMapping
    public Result add(@RequestBody Projectvulnnotify projectvulnnotify) {
        projectvulnnotifyService.add(projectvulnnotify);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param projectvulnnotify
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Projectvulnnotify projectvulnnotify, @PathVariable String id) {
        projectvulnnotify.setId(id);
        projectvulnnotifyService.update(projectvulnnotify);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        projectvulnnotifyService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}

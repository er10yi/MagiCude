package com.tiji.center.controller;

import com.tiji.center.pojo.Projectinfo;
import com.tiji.center.pojo.Projectportwhitelist;
import com.tiji.center.service.ProjectinfoService;
import com.tiji.center.service.ProjectportwhitelistService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * projectportwhitelist控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/projectportwhitelist")
public class ProjectportwhitelistController {

    @Autowired
    private ProjectportwhitelistService projectportwhitelistService;
    @Autowired
    private ProjectinfoService projectinfoService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", projectportwhitelistService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", projectportwhitelistService.findById(id));
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
        Page<Projectportwhitelist> pageList = projectportwhitelistService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(projectportwhitelist -> {
            String projectinfoid = projectportwhitelist.getProjectinfoid();
            if (!StringUtils.isEmpty(projectinfoid)) {
                Projectinfo projectinfo = projectinfoService.findById(projectinfoid);
                projectportwhitelist.setProjectinfoid(projectinfo.getProjectname());
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Projectportwhitelist>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", projectportwhitelistService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param projectportwhitelist
     */
    @PostMapping
    public Result add(@RequestBody Projectportwhitelist projectportwhitelist) {
        String port = projectportwhitelist.getPort();
        Projectportwhitelist projectportwhitelistInDb = projectportwhitelistService.findByProjectinfoidAndPort(projectportwhitelist.getProjectinfoid(), port);
        if (Objects.isNull(projectportwhitelistInDb)) {
            projectportwhitelistService.add(projectportwhitelist);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：端口重复");
        }
    }

    /**
     * 修改
     *
     * @param projectportwhitelist
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Projectportwhitelist projectportwhitelist, @PathVariable String id) {
        projectportwhitelist.setId(id);
        projectportwhitelistService.update(projectportwhitelist);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        projectportwhitelistService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        projectportwhitelistService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

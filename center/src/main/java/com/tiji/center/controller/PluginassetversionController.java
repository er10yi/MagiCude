package com.tiji.center.controller;

import com.tiji.center.pojo.Pluginassetversion;
import com.tiji.center.service.PluginassetversionService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * assetversion控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/pluginassetversion")
public class PluginassetversionController {

    @Autowired
    private PluginassetversionService pluginassetversionService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", pluginassetversionService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", pluginassetversionService.findById(id));
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
        Page<Pluginassetversion> pageList = pluginassetversionService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Pluginassetversion>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", pluginassetversionService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param assetversion
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Pluginassetversion assetversion) {
        pluginassetversionService.add(assetversion);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param assetversion
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Pluginassetversion assetversion, @PathVariable String id) {
        assetversion.setId(id);
        pluginassetversionService.update(assetversion);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        pluginassetversionService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        pluginassetversionService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据pluginId查询
     *
     * @param pluginId pluginId
     * @return
     */
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.GET)
    public Result findAllByPluginId(@PathVariable String pluginId) {
        return new Result(true, StatusCode.OK, "查询成功", pluginassetversionService.findAllByPluginId(pluginId));
    }

}

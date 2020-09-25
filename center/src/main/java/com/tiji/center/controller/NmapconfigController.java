package com.tiji.center.controller;

import com.tiji.center.pojo.Nmapconfig;
import com.tiji.center.service.NmapconfigService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * nmapconfig控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/nmapconfig")
public class NmapconfigController {

    @Autowired
    private NmapconfigService nmapconfigService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", nmapconfigService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", nmapconfigService.findById(id));
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
        Page<Nmapconfig> pageList = nmapconfigService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Nmapconfig>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", nmapconfigService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param nmapconfig
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Nmapconfig nmapconfig) {
        nmapconfigService.add(nmapconfig);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param nmapconfig
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Nmapconfig nmapconfig, @PathVariable String id) {
        nmapconfig.setId(id);
        nmapconfigService.update(nmapconfig);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        nmapconfigService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据taskID查询
     *
     * @param taskId taskId
     * @return
     */
    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET)
    public Result findByTaskId(@PathVariable String taskId) {
        return new Result(true, StatusCode.OK, "查询成功", nmapconfigService.findByTaskid(taskId));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        nmapconfigService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }


}

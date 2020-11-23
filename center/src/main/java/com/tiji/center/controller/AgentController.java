package com.tiji.center.controller;

import com.tiji.center.pojo.Agent;
import com.tiji.center.service.AgentService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * agent控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", agentService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", agentService.findById(id));
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
        Page<Agent> pageList = agentService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", agentService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param agent
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Agent agent) {
        agentService.add(agent);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param agent
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Agent agent, @PathVariable String id) {
        agent.setId(id);
        agentService.update(agent);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        agentService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        agentService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}

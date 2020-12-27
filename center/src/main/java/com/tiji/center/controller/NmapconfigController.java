package com.tiji.center.controller;

import com.tiji.center.pojo.Nmapconfig;
import com.tiji.center.pojo.Task;
import com.tiji.center.service.NmapconfigService;
import com.tiji.center.service.TaskService;
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
    @Autowired
    private TaskService taskService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", nmapconfigService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
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
     @PostMapping(value = "/search/{page}/{size}")
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Nmapconfig> pageList = nmapconfigService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(nmapconfig -> {
            String taskid = nmapconfig.getTaskid();
            if (!StringUtils.isEmpty(taskid)) {
                Task task = taskService.findById(taskid);
                if (!Objects.isNull(task)) {
                    nmapconfig.setTaskid(task.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Nmapconfig>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", nmapconfigService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param nmapconfig
     */
    @PostMapping
    public Result add(@RequestBody Nmapconfig nmapconfig) {
        nmapconfigService.add(nmapconfig);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param nmapconfig
     */
    @PutMapping(value = "/{id}")
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
    @DeleteMapping(value = "/{id}")
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
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        nmapconfigService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }


}

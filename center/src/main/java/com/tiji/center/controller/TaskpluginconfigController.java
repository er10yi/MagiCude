package com.tiji.center.controller;

import com.tiji.center.pojo.Taskpluginconfig;
import com.tiji.center.service.TaskpluginconfigService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * taskpluginconfig控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/taskpluginconfig")
public class TaskpluginconfigController {

    @Autowired
    private TaskpluginconfigService taskpluginconfigService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findById(id));
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
        Page<Taskpluginconfig> pageList = taskpluginconfigService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Taskpluginconfig>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param taskpluginconfig
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Taskpluginconfig taskpluginconfig) {
        String taskid = taskpluginconfig.getTaskid();
        String pluginconfigid = taskpluginconfig.getPluginconfigid();
        Taskpluginconfig taskidAndPluginconfigid = taskpluginconfigService.findByTaskidAndPluginconfigid(taskid, pluginconfigid);
        if (Objects.isNull(taskidAndPluginconfigid)) {
            taskpluginconfigService.add(taskpluginconfig);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param taskpluginconfig
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Taskpluginconfig taskpluginconfig, @PathVariable String id) {
        String taskid = taskpluginconfig.getTaskid();
        String pluginconfigid = taskpluginconfig.getPluginconfigid();
        Taskpluginconfig taskidAndPluginconfigid = taskpluginconfigService.findByTaskidAndPluginconfigid(taskid, pluginconfigid);
        if (Objects.isNull(taskidAndPluginconfigid)) {
            taskpluginconfig.setId(id);
            taskpluginconfigService.update(taskpluginconfig);
            return new Result(true, StatusCode.OK, "修改成功");
        } else {
            return new Result(false, StatusCode.ERROR, "修改失败");
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        taskpluginconfigService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        taskpluginconfigService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据taskid查询
     *
     * @param taskid
     * @return
     */
    @RequestMapping(value = "/task/{taskid}", method = RequestMethod.GET)
    public Result findAllByTaskid(@PathVariable String taskid) {
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findAllByTaskid(taskid));
    }


    /**
     * 根据taskid和pluginids批量增加
     *
     * @param ids
     */
    @RequestMapping(value = "/plugin/ids", method = RequestMethod.POST)
    public Result addAllByIds(@RequestBody String[] ids) {
        String taskid = ids[0];
        boolean addFlag = false;
        for (int i = 1; i < ids.length; i++) {
            String pluginconfigid = ids[i];
            Taskpluginconfig taskidAndPluginconfigid = taskpluginconfigService.findByTaskidAndPluginconfigid(taskid, pluginconfigid);
            if (Objects.isNull(taskidAndPluginconfigid)) {
                taskpluginconfigService.add(new Taskpluginconfig(idWorker.nextId() + "", taskid, pluginconfigid));
                addFlag = true;
            }
        }
        if (addFlag) {
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败");

        }
    }

    /**
     * 根据taskid删除
     *
     * @param id
     */
    @RequestMapping(value = "/task/{id}", method = RequestMethod.DELETE)
    public Result deleteAllByTaskid(@PathVariable String id) {
        taskpluginconfigService.deleteAllByTaskid(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据taskid和pluginids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/task/ids", method = RequestMethod.POST)
    public Result deleteAllIds(@RequestBody String[] ids) {
        String taskid = ids[0];
        for (int i = 1; i < ids.length; i++) {
            String pluginconfigid = ids[i];
            taskpluginconfigService.deleteAllByTaskidAndPluginconfigid(taskid, pluginconfigid);

        }
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

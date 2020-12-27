package com.tiji.center.controller;

import com.tiji.center.pojo.Pluginconfig;
import com.tiji.center.pojo.Task;
import com.tiji.center.pojo.Taskip;
import com.tiji.center.pojo.Taskpluginconfig;
import com.tiji.center.service.PluginconfigService;
import com.tiji.center.service.TaskService;
import com.tiji.center.service.TaskpluginconfigService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
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
    @Autowired
    private TaskService taskService;

    @Autowired
    private PluginconfigService pluginconfigService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
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
     @PostMapping(value = "/search/{page}/{size}")
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Taskpluginconfig> pageList = taskpluginconfigService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(taskpluginconfig -> {
            String taskid = taskpluginconfig.getTaskid();
            if (!StringUtils.isEmpty(taskid)) {
                Task task = taskService.findById(taskid);
                if (!Objects.isNull(task)) {
                    taskpluginconfig.setTaskid(task.getName());
                }
            }
            String pluginconfigid = taskpluginconfig.getPluginconfigid();
            if (!StringUtils.isEmpty(pluginconfigid)) {
                Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                if (!Objects.isNull(pluginconfig)) {
                    taskpluginconfig.setPluginconfigid(pluginconfig.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Taskpluginconfig>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param taskpluginconfig
     */
    @PostMapping
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
    @PutMapping(value = "/{id}")
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
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        taskpluginconfigService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
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
        return new Result(true, StatusCode.OK, "查询成功", taskpluginconfigService.findPluginconfigidByTaskid(taskid));
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
            return new Result(true, StatusCode.OK, "启用成功");
        } else {
            return new Result(false, StatusCode.ERROR, "启用失败");

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
    public Result deleteAllIds(@RequestBody List<String> ids) {
        String taskid = ids.get(0);
        ids.parallelStream().forEach(pluginconfigid -> {
            taskpluginconfigService.deleteAllByTaskidAndPluginconfigid(taskid, pluginconfigid);
        });
        return new Result(true, StatusCode.OK, "禁用成功");
    }
}

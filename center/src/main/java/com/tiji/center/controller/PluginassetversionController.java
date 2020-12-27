package com.tiji.center.controller;

import com.tiji.center.pojo.Pluginassetversion;
import com.tiji.center.pojo.Pluginconfig;
import com.tiji.center.service.PluginassetversionService;
import com.tiji.center.service.PluginconfigService;
import com.tiji.center.service.VulnService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    private PluginconfigService pluginconfigService;

    /**
     * 查询全部数据
     *

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
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
     @PostMapping(value = "/search/{page}/{size}")
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Pluginassetversion> pageList = pluginassetversionService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(Pluginassetversion -> {
            String pluginconfigid = Pluginassetversion.getPluginconfigid();
            if (!StringUtils.isEmpty(pluginconfigid)) {
                Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                if (!Objects.isNull(pluginconfig)) {
                    Pluginassetversion.setPluginconfigid(pluginconfig.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Pluginassetversion>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", pluginassetversionService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param assetversion
     */
    @PostMapping
    public Result add(@RequestBody Pluginassetversion assetversion) {
        pluginassetversionService.add(assetversion);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param assetversion
     */
    @PutMapping(value = "/{id}")
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
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        pluginassetversionService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
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

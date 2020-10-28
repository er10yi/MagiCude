package com.tiji.center.controller;

import com.tiji.center.pojo.Checkresult;
import com.tiji.center.pojo.Pluginconfig;
import com.tiji.center.pojo.Vuln;
import com.tiji.center.pojo.Vulnpluginconfig;
import com.tiji.center.service.PluginconfigService;
import com.tiji.center.service.VulnService;
import com.tiji.center.service.VulnpluginconfigService;
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
 * vulnpluginconfig控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/vulnpluginconfig")
public class VulnpluginconfigController {

    @Autowired
    private VulnpluginconfigService vulnpluginconfigService;
    @Autowired
    private VulnService vulnService;
    @Autowired
    private PluginconfigService pluginconfigService;
    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", vulnpluginconfigService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", vulnpluginconfigService.findById(id));
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
        Page<Vulnpluginconfig> pageList = vulnpluginconfigService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(Vulnpluginconfig -> {
            String vulnid = Vulnpluginconfig.getVulnid();
            if (!StringUtils.isEmpty(vulnid)) {
                Vuln vuln = vulnService.findById(vulnid);
                if (!Objects.isNull(vuln)) {
                    Vulnpluginconfig.setVulnid(vuln.getName());
                }
            }
            String pluginconfigid = Vulnpluginconfig.getPluginconfigid();
            if (!StringUtils.isEmpty(pluginconfigid)) {
                Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                if (!Objects.isNull(pluginconfig)) {
                    Vulnpluginconfig.setPluginconfigid(pluginconfig.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Vulnpluginconfig>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", vulnpluginconfigService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param vulnpluginconfig
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Vulnpluginconfig vulnpluginconfig) {
        vulnpluginconfigService.add(vulnpluginconfig);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param vulnpluginconfig
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Vulnpluginconfig vulnpluginconfig, @PathVariable String id) {
        vulnpluginconfig.setId(id);
        vulnpluginconfigService.update(vulnpluginconfig);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        vulnpluginconfigService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        vulnpluginconfigService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据pluginId查询所有漏洞名称
     *
     * @param pluginId pluginId
     * @return
     */
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.GET)
    public Result findAllByPluginId(@PathVariable String pluginId) {
        return new Result(true, StatusCode.OK, "查询成功", vulnpluginconfigService.findAllByPluginconfigid(pluginId));
    }

    /**
     * 根据pluginId 删除
     *
     * @param pluginId pluginId
     * @return
     */
    @RequestMapping(value = "/plugin/{pluginId}", method = RequestMethod.DELETE)
    public Result deleteAllByPluginId(@PathVariable String pluginId) {
        vulnpluginconfigService.deleteAllByPluginconfigid(pluginId);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

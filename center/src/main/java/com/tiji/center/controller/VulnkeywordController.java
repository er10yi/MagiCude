package com.tiji.center.controller;

import com.tiji.center.pojo.Pluginconfig;
import com.tiji.center.pojo.Vulnkeyword;
import com.tiji.center.service.PluginconfigService;
import com.tiji.center.service.VulnService;
import com.tiji.center.service.VulnkeywordService;
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
 * vulnkeyword控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/vulnkeyword")
public class VulnkeywordController {

    @Autowired
    private VulnkeywordService vulnkeywordService;
    @Autowired
    private PluginconfigService pluginconfigService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", vulnkeywordService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", vulnkeywordService.findById(id));
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
        Page<Vulnkeyword> pageList = vulnkeywordService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(Vulnkeyword -> {
            String pluginconfigid = Vulnkeyword.getPluginconfigid();
            if (!StringUtils.isEmpty(pluginconfigid)) {
                Pluginconfig pluginconfig = pluginconfigService.findById(pluginconfigid);
                if (!Objects.isNull(pluginconfig)) {
                    Vulnkeyword.setPluginconfigid(pluginconfig.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Vulnkeyword>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", vulnkeywordService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param vulnkeyword
     */
    @PostMapping
    public Result add(@RequestBody Vulnkeyword vulnkeyword) {
        vulnkeywordService.add(vulnkeyword);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param vulnkeyword
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Vulnkeyword vulnkeyword, @PathVariable String id) {
        vulnkeyword.setId(id);
        vulnkeywordService.update(vulnkeyword);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        vulnkeywordService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        vulnkeywordService.deleteAllByIds(ids);
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
        return new Result(true, StatusCode.OK, "查询成功", vulnkeywordService.findAllByPluginId(pluginId));
    }

    /**
     * 查询所有keyword并去重
     *
     * @return List
     */
    @RequestMapping(value = "/plugin", method = RequestMethod.GET)
    public Result findAllDistinctVersion() {
        return new Result(true, StatusCode.OK, "查询成功", vulnkeywordService.findAllDistinctVulnKeyword());
    }
}

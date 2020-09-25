package com.tiji.center.controller;

import com.tiji.center.pojo.Checkresult;
import com.tiji.center.service.CheckresultService;
import com.tiji.center.service.CheckresultVulnService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * checkresult控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/checkresult")
public class CheckresultController {

    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private CheckresultVulnService checkresultVulnService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findById(id));
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
        Page<Checkresult> pageList = checkresultService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Checkresult>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param checkresult
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Checkresult checkresult) {
        checkresultService.add(checkresult);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param checkresult
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Checkresult checkresult, @PathVariable String id) {
        checkresult.setId(id);
        checkresultService.update(checkresult);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        checkresultService.deleteById(id);
        //删除检测结果的同时，删除中间表关联
        checkresultVulnService.deleteAllByCheckresultid(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据assetportid查询
     *
     * @param assetportid assetportid
     * @return
     */
    @RequestMapping(value = "/assetport/{assetportid}", method = RequestMethod.GET)
    public Result findAllByAssetportid(@PathVariable String assetportid) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findAllByAssetportid(assetportid));
    }

    /**
     * 根据ids查询漏洞
     *
     * @param ids ids
     * @return id-漏洞名称
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findAllByIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findAllByIds(ids));
    }

    /**
     * 根据assetportids查询
     *
     * @param assetportids assetportids
     * @return
     */
    @RequestMapping(value = "/assetportids/{assetportids}", method = RequestMethod.GET)
    public Result findAllByAssetportIds(@PathVariable String[] assetportids) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findAllByAssetportIds(assetportids));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        checkresultService.deleteAllByIds(ids);
        ids.forEach(id -> {
//            checkresultService.deleteById(id);
            //删除检测结果的同时，删除中间表关联
            checkresultVulnService.deleteAllByCheckresultid(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

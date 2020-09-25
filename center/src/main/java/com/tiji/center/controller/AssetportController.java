package com.tiji.center.controller;

import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Checkresult;
import com.tiji.center.pojo.Webinfo;
import com.tiji.center.service.*;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * assetport控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/assetport")
public class AssetportController {

    @Autowired
    private AssetportService assetportService;

    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private CheckresultVulnService checkresultVulnService;
    @Autowired
    private WebinfoService webinfoService;
    @Autowired
    private UrlService urlService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findById(id));
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
        Page<Assetport> pageList = assetportService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Assetport>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param assetport
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Assetport assetport) {
        if (Objects.isNull(assetport.getDowntime())) {
            String port = assetport.getPort();
            Assetport assetportInDb = assetportService.findByAssetipidAndPortAndDowntimeIsNull(assetport.getAssetipid(), port);
            if (Objects.isNull(assetportInDb)) {
                assetportService.add(assetport);
                return new Result(true, StatusCode.OK, "增加成功");
            } else {
                return new Result(false, StatusCode.ERROR, "增加失败：端口重复");
            }
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：无法直接新增已关闭端口，请直接将资产端口关闭");
        }
    }

    /**
     * 修改
     *
     * @param assetport
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Assetport assetport, @PathVariable String id) {
        assetport.setId(id);
        assetportService.update(assetport);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        assetportService.deleteById(id);

        //删除漏洞检测结果
        List<Checkresult> checkresultList = checkresultService.deleteAllByAssetportid(id);
        //删除web信息
        List<Webinfo> webinfoList = webinfoService.deleteAllByPortid(id);
        checkresultList.forEach(checkresult -> {
            String checkresultId = checkresult.getId();
            //删除检测结果的同时，删除中间表关联
            checkresultVulnService.deleteAllByCheckresultid(checkresultId);
        });
        //删除url信息
        webinfoList.forEach(webinfo -> {
            String webinfoId = webinfo.getId();
            urlService.deleteAllByWebinfoid(webinfoId);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findByAssetIpIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findByIds(ids));
    }

    /**
     * 查询service并去重
     *
     * @return List
     */
    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public Result findAllDistinctService() {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findAllDistinctService());
    }

    /**
     * 查询version并去重
     *
     * @return List
     */
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public Result findAllDistinctVersion() {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findAllDistinctVersion());
    }

    /**
     * 根据assetipid查询
     *
     * @param assetipid assetipid
     * @return
     */
    @RequestMapping(value = "/assetip/{assetipid}", method = RequestMethod.GET)
    public Result findAllByAssetipid(@PathVariable String assetipid) {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findAllByAssetipid(assetipid));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        assetportService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除漏洞检测结果
            List<Checkresult> checkresultList = checkresultService.deleteAllByAssetportid(id);
            //删除web信息
            List<Webinfo> webinfoList = webinfoService.deleteAllByPortid(id);
            checkresultList.forEach(checkresult -> {
                String checkresultId = checkresult.getId();
                //删除检测结果的同时，删除中间表关联
                checkresultVulnService.deleteAllByCheckresultid(checkresultId);
            });
            //删除url信息
            webinfoList.forEach(webinfo -> {
                String webinfoId = webinfo.getId();
                urlService.deleteAllByWebinfoid(webinfoId);
            });
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

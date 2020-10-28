package com.tiji.center.controller;

import com.tiji.center.pojo.*;
import com.tiji.center.service.*;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * webinfo控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/webinfo")
public class WebinfoController {

    @Autowired
    private WebinfoService webinfoService;
    @Autowired
    private UrlService urlService;
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private AssetipService assetipService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", webinfoService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", webinfoService.findById(id));
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
        //根据ip查询webinfo
        List<String> assetPortIdList = new ArrayList<>();
        if (searchMap.containsKey("assetip")) {
            //ip -> assetportid
            String ipaddressv4 = (String) searchMap.get("assetip");
            Map<String, String> ipSearchMap = new HashMap<>();
            ipSearchMap.put("ipaddressv4", ipaddressv4);
            List<Assetip> assetipList = assetipService.findSearch(ipSearchMap);
            assetipList.forEach(ip -> {
                String ipId = ip.getId();
                List<Assetport> assetportList = assetportService.findAllByAssetipid(ipId);
                assetportList.forEach(assetport -> {
                    String assetportId = assetport.getId();
                    List<Webinfo> webinfoList = webinfoService.findAllByAssetportid(assetportId);
                    webinfoList.forEach(webinfo -> {
                        String webinfoPortid = webinfo.getPortid();
                        assetPortIdList.add(webinfoPortid);
                    });
                });

            });
            searchMap.put("portid", assetPortIdList);
        }

        //根据端口查询webinfo
        if (searchMap.containsKey("assetport")) {
            String port = (String) searchMap.get("assetport");
            Map<String, String> portSearchMap = new HashMap<>();
            portSearchMap.put("port", port);
            List<Assetport> assetportList = assetportService.findSearch(portSearchMap);
            assetportList.forEach(assetport -> {
                String assetportId = assetport.getId();
                List<Webinfo> webinfoList = webinfoService.findAllByAssetportid(assetportId);
                webinfoList.forEach(webinfo -> {
                    String webinfoPortid = webinfo.getPortid();
                    assetPortIdList.add(webinfoPortid);
                });
            });
            searchMap.put("portid", assetPortIdList);
        }
        System.out.println(assetPortIdList);

        Page<Webinfo> pageList = webinfoService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(webinfo -> {
            String id = webinfo.getId();
            String assetportid = webinfo.getPortid();
            Assetport assetport = assetportService.findById(assetportid);
            webinfo.setPortid(assetport.getPort());
            String assetipid = assetport.getAssetipid();

            Assetip assetip = assetipService.findById(assetipid);
            webinfo.setAssetip(assetip.getIpaddressv4());


            List<Url> allByWebinfoid = urlService.findAllByWebinfoid(id);
            StringBuilder stringBuilder = new StringBuilder();
            allByWebinfoid.forEach(url -> {
                stringBuilder.append(url.getName()).append(" ").append(url.getUrl()).append("\n");
            });
            webinfo.setUrl(stringBuilder.toString());
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Webinfo>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", webinfoService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param webinfo
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Webinfo webinfo) {
        webinfoService.add(webinfo);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param webinfo
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Webinfo webinfo, @PathVariable String id) {
        webinfo.setId(id);
        webinfoService.update(webinfo);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        webinfoService.deleteById(id);
        //删除web信息同时，删除url
        urlService.deleteAllByWebinfoid(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        webinfoService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除web信息同时，删除url
            urlService.deleteAllByWebinfoid(id);
        });
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
        return new Result(true, StatusCode.OK, "查询成功", webinfoService.findAllByAssetportid(assetportid));
    }

    /**
     * 根据assetportids查询
     *
     * @param assetportids assetportids
     * @return
     */
    @RequestMapping(value = "/assetportids/{assetportids}", method = RequestMethod.GET)
    public Result findAllByAssetportIds(@PathVariable String[] assetportids) {
        return new Result(true, StatusCode.OK, "查询成功", webinfoService.findAllByAssetportIds(assetportids));
    }
}

package com.tiji.center.controller;

import com.tiji.center.pojo.*;
import com.tiji.center.pojo.category.CategoryTab;
import com.tiji.center.service.*;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private AssetipService assetipService;

    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private CheckresultVulnService checkresultVulnService;
    @Autowired
    private WebinfoService webinfoService;
    @Autowired
    private UrlService urlService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CategoryTabService categoryTabService;
    @Autowired
    private AssetipAppsysHostdomainService assetipAppsysHostdomainService;
    @Autowired
    private AppsystemService appsystemService;
    private String categoryCacheKey = "categoryTabCache";
    private String categoryIdNameKey = "categoryTabIdNameCache";

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        Assetport assetport = assetportService.findById(id);
        String tabbitmap = assetport.getTabbitmap();
        List<CategoryTab> categoryTabList = new ArrayList<>();
        if (!StringUtils.isEmpty(tabbitmap)) {
            List<String> resultList = new ArrayList<>();
            String[] splitTabs = tabbitmap.split(",");
            if (redisTemplate.hasKey(categoryIdNameKey)) {
                for (String tabid : splitTabs) {
                    resultList.add((String) redisTemplate.opsForHash().get(categoryIdNameKey, tabid));
                }
            } else {
                for (String tabid : splitTabs) {
                    CategoryTab categoryTab = categoryTabService.findById(Long.parseLong(tabid));
                    if (!Objects.isNull(categoryTab)) {
                        String categoryTabName = categoryTab.getName();
                        if (!StringUtils.isEmpty(categoryTabName)) {
                            resultList.add(categoryTabName);
                        }
                    }
                }
            }
            for (String tabid : splitTabs) {
                CategoryTab categoryTab = categoryTabService.findById(Long.parseLong(tabid));
                categoryTabList.add(categoryTab);
            }
            assetport.setTabname(String.valueOf(resultList).replaceAll("[\\[\\]]", ""));
        }

        StringBuilder appsysNameBuilder = new StringBuilder();
        List<AssetipAppsysHostdomain> hostdomainList = assetipAppsysHostdomainService.findByAssetportid(id);
        for (AssetipAppsysHostdomain hostdomain : hostdomainList) {
            String appsysid = hostdomain.getAppsysid();
            if (!StringUtils.isEmpty(appsysid)) {
                Appsystem appsystem = appsystemService.findById(appsysid);
                if (!Objects.isNull(appsystem)) {
                    String appsystemName = appsystem.getName();
                    appsysNameBuilder.append(appsystemName).append(",");
                }
            }
        }
        assetport.setAppsysname(appsysNameBuilder.toString());


        assetport.setTabList(categoryTabList);
        return new Result(true, StatusCode.OK, "查询成功", assetport);
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
        //根据ip查询端口
        List<String> assetipIdList = new ArrayList<>();
        if (searchMap.containsKey("assetip")) {
            //ip -> assetportid
            String ipaddressv4 = (String) searchMap.get("assetip");
            Map<String, String> ipSearchMap = new HashMap<>();
            ipSearchMap.put("ipaddressv4", ipaddressv4);
            List<Assetip> assetipList = assetipService.findSearch(ipSearchMap);
            assetipList.forEach(ip -> {
                String ipId = ip.getId();
                assetipIdList.add(ipId);
            });
            searchMap.put("assetipid", assetipIdList);
        }


        Page<Assetport> pageList = assetportService.findSearch(searchMap, page, size);
        Map<String, String> idNameMap = new HashMap<>();
        pageList.stream().parallel().forEach(assetport -> {
            String assetipid = assetport.getAssetipid();
            if (!StringUtils.isEmpty(assetipid)) {
                Assetip assetip = assetipService.findById(assetipid);
                if (!Objects.isNull(assetip)) {
                    assetport.setAssetipid(assetip.getIpaddressv4());
                }
            }
            String assetportId = assetport.getId();
            List<String> countResultList = assetportService.findCountByIds(Collections.singletonList(assetportId));
            String port = assetport.getPort();
            if (!StringUtils.isEmpty(port)) {
                assetport.setPort(port);
            }
            if (!"0:0".equals(countResultList.get(0))) {
                assetport.setStatistic(countResultList.get(0));
            }

            String tabbitmap = assetport.getTabbitmap();
            if (!StringUtils.isEmpty(tabbitmap)) {
                List<String> resultList = new ArrayList<>();
                String[] splitTabs = tabbitmap.split(",");
                if (redisTemplate.hasKey(categoryIdNameKey)) {
                    for (String id : splitTabs) {
                        resultList.add((String) redisTemplate.opsForHash().get(categoryIdNameKey, id));
                    }
                } else {
                    for (String id : splitTabs) {
                        CategoryTab categoryTab = categoryTabService.findById(Long.parseLong(id));
                        if (!Objects.isNull(categoryTab)) {
                            String categoryTabName = categoryTab.getName();
                            if (!StringUtils.isEmpty(categoryTabName)) {
                                resultList.add(categoryTabName);
                            }
                        }
                    }
                }
                assetport.setTabname(String.valueOf(resultList).replaceAll("[\\[\\]]", ""));
            }

        });
        if (!idNameMap.isEmpty()) {
            redisTemplate.opsForHash().putAll(categoryIdNameKey, idNameMap);
        }
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param assetport
     */
    @PostMapping
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
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Assetport assetport, @PathVariable String id) {
        assetport.setId(id);
        String sourceTabbitMap = assetport.getTabbitmap();
        if (!StringUtils.isEmpty(sourceTabbitMap)) {
            String[] strings = sourceTabbitMap.split(",");
            Set<String> set = new TreeSet<>(Arrays.asList(strings));
            assetport.setTabbitmap(set.toString().replaceAll("[\\[\\]\\s]", ""));
        }
        assetportService.update(assetport);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        assetportService.deleteById(id);
        //中间件表置空
        assetportService.updateMiddByAssetportidSetAssetportid2Null(id);
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
    @PostMapping(value = "/ids")
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
    @PostMapping(value = "/deleteids")
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

    /**
     * 根据id数组查询数量
     *
     * @param countbyids
     * @return
     */
    @RequestMapping(value = "/countbyids", method = RequestMethod.POST)
    public Result findCountByIds(@RequestBody List<String> countbyids) {
        return new Result(true, StatusCode.OK, "查询成功", assetportService.findCountByIds(countbyids));
    }
}

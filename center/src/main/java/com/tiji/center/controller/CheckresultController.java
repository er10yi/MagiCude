package com.tiji.center.controller;

import com.tiji.center.pojo.*;
import com.tiji.center.service.*;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.util.*;

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
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private AssetipService assetipService;
    @Autowired
    private VulnService vulnService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        Checkresult checkresult = checkresultService.findById(id);
        String assetportid = checkresult.getAssetportid();
        Assetport assetport = assetportService.findById(assetportid);
        String assetipid = assetport.getAssetipid();
        checkresult.setAssetipid(assetipid);

        List<CheckresultVuln> checkresultVulnList = checkresultVulnService.findAllByCheckresultid(id);
        String vulnid = checkresultVulnList.get(0).getVulnid();
        checkresult.setVulnid(vulnid);

        return new Result(true, StatusCode.OK, "查询成功", checkresult);
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
        //根据ip查询漏洞
        List<String> vulnPortIdList = new ArrayList<>();
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
                    List<Checkresult> checkresultList = checkresultService.findAllByAssetportid(assetportId);
                    checkresultList.forEach(checkresult -> {
                        String checkresultAssetportid = checkresult.getAssetportid();
                        vulnPortIdList.add(checkresultAssetportid);
                    });
                });

            });
            searchMap.put("assetportid", vulnPortIdList);
        }

        //根据端口查询漏洞
        if (searchMap.containsKey("assetport")) {
            String port = (String) searchMap.get("assetport");
            Map<String, String> portSearchMap = new HashMap<>();
            portSearchMap.put("port", port);
            List<Assetport> assetportList = assetportService.findSearch(portSearchMap);
            assetportList.forEach(assetport -> {
                String assetportId = assetport.getId();
                List<Checkresult> checkresultList = checkresultService.findAllByAssetportid(assetportId);
                checkresultList.forEach(checkresult -> {
                    String checkresultAssetportid = checkresult.getAssetportid();
                    vulnPortIdList.add(checkresultAssetportid);
                });
            });
            searchMap.put("assetportid", vulnPortIdList);
        }

        //根据漏洞名称查询漏洞
        if (searchMap.containsKey("vulname")) {
            String vulname = (String) searchMap.get("vulname");
            Map<String, String> vulNameSearchMap = new HashMap<>();
            vulNameSearchMap.put("name", vulname);
            List<Vuln> vulnList = vulnService.findSearch(vulNameSearchMap);
            List<String> checkResultIdList = new LinkedList<>();
            vulnList.forEach(vuln -> {
                String vulnId = vuln.getId();
                checkResultIdList.addAll(checkresultVulnService.findAllCheckResultIdByVulnid(vulnId));

            });
            searchMap.put("id", checkResultIdList);
        }

        Page<Checkresult> pageList = checkresultService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(checkresult -> {
            String id = checkresult.getId();
            String assetportid = checkresult.getAssetportid();
            Assetport assetport = assetportService.findById(assetportid);
            checkresult.setAssetportid(assetport.getPort());
            String assetipid = assetport.getAssetipid();

            Assetip assetip = assetipService.findById(assetipid);
            checkresult.setAssetip(assetip.getIpaddressv4());


            List<CheckresultVuln> checkresultVulnList = checkresultVulnService.findAllByCheckresultid(id);
            String vulnid = checkresultVulnList.get(0).getVulnid();
            Vuln vuln = vulnService.findById(vulnid);
            checkresult.setVulname(vuln.getName());

        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Checkresult>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param checkresult
     */
    @PostMapping
    public Result add(@RequestBody Checkresult checkresult) {
        String assetipid = checkresult.getAssetipid();
        String assetportid = checkresult.getAssetportid();
        System.out.println("assetportid: " + assetportid);
        if (StringUtils.isEmpty(checkresult.getVulnid()) || StringUtils.isEmpty(assetportid) || StringUtils.isEmpty(assetipid)) {
            return new Result(false, StatusCode.ERROR, "ip端口漏洞名称不能为空");
        }
        Assetport assetport = assetportService.findByIdAndAndAssetipidAndDowntimeIsNull(assetportid, assetipid);
        if (Objects.isNull(assetport)) {
            String portTemp = assetportid;
            assetportid = idWorker.nextId() + "";
            assetportService.add(new Assetport(assetportid, assetipid, portTemp, null, null, null, null, null, null, new Date(), null, null));
        } else {
            assetportid = assetport.getId();
        }
        String checkresultid = idWorker.nextId() + "";
        checkresult.setId(checkresultid);
        checkresult.setAssetportid(assetportid);
        checkresultVulnService.add(new CheckresultVuln(idWorker.nextId() + "", checkresultid, checkresult.getVulnid()));
        checkresultService.add(checkresult);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param checkresult
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Checkresult checkresult, @PathVariable String id) {
        checkresult.setId(id);
        if (StringUtils.isEmpty(checkresult.getVulnid()) || StringUtils.isEmpty(checkresult.getAssetportid()) || StringUtils.isEmpty(checkresult.getAssetipid())) {
            return new Result(false, StatusCode.ERROR, "ip端口漏洞名称不能为空");
        }
        checkresultService.update(checkresult);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
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
        List<Checkresult> checkresultList = checkresultService.findAllByAssetportid(assetportid);
        checkresultList.stream().parallel().forEach(checkresult -> {
            String id = checkresult.getId();
            List<CheckresultVuln> vulnList = checkresultVulnService.findAllByCheckresultid(id);
            if (!vulnList.isEmpty()) {
                String vulnid = vulnList.get(0).getVulnid();
                Vuln vuln = vulnService.findById(vulnid);
                if (!Objects.isNull(vuln)) {
                    String vulnName = vuln.getName();
                    if (!StringUtils.isEmpty(vulnName)) {
                        checkresult.setVulname(vulnName);
                    }
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", checkresultList);
    }

    /**
     * 根据ids查询漏洞
     *
     * @param ids ids
     * @return id-漏洞名称
     */
    @PostMapping(value = "/ids")
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
        List<Checkresult> checkresultList = checkresultService.findAllByAssetportIds(assetportids);
        checkresultList.stream().parallel().forEach(checkresult -> {
            String id = checkresult.getId();
            List<CheckresultVuln> vulnList = checkresultVulnService.findAllByCheckresultid(id);
            if (!vulnList.isEmpty()) {
                String vulnid = vulnList.get(0).getVulnid();
                Vuln vuln = vulnService.findById(vulnid);
                if (!Objects.isNull(vuln)) {
                    String vulnName = vuln.getName();
                    if (!StringUtils.isEmpty(vulnName)) {
                        checkresult.setVulname(vulnName);
                    }
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", checkresultList);
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        checkresultService.deleteAllByIds(ids);
        ids.forEach(id -> {
//            checkresultService.deleteById(id);
            //删除检测结果的同时，删除中间表关联
            checkresultVulnService.deleteAllByCheckresultid(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据id查询漏洞
     *
     * @param id
     * @return 漏洞名称
     */
    @RequestMapping(value = "/vulname/{id}", method = RequestMethod.GET)
    public Result findVulNameById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultService.findVulNameById(id));
    }
}

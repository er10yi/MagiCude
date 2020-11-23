package com.tiji.center.controller;

import com.tiji.center.pojo.*;
import com.tiji.center.service.*;
import com.tiji.center.util.TijiHelper;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;
import util.IpRange2Ips;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * assetip控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/assetip")
public class AssetipController {

    @Autowired
    private AssetipService assetipService;
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private HostService hostService;

    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private CheckresultVulnService checkresultVulnService;
    @Autowired
    private WebinfoService webinfoService;
    @Autowired
    private UrlService urlService;
    @Autowired
    private ProjectinfoService projectinfoService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", assetipService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        Assetip assetip = assetipService.findById(id);
        String projectinfoid = assetip.getProjectinfoid();
        if (!StringUtils.isEmpty(projectinfoid)) {
            Projectinfo projectinfo = projectinfoService.findById(projectinfoid);
            assetip.setProjectname(projectinfo.getProjectname());
        }
        return new Result(true, StatusCode.OK, "查询成功", assetip);
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
        Page<Assetip> pageList = assetipService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(assetip -> {
            String projectinfoid = assetip.getProjectinfoid();
            if (!StringUtils.isEmpty(projectinfoid)) {
                Projectinfo projectinfo = projectinfoService.findById(projectinfoid);
                if (!Objects.isNull(projectinfo)) {
                    assetip.setProjectname(projectinfo.getProjectname());
                }
            }
            String assetipId = assetip.getId();

            List<String> resultList = assetipService.findCountByIds(Collections.singletonList(assetipId));
            String ipaddressv4 = assetip.getIpaddressv4();
            if (!StringUtils.isEmpty(ipaddressv4)) {
                assetip.setIpaddressv4(ipaddressv4);
            }
            if (!"0:0:0:0".equals(resultList.get(0))) {
                assetip.setStatistic(resultList.get(0));
            } else {
                assetip.setIpaddressv4(ipaddressv4);
            }

        });

        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", assetipService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param assetip
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Assetip assetip) {
        if (Objects.isNull(assetip.getPassivetime())) {
            String ipaddressv4 = assetip.getIpaddressv4();
            Assetip assetipInDb = assetipService.findByIpaddressv4AndPassivetimeIsNull(ipaddressv4);
            if (Objects.isNull(assetipInDb)) {
                assetipService.add(assetip);
                return new Result(true, StatusCode.OK, "增加成功");
            } else {
                return new Result(false, StatusCode.ERROR, "增加失败：ip重复");
            }
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：无法直接新增已下线ip，请直接将资产ip下线");
        }
    }

    /**
     * 修改
     *
     * @param assetip
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Assetip assetip, @PathVariable String id) {
        assetip.setId(id);
        assetipService.update(assetip);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     * 资产ip, 资产端口, 主机信息, 位置信息, 漏洞检测结果, web信息, url信息
     *
     * @param id
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        assetipService.deleteById(id);
        //删除资产端口
        List<Assetport> assetportList = assetportService.deleteAllByAssetipid(id);
        //删除主机信息
        hostService.deleteAllByAssetipid(id);
        assetportList.forEach(assetport -> {
            String assetportId = assetport.getId();
            //删除漏洞检测结果
            List<Checkresult> checkresultList = checkresultService.deleteAllByAssetportid(assetportId);
            //删除web信息
            List<Webinfo> webinfoList = webinfoService.deleteAllByPortid(assetportId);
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
     * 批量导入ip端口
     */
    @RequestMapping(value = "/batchAdd", method = RequestMethod.POST)
    public Result batchAdd(@RequestParam("file") MultipartFile file) {
        if (Objects.isNull(file) || file.getSize() == 0) {
            return new Result(false, StatusCode.ERROR, "文件为空");
        }
        long fileSize = file.getSize();
        if (fileSize / 1024 / 1024 > 3) {
            return new Result(false, StatusCode.ERROR, "文件大小不能超过 3M");
        }
        String fileContentType = file.getContentType();
        assert fileContentType != null;
        if (!fileContentType.equals("text/plain")) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }
        String fileOriginalFilename = file.getOriginalFilename();
        assert fileOriginalFilename != null;
        String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf(".") + 1);
        if (!suffix.equals("txt")) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }

        Map<String, Set<String>> resultMap = new LinkedHashMap<>();
        String line;
        Date date = new Date();
        Set<String> ipSet = new HashSet<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {
            while ((line = bf.readLine()) != null) {
                //有端口
                if (line.contains("[[")) {
                    line = line.replaceAll("\\[\\[", "@");
                    line = line.replaceAll("],\\s?\\[", "!");
                    line = line.replaceAll("]]", "");
                    String ipp = line.split("@")[0];
                    String servers = line.split("@")[1];
                    List<String> serverList = Arrays.asList(servers.split("!"));
                    Set<String> resultSet = new HashSet<>(serverList);
                    if (resultMap.containsKey(ipp)) {
                        Set<String> set = resultMap.get(ipp);
                        set.addAll(resultSet);
                        resultMap.put(ipp, set);
                    } else {
                        resultMap.put(ipp, resultSet);
                    }
                } else {
                    //20201012 新增 增加无端口的ip导入格式
                    //没有端口，新增ip
                    ipSet.addAll(IpRange2Ips.genIp(line));
                }

            }
        } catch (IOException ignored) {
        }
        if (!resultMap.isEmpty()) {
            TijiHelper.nmapScanResult2AssetDB(assetipService, assetportService, hostService, idWorker, resultMap);
        }
        if (!ipSet.isEmpty()) {
            ipSet.forEach(ip -> {
                Assetip assetip = assetipService.findByIpaddressv4AndPassivetimeIsNull(ip);
                String assetipId;
                //ip不存在，新增
                if (Objects.isNull(assetip)) {
                    assetipId = idWorker.nextId() + "";
                    assetipService.add(new Assetip(assetipId, null, ip, null, false, false, date, null, null));
                }
            });
        }
        return new Result(true, StatusCode.OK, "ip端口已上传处理，请稍后查看");

    }

    /**
     * 根据ids数组查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findByAssetIpIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", assetipService.findByIds(ids));
    }

    /**
     * 批量导入部门ip
     */
    @RequestMapping(value = "/projectinfoip/batchAdd", method = RequestMethod.POST)
    public Result batchAddProjectinfoIp(@RequestParam("file") MultipartFile file) {
        if (Objects.isNull(file) || file.getSize() == 0) {
            return new Result(false, StatusCode.ERROR, "文件为空");
        }
        long fileSize = file.getSize();
        if (fileSize / 1024 / 1024 > 3) {
            return new Result(false, StatusCode.ERROR, "文件大小不能超过 3M");
        }
        String fileContentType = file.getContentType();
        assert fileContentType != null;
        if (!fileContentType.equals("text/plain")) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }
        String fileOriginalFilename = file.getOriginalFilename();
        assert fileOriginalFilename != null;
        String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf(".") + 1);
        if (!suffix.equals("txt")) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }

        String line;
        //20201012 优化 去除bf.close，bf放到try中
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            Date date = new Date();
            while ((line = bf.readLine()) != null) {
                String projectinfoName = line.split("\\|")[0];
                String ipRange = line.split("\\|")[1];
                Projectinfo projectinfo = projectinfoService.findByProjectname(projectinfoName);
                String projectInfoId;
                if (Objects.isNull(projectinfo)) {
                    projectInfoId = idWorker.nextId() + "";
                    projectinfoService.add(new Projectinfo(projectInfoId, null, projectinfoName, false, false, date, false));
                } else {
                    projectInfoId = projectinfo.getId();
                }

                Set<String> ipSet = IpRange2Ips.genIp(ipRange);
                ipSet.forEach(ip -> {
                    Assetip assetip = assetipService.findByIpaddressv4AndPassivetimeIsNull(ip);
                    String assetipId;
                    //System.out.println(projectInfoId + " = " + ip);
                    //20201012 移除 移除如果ip不存在，会新增
                    //ip不存在，新增
                    if (Objects.isNull(assetip)) {
                        //assetipId = idWorker.nextId() + "";
                        //assetipService.add(new Assetip(assetipId, projectInfoId, ip, null, false, false, date, null, null));
                    } else {
                        //ip存在
                        //projectInfoId = assetip.getProjectinfoid();
                        //没有项目信息
                        if (Objects.isNull(assetip.getProjectinfoid()) || assetip.getProjectinfoid().isEmpty()) {
                            assetip.setProjectinfoid(projectInfoId);
                            assetipService.update(assetip);
                        }
                    }
                });
            }
        } catch (IOException ignored) {
        }

        return new Result(true, StatusCode.OK, "项目信息ip已上传处理，请稍后查看");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        assetipService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除资产端口
            List<Assetport> assetportList = assetportService.deleteAllByAssetipid(id);
            //删除主机信息
            hostService.deleteAllByAssetipid(id);
            assetportList.forEach(assetport -> {
                String assetportId = assetport.getId();
                //删除漏洞检测结果
                List<Checkresult> checkresultList = checkresultService.deleteAllByAssetportid(assetportId);
                //删除web信息
                List<Webinfo> webinfoList = webinfoService.deleteAllByPortid(assetportId);
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
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }

}

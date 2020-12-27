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
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * host控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/host")
public class HostController {

    @Autowired
    private HostService hostService;


    @Autowired
    private AssetipService assetipService;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private AssetportService assetportService;

    @Autowired
    private AssetipAppsysHostdomainService assetipAppsysHostdomainService;

    @Autowired
    private AppsystemService appsystemService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", hostService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", hostService.findById(id));
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
        //根据ip查询主机
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

        Page<Host> pageList = hostService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(host -> {
            String assetipid = host.getAssetipid();
            if (!StringUtils.isEmpty(assetipid)) {
                Assetip assetip = assetipService.findById(assetipid);
                if (!Objects.isNull(assetip)) {
                    host.setAssetipid(assetip.getIpaddressv4());
                }
            }
            StringBuilder appsysNameBuilder = new StringBuilder();
            List<AssetipAppsysHostdomain> hostdomainList = assetipAppsysHostdomainService.findByAssetipid(assetipid);
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
            host.setAppsysname(appsysNameBuilder.toString());
        });
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
        return new Result(true, StatusCode.OK, "查询成功", hostService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param host
     */
    @PostMapping
    public Result add(@RequestBody Host host) {
        String hostname = host.getHostname();
        Host hostInDb = hostService.findByAssetipidAndHostname(host.getAssetipid(), hostname);
        if (Objects.isNull(hostInDb)) {
            hostService.add(host);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：ip和主机名重复");
        }
    }

    /**
     * 修改
     *
     * @param host
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Host host, @PathVariable String id) {
        host.setId(id);
        hostService.update(host);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        hostService.deleteById(id);
        //将中间表的域名id置空
        assetipAppsysHostdomainService.updateMiddByHostidSetHostid2Null(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入ip主机名
     */

    @PostMapping(value = "/batchAdd")
    public Result batchAdd(@RequestParam("file") MultipartFile file) throws IOException {
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
        Date date = new Date();
        List<Host> hostList = new ArrayList<>();
        List<Assetip> assetipListList = new ArrayList<>();
        Set<String> tempSet = new HashSet<>();
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = bf.readLine()) != null) {
                tempSet.add(line);
            }
        } catch (IOException ignored) {
        }
        tempSet.forEach(infoLine -> {
            String ip = infoLine.split(":")[0];
            String hostname = infoLine.split(":")[1];
            Assetip assetip = assetipService.findByIpaddressv4AndPassivetimeIsNull(ip);
            String temp = hostname.split("\\.")[0];
            String owner = temp.substring(temp.indexOf("-") + 1);
            if (assetip != null) {
                //20201012 修复 修复ip主机名，主机名重复时，导入失败问题
                String assetipId = assetip.getId();
                Host hostnameInDb = hostService.findByAssetipidAndHostname(assetipId, hostname);
                if (Objects.isNull(hostnameInDb)) {
                    Host host = new Host(idWorker.nextId() + "", assetipId, null, hostname, null, null, null, owner, date, null);
                    hostList.add(host);
                }
            } else {
                String assetipId = idWorker.nextId() + "";
                //20201012 修复 修复ip主机名，主机名重复时，导入失败问题
                Host hostnameInDb = hostService.findByAssetipidAndHostname(assetipId, hostname);
                if (Objects.isNull(hostnameInDb)) {
                    hostList.add(new Host(idWorker.nextId() + "", assetipId, null, hostname, null, null, null, owner, date, null));
                }
                assetipService.add(new Assetip(assetipId, null, ip, null, false, false, date, null, null));
            }
        });
        if (!hostList.isEmpty()) {
            hostService.batchAdd(hostList);
        }
        return new Result(true, StatusCode.OK, "ip主机名已上传处理，请稍后查看");
    }

    /**
     * 根据assetipid查询
     *
     * @param assetipid assetipid
     * @return
     */
    @RequestMapping(value = "/assetip/{assetipid}", method = RequestMethod.GET)
    public Result findAllByAssetipid(@PathVariable String assetipid) {
        return new Result(true, StatusCode.OK, "查询成功", hostService.findAllByAssetipid(assetipid));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        hostService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

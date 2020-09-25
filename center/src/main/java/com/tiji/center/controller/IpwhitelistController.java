package com.tiji.center.controller;

import com.tiji.center.pojo.Assetip;
import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Ipportwhitelist;
import com.tiji.center.pojo.Ipwhitelist;
import com.tiji.center.service.AssetipService;
import com.tiji.center.service.AssetportService;
import com.tiji.center.service.IpportwhitelistService;
import com.tiji.center.service.IpwhitelistService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.IdWorker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ipwhitelist控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/ipwhitelist")
public class IpwhitelistController {

    @Autowired
    private IpwhitelistService ipwhitelistService;
    @Autowired
    private IdWorker idWorker;

    @Autowired
    private IpportwhitelistService ipportwhitelistService;

    @Autowired
    private AssetipService assetipService;
    @Autowired
    private AssetportService assetportService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", ipwhitelistService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", ipwhitelistService.findById(id));
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
        Page<Ipwhitelist> pageList = ipwhitelistService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Ipwhitelist>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", ipwhitelistService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param ipwhitelist
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Ipwhitelist ipwhitelist) {
        String ip = ipwhitelist.getIp();
        Ipwhitelist ipwhitelistInDb = ipwhitelistService.findByIp(ip);
        if (Objects.isNull(ipwhitelistInDb)) {
            ipwhitelistService.add(ipwhitelist);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：ip重复");
        }
    }

    /**
     * 修改
     *
     * @param ipwhitelist
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Ipwhitelist ipwhitelist, @PathVariable String id) {
        ipwhitelist.setId(id);
        ipwhitelistService.update(ipwhitelist);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        ipwhitelistService.deleteById(id);
        //删除端口白名单
        ipportwhitelistService.deleteAllByipwhitelistid(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        ipwhitelistService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除端口白名单
            ipportwhitelistService.deleteAllByipwhitelistid(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入ip端口白名单
     */
    @RequestMapping(value = "/batchAdd", method = RequestMethod.POST)
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
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = bf.readLine()) != null) {
                String ip = line.split("\\|")[0];
                String port = line.split("\\|")[1];
                boolean checkWhitelist = line.split("\\|")[2].equals("1");
                boolean notifyWhitelist = line.split("\\|")[3].equals("1");
                //端口为空，ip白名单
                if (Objects.isNull(port) || port.isEmpty()) {
                    Ipwhitelist ipwhitelist = ipwhitelistService.findByIp(ip);
                    //不在数据库中
                    if (Objects.isNull(ipwhitelist)) {
                        ipwhitelistService.add(new Ipwhitelist(idWorker.nextId() + "", ip, checkWhitelist, notifyWhitelist));
                    } else {
                        ipwhitelist.setCheckwhitelist(checkWhitelist);
                        ipwhitelist.setNotifywhitelist(notifyWhitelist);
                        ipwhitelistService.update(ipwhitelist);
                        //删除端口白名单
                        ipportwhitelistService.deleteAllByipwhitelistid(ipwhitelist.getId());
                    }
                } else {
                    //端口白名单
                    Ipwhitelist ipwhitelist = ipwhitelistService.findByIp(ip);
                    //不在数据库中
                    String ipwhitelistId = idWorker.nextId() + "";
                    if (Objects.isNull(ipwhitelist)) {
                        ipwhitelistService.add(new Ipwhitelist(ipwhitelistId, ip, false, false));
                        ipportwhitelistService.add(new Ipportwhitelist(idWorker.nextId() + "", ipwhitelistId, port, checkWhitelist, notifyWhitelist));
                    } else {
                        //在数据库中，新增端口
                        ipwhitelistId = ipwhitelist.getId();
                        //如果是端口白名单，则ip白名单置空
                        if (checkWhitelist) {
                            ipwhitelist.setCheckwhitelist(false);
                            ipwhitelistService.add(ipwhitelist);
                        }
                        if (notifyWhitelist) {
                            ipwhitelist.setNotifywhitelist(false);
                            ipwhitelistService.add(ipwhitelist);
                        }
                        Ipportwhitelist ipportwhitelist = ipportwhitelistService.findByIpwhitelistidAndPort(ipwhitelistId, port);
                        if (Objects.isNull(ipportwhitelist)) {
                            ipportwhitelistService.add(new Ipportwhitelist(idWorker.nextId() + "", ipwhitelistId, port, checkWhitelist, notifyWhitelist));
                        } else {
                            ipportwhitelist.setCheckwhitelist(checkWhitelist);
                            ipportwhitelist.setNotifywhitelist(notifyWhitelist);
                            ipportwhitelistService.update(ipportwhitelist);
                        }
                    }
                }

            }
        } catch (IOException ignored) {
        }
        return new Result(true, StatusCode.OK, "ip端口白名单已上传处理，请稍后查看");

    }

    /**
     * 更新资产库中ip端口白名单
     *
     * @return
     */
    @RequestMapping(value = "/batchUpdate", method = RequestMethod.GET)
    public Result batchUpdate() {
        List<Assetip> assetipList = new ArrayList<>();
        List<Assetport> assetPortList = new ArrayList<>();
        List<Ipwhitelist> ipwhitelistList = ipwhitelistService.findAll();
        ipwhitelistList.forEach(ipwhitelist -> {
            String ipwhitelistId = ipwhitelist.getId();
            String ip = ipwhitelist.getIp();
            Boolean ipCheckwhitelist = ipwhitelist.getCheckwhitelist();
            Boolean ipNotifywhitelist = ipwhitelist.getNotifywhitelist();
            Assetip assetip = assetipService.findByIpaddressv4AndPassivetimeIsNull(ip);
            //当前ip在数据库中
            if (!Objects.isNull(assetip)) {
                String assetIpId = assetip.getId();

                List<String> whitelistPortList = ipportwhitelistService.findAllPortByIpwhitelistid(ipwhitelistId);
                //没有白名单端口，直接更新ip
                if (whitelistPortList.isEmpty()) {
                    assetip.setCheckwhitelist(ipCheckwhitelist);
                    assetip.setAssetnotifywhitelist(ipNotifywhitelist);
                    assetipList.add(assetip);
                    //assetipService.update(assetip);
                } else {
                    //有白名单端口，ip白名单置空
                    if (ipCheckwhitelist) {
                        assetip.setCheckwhitelist(false);
                        assetipList.add(assetip);
                    }
                    if (ipNotifywhitelist) {
                        assetip.setAssetnotifywhitelist(false);
                        assetipList.add(assetip);
                    }
                    //assetipService.update(assetip);
                    //根据白名单端口，遍历资产库端口，如果白名单端口列表包含数据库端口，则判断白名单
                    //根据ip编号获取端口列表
                    List<Assetport> assetportList = assetportService.findAllByAssetipidAndDowntimeIsNull(assetIpId);
                    if (!assetportList.isEmpty()) {
                        assetportList.forEach(assetport -> {
                            String dbPort = assetport.getPort();
                            //当前端口在白名单whitelistPortList中，加白
                            if (whitelistPortList.contains(dbPort)) {
                                Ipportwhitelist ipportwhitelist = ipportwhitelistService.findByIpwhitelistidAndPort(ipwhitelistId, dbPort);
                                Boolean portCheckwhitelist = ipportwhitelist.getCheckwhitelist();
                                Boolean portNotifywhitelist = ipportwhitelist.getNotifywhitelist();
                                assetport.setCheckwhitelist(portCheckwhitelist);
                                assetport.setAssetnotifywhitelist(portNotifywhitelist);
                                assetPortList.add(assetport);
                            }
                        });
                    }
                }
            }
        });
        // 批量更新
        if (!assetPortList.isEmpty()) {
            assetportService.batchAdd(assetPortList);
        }
        //批量更新
        if (!assetipList.isEmpty()) {
            assetipService.batchAdd(assetipList);
        }
        return new Result(true, StatusCode.OK, "资产库ip端口白名单已在后台处理，请稍后查看");
    }
}

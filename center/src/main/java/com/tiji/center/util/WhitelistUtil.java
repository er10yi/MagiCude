package com.tiji.center.util;

import com.tiji.center.pojo.*;
import com.tiji.center.service.*;

import java.util.*;

/**
 * @author 贰拾壹
 * @create 2019-11-27 17:39
 */
public class WhitelistUtil {

    /**
     * 根据ip、ip端口进行加白，安全检测白名单，提醒白名单
     */
    public static void markIpWhitelist(AssetipService assetipService, AssetportService assetportService, IpwhitelistService ipwhitelistService, IpportwhitelistService ipportwhitelistService) {
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
                    assetipService.update(assetip);
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

                    //根据白名单端口，遍历资产库端口，如果白名单端口列表包含数据库端口，则判断白名单
                    //根据ip编号获取端口列表
                    List<Assetport> assetportList = assetportService.findAllByAssetipidAndDowntimeIsNull(assetIpId);
                    if (!assetportList.isEmpty()) {
//                        for (Assetport assetport : assetportList) {
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
        //批量更新
        if (!assetPortList.isEmpty()) {
            assetportService.batchAdd(assetPortList);
        }
        //批量更新
        if (!assetipList.isEmpty()) {
            assetipService.batchAdd(assetipList);
        }
    }

    /***
     *根据项目、项目和端口进行加白，安全检测白名单，提醒白名单
     */
    public static void markProjectInfoWhitelist(ProjectinfoService projectinfoService, AssetipService assetipService, AssetportService assetportService, ProjectportwhitelistService projectportwhitelistService) {
        List<Assetip> assetipList = new ArrayList<>();
        List<Projectinfo> projectinfoList = projectinfoService.findAll();
        List<Assetport> assetPortList = new ArrayList<>();
        projectinfoList.forEach(projectinfo -> {
            String projectinfoid = projectinfo.getId();
            Boolean projectCheckwhitelist = projectinfo.getCheckwhitelist();
            Boolean projectNotifywhitelist = projectinfo.getNotifywhitelist();

            List<String> whitelistProjectPortList = projectportwhitelistService.findAllPortByProjectinfoid(projectinfoid);
            //覆盖ip白名单，且没有白名单端口，直接更新当前项目的所有ip
            if (projectinfo.getOverrideipwhitelist() && whitelistProjectPortList.isEmpty()) {
                assetipService.updateByProjectinfoidAndCheckwhitelistAndAssetNotifywhitelist(projectinfoid, projectCheckwhitelist, projectNotifywhitelist);
            } else {
                //有白名单端口
                //查找当前项目的所有ip
                Map<String, String> searchMap = new HashMap<>();
                searchMap.put("projectinfoid", projectinfoid);
                //根据项目id获取ip列表
                List<Assetip> searchAssetIpList = assetipService.findSearch(searchMap);
                searchAssetIpList.forEach(assetip -> {
                    String assetIpId = assetip.getId();
                    //根据ip编号获取端口列表
                    List<Assetport> assetportList = assetportService.findAllByAssetipidAndDowntimeIsNull(assetIpId);
                    if (!assetportList.isEmpty()) {
                        assetportList.forEach(assetport -> {
                            //ip白名单置空
                            if (projectCheckwhitelist) {
                                assetip.setCheckwhitelist(false);
                                assetipList.add(assetip);
                            }
                            if (projectNotifywhitelist) {
                                assetip.setAssetnotifywhitelist(false);
                                assetipList.add(assetip);
                            }
                            String port = assetport.getPort();
                            //当前端口在白名单whitelistProjectPortList中，加白
                            if (whitelistProjectPortList.contains(port)) {
                                Projectportwhitelist projectportwhitelist = projectportwhitelistService.findByProjectinfoidAndPort(projectinfoid, port);
                                Boolean portCheckwhitelist = projectportwhitelist.getCheckwhitelist();
                                Boolean portNotifywhitelist = projectportwhitelist.getNotifywhitelist();
                                assetport.setCheckwhitelist(portCheckwhitelist);
                                assetport.setAssetnotifywhitelist(portNotifywhitelist);
                                assetPortList.add(assetport);
                            }
                        });
                    }
                });
            }
        });
        //批量更新
        if (!assetPortList.isEmpty()) {
            assetportService.batchAdd(assetPortList);
        }
        //批量更新
        if (!assetipList.isEmpty()) {
            assetipService.batchAdd(assetipList);
        }
    }

}

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
 * projectinfo控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/projectinfo")
public class ProjectinfoController {

    @Autowired
    private ProjectinfoService projectinfoService;

    @Autowired
    private ContactProjectinfoService contactProjectinfoService;
    @Autowired
    private ProjectportwhitelistService projectportwhitelistService;
    @Autowired
    private AssetipService assetipService;
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", projectinfoService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", projectinfoService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Autowired
    private ContactService contactService;

    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Projectinfo> pageList = projectinfoService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(projectinfo -> {
            StringBuilder stringBuilder = new StringBuilder();
            String departmentid = projectinfo.getDepartmentid();
            if (!StringUtils.isEmpty(departmentid)) {
                Department department = departmentService.findById(departmentid);
                if (!Objects.isNull(department)) {
                    projectinfo.setDepartmentid(department.getDepartmentname());
                }
            }
            String id = projectinfo.getId();
            List<ContactProjectinfo> contactProjectinfoList = contactProjectinfoService.findAllByProjectinfoid(id);
            contactProjectinfoList.forEach(contactProjectinfo -> {
                String contactid = contactProjectinfo.getContactid();
                Contact contact = contactService.findById(contactid);
                if (!Objects.isNull(contact)) {
                    stringBuilder.append(contact.getName()).append(" \n");
                }
            });
            projectinfo.setContact(stringBuilder.toString());
        });

        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Projectinfo>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", projectinfoService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param projectinfo
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Projectinfo projectinfo) {
        String departmentid = projectinfo.getDepartmentid();
        Projectinfo projectinfoInDb = projectinfoService.findByDepartmentidAndProjectname(departmentid, projectinfo.getProjectname());
        if (Objects.isNull(projectinfoInDb)) {
            String id = projectinfo.getId();
            if (Objects.isNull(id)) {
                id = idWorker.nextId() + "";
                projectinfo.setId(id);
            }
            projectinfoService.add(projectinfo);
            return new Result(true, StatusCode.OK, "增加成功", id);
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：部门和项目信息重复");
        }
    }

    /**
     * 修改
     *
     * @param projectinfo
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Projectinfo projectinfo, @PathVariable String id) {
        projectinfo.setId(id);
        projectinfoService.update(projectinfo);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        projectinfoService.deleteById(id);
        //删除项目信息的同时，删除与联系人关联
        contactProjectinfoService.deleteAllByProjectinfoid(id);
        //删除项目信息的同时，删除项目信息-端口白名单
        projectportwhitelistService.deleteAllByProjectinfoid(id);
        //将资产ip的projectinfoid置空
        assetipService.updateAssetipByProjectinfoidSetProjectinfoid2Null(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        projectinfoService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除项目信息的同时，删除与联系人关联
            contactProjectinfoService.deleteAllByProjectinfoid(id);
            //删除项目信息的同时，删除项目信息-端口白名单
            projectportwhitelistService.deleteAllByProjectinfoid(id);
            //将资产ip的projectinfoid置空
            assetipService.updateAssetipByProjectinfoidSetProjectinfoid2Null(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入项目信息端口白名单
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
        if (!"text/plain".equals(fileContentType)) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }
        String fileOriginalFilename = file.getOriginalFilename();
        assert fileOriginalFilename != null;
        String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf(".") + 1);
        if (!"txt".equals(suffix)) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }

        String line;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            while ((line = bf.readLine()) != null) {
                String projectInfoName = line.split("\\|")[0];
                String port = line.split("\\|")[1];
                boolean checkWhitelist = "1".equals(line.split("\\|")[2]);
                boolean notifyWhitelist = "1".equals(line.split("\\|")[3]);
                //端口为空，projectInfo白名单
                if (Objects.isNull(port) || port.isEmpty()) {
                    Projectinfo projectInfo = projectinfoService.findByProjectname(projectInfoName);
                    //不在数据库中
                    if (Objects.isNull(projectInfo)) {
                        //此时没有部门，需要手动维护
                        projectinfoService.add(new Projectinfo(idWorker.nextId() + "", null, projectInfoName, checkWhitelist, notifyWhitelist, new Date(), false));
                    } else {
                        projectInfo.setCheckwhitelist(checkWhitelist);
                        projectInfo.setNotifywhitelist(notifyWhitelist);
                        projectinfoService.update(projectInfo);
                        //删除端口白名单
                        projectportwhitelistService.deleteAllByProjectinfoid(projectInfo.getId());
                    }
                } else {
                    //端口白名单
                    Projectinfo projectInfo = projectinfoService.findByProjectname(projectInfoName);
                    //不在数据库中
                    String projectInfoId = idWorker.nextId() + "";
                    if (Objects.isNull(projectInfo)) {
                        projectinfoService.add(new Projectinfo(projectInfoId, null, projectInfoName, false, false, new Date(), false));
                        projectportwhitelistService.add(new Projectportwhitelist(idWorker.nextId() + "", projectInfoId, port, checkWhitelist, notifyWhitelist));
                    } else {
                        //在数据库中，新增端口
                        projectInfoId = projectInfo.getId();
                        //如果是端口白名单，则projectInfo白名单置空
                        if (checkWhitelist) {
                            projectInfo.setCheckwhitelist(false);
                            projectinfoService.add(projectInfo);
                        }
                        if (notifyWhitelist) {
                            projectInfo.setNotifywhitelist(false);
                            projectinfoService.add(projectInfo);
                        }
                        Projectportwhitelist projectportwhitelist = projectportwhitelistService.findByProjectinfoidAndPort(projectInfoId, port);
                        if (Objects.isNull(projectportwhitelist)) {
                            projectportwhitelistService.add(new Projectportwhitelist(idWorker.nextId() + "", projectInfoId, port, checkWhitelist, notifyWhitelist));
                        } else {
                            projectportwhitelist.setCheckwhitelist(checkWhitelist);
                            projectportwhitelist.setNotifywhitelist(notifyWhitelist);
                            projectportwhitelistService.update(projectportwhitelist);
                        }
                    }
                }

            }
        } catch (IOException ignored) {
        }
        return new Result(true, StatusCode.OK, "项目信息端口白名单已上传处理，请稍后查看");

    }


    /**
     * 更新资产库中项目信息端口白名单
     *
     * @return
     */
    @RequestMapping(value = "/batchUpdate", method = RequestMethod.GET)
    public Result batchUpdate() {

        List<Projectinfo> projectinfoList = projectinfoService.findAll();
        List<Assetip> assetipList = new ArrayList<>();
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
                    //ip白名单置空
                    if (projectCheckwhitelist) {
                        assetip.setCheckwhitelist(false);
                        assetipList.add(assetip);
                    }
                    if (projectNotifywhitelist) {
                        assetip.setAssetnotifywhitelist(false);
                        assetipList.add(assetip);
                    }

                    String assetIpId = assetip.getId();
                    //根据ip编号获取端口列表
                    List<Assetport> assetportList = assetportService.findAllByAssetipidAndDowntimeIsNull(assetIpId);
                    if (!assetportList.isEmpty()) {
                        assetportList.forEach(assetport -> {
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
        return new Result(true, StatusCode.OK, "项目信息端口白名单已在后台处理，请稍后查看");
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findByAssetIpIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", projectinfoService.findByIds(ids));
    }

    /**
     * 根据id查询联系人
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/contact/{id}", method = RequestMethod.GET)
    public Result findAllContactById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", projectinfoService.findAllContactById(id));
    }

    /**
     * 根据项目信息id和联系人id，新增关联
     *
     * @param projectinfoIdAndContactId
     * @return
     */
    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public Result addContact(@RequestBody String[] projectinfoIdAndContactId) {
        String projectinfoId = projectinfoIdAndContactId[0];
        String contactId = projectinfoIdAndContactId[1];
        if(!StringUtils.isEmpty((projectinfoId))&&!StringUtils.isEmpty((contactId))){
            ContactProjectinfo contactProjectinfo = contactProjectinfoService.findByContactidAndProjectinfoid(contactId, projectinfoId);
            if (Objects.isNull(contactProjectinfo)) {
                projectinfoService.addContact(projectinfoIdAndContactId);
                return new Result(true, StatusCode.OK, "新增成功");
            } else {
                return new Result(false, StatusCode.ERROR, "已存在负责人");
            }
        }
        return new Result(false, StatusCode.ERROR, "新增失败");
    }

    /**
     * 根据contacid和projectinfoid删除
     *
     * @param ids
     */

    @RequestMapping(value = "/delcontact", method = RequestMethod.POST)
    public Result deleteContact(@RequestBody List<String> ids) {
        String id = ids.get(1);
        String contactid = ids.get(0);
        contactProjectinfoService.deleteByContactidAndProjectinfoid(id, contactid);
        return new Result(true, StatusCode.OK, "删除成功");
    }

}

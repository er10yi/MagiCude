package com.tiji.center.controller;

import com.tiji.center.pojo.*;
import com.tiji.center.service.*;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * pluginconfig控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/pluginconfig")
public class PluginconfigController {

    @Autowired
    private PluginconfigService pluginconfigService;

    @Autowired
    private IdWorker idWorker;


    @Autowired
    private PluginassetserviceService pluginassetserviceService;
    @Autowired
    private PluginassetversionService pluginassetversionService;
    @Autowired
    private VulnkeywordService vulnkeywordService;
    @Autowired
    private VulnpluginconfigService vulnpluginconfigService;
    @Autowired
    private VulnService vulnService;
    @Autowired
    private TaskpluginconfigService taskpluginconfigService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", pluginconfigService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", pluginconfigService.findById(id));
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
        Page<Pluginconfig> pageList = pluginconfigService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Pluginconfig>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", pluginconfigService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param pluginconfig
     */
    @PostMapping
    public Result add(@RequestBody Pluginconfig pluginconfig) {
        if (Objects.isNull(pluginconfig.getTimeout()) || pluginconfig.getTimeout().isEmpty()) {
            pluginconfig.setTimeout("0");
        }
        pluginconfigService.add(pluginconfig);
        return new Result(true, StatusCode.OK, "增加成功", pluginconfig.getId());
    }

    /**
     * 修改
     *
     * @param pluginconfig
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Pluginconfig pluginconfig, @PathVariable String id) {
        pluginconfig.setId(id);
        pluginconfigService.update(pluginconfig);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        pluginconfigService.deleteById(id);

        //删除资产服务
        pluginassetserviceService.deleteAllByPluginconfigid(id);

        //删除资产版本
        pluginassetversionService.deleteAllByPluginconfigid(id);

        //删除漏洞关键词
        vulnkeywordService.deleteAllByPluginconfigid(id);
        //删除漏洞插件配置关联
        vulnpluginconfigService.deleteAllByPluginconfigid(id);

        //删除任务已启用插件
        taskpluginconfigService.deleteAllByPluginconfigid(id);

        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */

    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        pluginconfigService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除资产服务
            pluginassetserviceService.deleteAllByPluginconfigid(id);

            //删除资产版本
            pluginassetversionService.deleteAllByPluginconfigid(id);

            //删除漏洞关键词
            vulnkeywordService.deleteAllByPluginconfigid(id);
            //删除漏洞插件配置关联
            vulnpluginconfigService.deleteAllByPluginconfigid(id);

            //删除任务已启用插件
            taskpluginconfigService.deleteAllByPluginconfigid(id);

        });
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入插件配置（selfd不包含代码）
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
                String[] pluginConfigArr = line.split("\\|");
                String pluginName = pluginConfigArr[0];
                String pluginArgs = pluginConfigArr[1];
                String pluginRisk = pluginConfigArr[2];
                String pluginType = pluginConfigArr[3];
                String pluginValidateType = pluginConfigArr[4];
                String pluginTimeout = pluginConfigArr[5];

                String assetServiceName = pluginConfigArr[6];
                String assetVersionName = pluginConfigArr[7];
                String vulnKeywordName = pluginConfigArr[8];
                String vulnName = pluginConfigArr[9];

                String pluginConfigId;
                Pluginconfig dbPluginConfig = pluginconfigService.findByNameAndType(pluginName, pluginType);
                if (Objects.isNull(dbPluginConfig)) {
                    //插件不存在，新增一个插件
                    pluginConfigId = idWorker.nextId() + "";
                    if (Objects.isNull(pluginTimeout) || pluginTimeout.isEmpty()) {
                        pluginTimeout = "0";
                    }
                    pluginconfigService.add(new Pluginconfig(pluginConfigId, pluginName, pluginArgs, pluginRisk, pluginType, pluginValidateType, pluginTimeout, null));

                    if (!assetServiceName.isEmpty()) {
                        pluginassetserviceService.add(new Pluginassetservice(idWorker.nextId() + "", pluginConfigId, assetServiceName));
                    }
                    //资产版本
                    if (!assetVersionName.isEmpty()) {
                        pluginassetversionService.add(new Pluginassetversion(idWorker.nextId() + "", pluginConfigId, assetVersionName));
                    }
                    //漏洞关键字
                    if (!vulnKeywordName.isEmpty()) {
                        vulnkeywordService.add(new Vulnkeyword(idWorker.nextId() + "", pluginConfigId, vulnKeywordName));
                    }
                    //插件与漏洞关联
                    //没有明确漏洞名称，直接关联默认漏洞
                    Vuln vulnInDb;
                    if ("默认".equals(vulnName)) {
                        vulnInDb = vulnService.findByName(vulnName + pluginRisk);
                    } else {
                        //有漏洞名称
                        vulnInDb = vulnService.findByName(vulnName);
                        //漏洞名称不在漏洞wiki，还是关联默认漏洞
                        if (Objects.isNull(vulnInDb)) {
                            vulnInDb = vulnService.findByName("默认" + pluginRisk);
                        }
                    }
                    vulnpluginconfigService.add(new Vulnpluginconfig(idWorker.nextId() + "", vulnInDb.getId(), pluginConfigId));

                } else {
                    //插件存在，服务、版本、关键字不存在则新增，存在不做处理
                    pluginConfigId = dbPluginConfig.getId();
                    if (!assetServiceName.isEmpty() && Objects.isNull(pluginassetserviceService.findByPluginconfigidAndService(pluginConfigId, assetServiceName))) {
                        pluginassetserviceService.add(new Pluginassetservice(idWorker.nextId() + "", pluginConfigId, assetServiceName));
                    }
                    //资产版本
                    if (!assetVersionName.isEmpty() && Objects.isNull(pluginassetversionService.findByPluginconfigidAndService(pluginConfigId, assetVersionName))) {
                        pluginassetversionService.add(new Pluginassetversion(idWorker.nextId() + "", pluginConfigId, assetVersionName));
                    }
                    //漏洞关键字
                    if (!vulnKeywordName.isEmpty() && Objects.isNull(vulnkeywordService.findByPluginconfigidAndService(pluginConfigId, vulnKeywordName))) {
                        vulnkeywordService.add(new Vulnkeyword(idWorker.nextId() + "", pluginConfigId, vulnKeywordName));
                    }
                    //插件与漏洞关联
                    //已明确漏洞名称，更新关联漏洞
                    if (!"默认".equals(vulnName)) {
                        Vuln vulnDefault = vulnService.findByName("默认" + pluginRisk);
                        Vuln vulnInDb = vulnService.findByName(vulnName);
                        if (!Objects.isNull(vulnInDb)) {
                            Vulnpluginconfig vulnpluginconfig = vulnpluginconfigService.findByVulnidAndPluginconfigid(vulnDefault.getId(), pluginConfigId);
                            if (!Objects.isNull(vulnpluginconfig)) {
                                vulnpluginconfig.setVulnid(vulnInDb.getId());
                                vulnpluginconfigService.update(vulnpluginconfig);
                            }

                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }

        return new Result(true, StatusCode.OK, "插件配置已上传处理，请稍后查看");

    }


    /**
     * 批量导入包含插件代码的selfd插件配置
     */
    @RequestMapping(value = "/batchAddSelfd", method = RequestMethod.POST)
    public Result batchAddSelfd(@RequestParam("file") MultipartFile[] multipartFiles) throws IOException {
        StringBuilder sb = new StringBuilder();
        int FilesLength = multipartFiles.length;
        if (FilesLength == 0) {
            return new Result(false, StatusCode.ERROR, "文件为空");
        }
        if (FilesLength > 51) {
            return new Result(false, StatusCode.ERROR, "文件超50个");
        }
        for (MultipartFile file : multipartFiles) {
            String originalFilename = file.getOriginalFilename();
            if (file.getSize() == 0) {
                sb.append(originalFilename).append("文件为空").append(";");
            }
            long fileSize = file.getSize();
            if (fileSize / 1024 / 1024 > 3) {
                sb.append(originalFilename).append("文件大小不能超过 3M").append(";");
            }
            String fileContentType = file.getContentType();
            assert fileContentType != null;
            if (!"text/plain".equals(fileContentType)) {
                sb.append(originalFilename).append("文件只能是 txt 格式").append(";");
            }
            String fileOriginalFilename = file.getOriginalFilename();
            assert fileOriginalFilename != null;
            String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf(".") + 1);
            if (!"txt".equals(suffix)) {
                sb.append(originalFilename).append("文件只能是 txt 格式");
            }
        }
        if (sb.length() != 0) {
            return new Result(false, StatusCode.ERROR, sb.toString());
        }


        for (MultipartFile file : multipartFiles) {

            StringBuilder codeStringBuilder = new StringBuilder();
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    codeStringBuilder.append(line).append("\n");
                }
            } catch (IOException ignored) {
            }
            String[] pluginConfigArr = codeStringBuilder.toString().split("\\|");
            String pluginName = pluginConfigArr[0];
            String pluginArgs = pluginConfigArr[1];
            String pluginRisk = pluginConfigArr[2];
            String pluginType = pluginConfigArr[3];
            String pluginValidateType = pluginConfigArr[4];
            String pluginTimeout = pluginConfigArr[5];

            String assetServiceName = pluginConfigArr[6];
            String assetVersionName = pluginConfigArr[7];
            String vulnKeywordName = pluginConfigArr[8];
            String vulnName = pluginConfigArr[9];
            String pluginCode = null;
            if (pluginConfigArr.length == 11) {
                pluginCode = pluginConfigArr[10];
            }

            String pluginConfigId;
            Pluginconfig dbPluginConfig = pluginconfigService.findByNameAndType(pluginName, pluginType);
            if (Objects.isNull(dbPluginConfig)) {
                //插件不存在，新增一个插件
                pluginConfigId = idWorker.nextId() + "";
                if (Objects.isNull(pluginTimeout) || pluginTimeout.isEmpty()) {
                    pluginTimeout = "0";
                }
                pluginconfigService.add(new Pluginconfig(pluginConfigId, pluginName, pluginArgs, pluginRisk, pluginType, pluginValidateType, pluginTimeout, pluginCode));

                if (!assetServiceName.isEmpty()) {
                    pluginassetserviceService.add(new Pluginassetservice(idWorker.nextId() + "", pluginConfigId, assetServiceName));
                }
                //资产版本
                if (!assetVersionName.isEmpty()) {
                    pluginassetversionService.add(new Pluginassetversion(idWorker.nextId() + "", pluginConfigId, assetVersionName));
                }
                //漏洞关键字
                if (!vulnKeywordName.isEmpty()) {
                    vulnkeywordService.add(new Vulnkeyword(idWorker.nextId() + "", pluginConfigId, vulnKeywordName));
                }
                //插件与漏洞关联
                //没有明确漏洞名称，直接关联默认漏洞
                Vuln vulnInDb;
                if ("默认".equals(vulnName)) {
                    vulnInDb = vulnService.findByName(vulnName + pluginRisk);
                } else {
                    //有漏洞名称
                    vulnInDb = vulnService.findByName(vulnName);
                    //漏洞名称不在漏洞wiki，还是关联默认漏洞
                    if (Objects.isNull(vulnInDb)) {
                        vulnInDb = vulnService.findByName("默认" + pluginRisk);
                    }
                }
                vulnpluginconfigService.add(new Vulnpluginconfig(idWorker.nextId() + "", vulnInDb.getId(), pluginConfigId));

            } else {
                //插件存在，服务、版本、关键字不存在则新增，存在不做处理
                pluginConfigId = dbPluginConfig.getId();
                if (!assetServiceName.isEmpty() && Objects.isNull(pluginassetserviceService.findByPluginconfigidAndService(pluginConfigId, assetServiceName))) {
                    pluginassetserviceService.add(new Pluginassetservice(idWorker.nextId() + "", pluginConfigId, assetServiceName));
                }
                //资产版本
                if (!assetVersionName.isEmpty() && Objects.isNull(pluginassetversionService.findByPluginconfigidAndService(pluginConfigId, assetVersionName))) {
                    pluginassetversionService.add(new Pluginassetversion(idWorker.nextId() + "", pluginConfigId, assetVersionName));
                }
                //漏洞关键字
                if (!vulnKeywordName.isEmpty() && Objects.isNull(vulnkeywordService.findByPluginconfigidAndService(pluginConfigId, vulnKeywordName))) {
                    vulnkeywordService.add(new Vulnkeyword(idWorker.nextId() + "", pluginConfigId, vulnKeywordName));
                }
                //插件与漏洞关联
                //已明确漏洞名称，更新关联漏洞
                if (!"默认".equals(vulnName)) {
                    Vuln vulnDefault = vulnService.findByName("默认" + pluginRisk);
                    Vuln vulnInDb = vulnService.findByName(vulnName);
                    if (!Objects.isNull(vulnInDb)) {
                        Vulnpluginconfig vulnpluginconfig = vulnpluginconfigService.findByVulnidAndPluginconfigid(vulnDefault.getId(), pluginConfigId);
                        if (!Objects.isNull(vulnpluginconfig)) {
                            vulnpluginconfig.setVulnid(vulnInDb.getId());
                            vulnpluginconfigService.update(vulnpluginconfig);
                        }

                    }
                }

                //插件代码
                if (!Objects.isNull(pluginCode)) {
                    if (!pluginCode.isEmpty() && (Objects.isNull(dbPluginConfig.getPlugincode())) || dbPluginConfig.getPlugincode().isEmpty()) {
                        dbPluginConfig.setPlugincode(pluginCode);
                        pluginconfigService.update(dbPluginConfig);
                    }
                }

            }
        }
        return new Result(true, StatusCode.OK, "selfd插件配置已上传处理，请稍后查看");

    }


}

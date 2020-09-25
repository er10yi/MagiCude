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
 * vuln控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/vuln")
public class VulnController {

    @Autowired
    private VulnService vulnService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategorytopService categorytopService;
    @Autowired
    private CategorysecondService categorysecondService;
    @Autowired
    private DemocodeService democodeService;
    @Autowired
    private SolutionService solutionService;
    @Autowired
    private VulnpluginconfigService vulnpluginconfigService;

    @Autowired
    private PluginconfigService pluginconfigService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", vulnService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", vulnService.findById(id));
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
        Page<Vuln> pageList = vulnService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Vuln>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", vulnService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param vuln
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Vuln vuln) {
        String name = vuln.getName();
        Vuln vulnInDb = vulnService.findByName(name);
        if (Objects.isNull(vulnInDb)) {
            if (Objects.isNull(vuln.getId())) {
                String vulnId = idWorker.nextId() + "";
                vuln.setId(vulnId);
                vulnService.add(vuln);
                return new Result(true, StatusCode.OK, "增加成功", vulnId);
            } else {
                vulnService.add(vuln);
                return new Result(true, StatusCode.OK, "增加成功");
            }
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：名称重复");
        }
    }

    /**
     * 修改
     *
     * @param vuln
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Vuln vuln, @PathVariable String id) {
        vuln.setId(id);
        vulnService.update(vuln);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        vulnService.deleteById(id);
        //删除示例代码
        democodeService.deleteAllByVulnId(id);
        //删除解决方案
        solutionService.deleteAllByVulnId(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        vulnService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除示例代码
            democodeService.deleteAllByVulnId(id);
            //删除解决方案
            solutionService.deleteAllByVulnId(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 批量导入漏洞分类
     */
    @RequestMapping(value = "/category", method = RequestMethod.POST)
    public Result batchAddCategory(@RequestParam("file") MultipartFile file) throws IOException {
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
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {
            while ((line = bf.readLine()) != null) {
                if (line.contains(":")) {
                    //一级分类
                    String top = line.split(":")[0];
                    String second;
                    String[] secondArr;
                    String topId = idWorker.nextId() + "";
                    if (line.contains("|")) {
                        second = line.split(":")[1];
                        secondArr = second.split("\\|");
                    } else {
                        //没有二级分类，当前一级分类就是二级分类
                        secondArr = new String[]{top};
                    }
                    //top
                    Categorytop categorytop = categorytopService.findByName(top);
                    if (Objects.isNull(categorytop)) {
                        categorytopService.add(new Categorytop(topId, top));
                    } else {
                        topId = categorytop.getId();
                    }
                    if (secondArr.length != 0) {
                        //second
                        for (String secondName : secondArr) {
                            Categorysecond categorysecond = categorysecondService.findByName(secondName);
                            //没有second直接新增
                            if (Objects.isNull(categorysecond)) {
                                String secondId = idWorker.nextId() + "";
                                categorysecondService.add(new Categorysecond(secondId, topId, secondName));
                            } else {
                                //有second，修改topid
                                categorysecond.setCategorytopid(topId);
                                categorysecondService.update(categorysecond);
                            }
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return new Result(true, StatusCode.OK, "漏洞分类已上传处理，请稍后查看");

    }

    /**
     * 批量导入漏洞详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public Result batchAddDetail(@RequestParam("file") MultipartFile file) throws IOException {
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

                String[] vulnDetailArr = line.split("\\|");
                String name = vulnDetailArr[0];
                String desc = vulnDetailArr[1];
                String risk = vulnDetailArr[2];
                String referer = vulnDetailArr[3];
                String impact = vulnDetailArr[4];

                String top = vulnDetailArr[5];

                String second = vulnDetailArr[6];

                String vulnDemoCode = vulnDetailArr[7];
                String poc = vulnDetailArr[8];

                String solution = vulnDetailArr[9];
                String codedemo = vulnDetailArr[10];
                String configdemo = vulnDetailArr[11];

                String pulginName = "";
                if (vulnDetailArr.length > 12) {
                    pulginName = vulnDetailArr[12];
                }
                String[] pluginArr = new String[0];
                if (pulginName.length() != 0) {
                    //多个插件
                    if (pulginName.contains("、")) {
                        pluginArr = pulginName.split("、");
                    } else {
                        //只有一个插件
                        pluginArr = new String[]{pulginName};
                    }
                }
                Categorytop categorytopByName = categorytopService.findByName(top);
                //当前分类不在一级分类中，新增一个分类
                String topId = idWorker.nextId() + "";
                if (Objects.isNull(categorytopByName) && !top.isEmpty()) {
                    categorytopService.add(new Categorytop(topId, top));
                } else {
                    topId = categorytopByName.getId();
                }

                Categorysecond categorysecondByName = categorysecondService.findByName(second);
                //当前分类不在二级分类中，新增一个分类
                String secondId;
                if (Objects.isNull(categorysecondByName) && !second.isEmpty()) {
                    secondId = idWorker.nextId() + "";
                    categorysecondService.add(new Categorysecond(secondId, topId, second));
                } else if (!Objects.isNull(categorysecondByName)) {
                    secondId = categorysecondByName.getId();
                } else {
                    secondId = idWorker.nextId() + "";
                }

                //当前漏洞不在数据库中，新增
                Vuln vuln = vulnService.findByName(name);
                if (Objects.isNull(vuln)) {
                    String vulnId = idWorker.nextId() + "";
                    vulnService.add(new Vuln(vulnId, secondId, name, desc, risk, referer, impact));

                    if (!vulnDemoCode.isEmpty()) {
                        democodeService.add(new Democode(idWorker.nextId() + "", vulnId, vulnDemoCode, poc));
                    }

                    solutionService.add(new Solution(idWorker.nextId() + "", vulnId, solution, codedemo, configdemo));
                    //有插件，添加关联
                    if (pluginArr.length != 0) {
                        for (String plugin : pluginArr) {
                            Pluginconfig pluginconfigByName = pluginconfigService.findByName(plugin);
                            if (!Objects.isNull(pluginconfigByName)) {
                                vulnpluginconfigService.add(new Vulnpluginconfig(idWorker.nextId() + "", vulnId, pluginconfigByName.getId()));
                            }
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return new Result(true, StatusCode.OK, "漏洞详情已上传处理，请稍后查看");

    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findByAssetIpIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", vulnService.findByIds(ids));
    }
}

package com.tiji.center.controller;

import com.tiji.center.pojo.Titlewhitelist;
import com.tiji.center.service.TitlewhitelistService;
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
 * titlewhitelist控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/titlewhitelist")
public class TitlewhitelistController {

    @Autowired
    private TitlewhitelistService titlewhitelistService;
    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", titlewhitelistService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", titlewhitelistService.findById(id));
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
        Page<Titlewhitelist> pageList = titlewhitelistService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Titlewhitelist>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", titlewhitelistService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param titlewhitelist
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Titlewhitelist titlewhitelist) {
        String title = titlewhitelist.getTitle();
        Titlewhitelist titleInDb = titlewhitelistService.findByTitle(title);
        if (Objects.isNull(titleInDb)) {
            titlewhitelistService.add(titlewhitelist);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：标题重复");
        }
    }

    /**
     * 修改
     *
     * @param titlewhitelist
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Titlewhitelist titlewhitelist, @PathVariable String id) {
        titlewhitelist.setId(id);
        titlewhitelistService.update(titlewhitelist);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        titlewhitelistService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        titlewhitelistService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 批量导入标题白名单
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
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {
            while ((line = bf.readLine()) != null) {
                Titlewhitelist titlewhitelist = titlewhitelistService.findByTitle(line);
                if (Objects.isNull(titlewhitelist)) {
                    titlewhitelistService.add(new Titlewhitelist(idWorker.nextId() + "", line));
                }
            }
        } catch (IOException ignored) {
        }
        return new Result(true, StatusCode.OK, "标题白名单已上传处理，请稍后查看");

    }
}

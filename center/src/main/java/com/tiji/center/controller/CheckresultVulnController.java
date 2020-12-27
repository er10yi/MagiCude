package com.tiji.center.controller;

import com.tiji.center.pojo.Checkresult;
import com.tiji.center.pojo.CheckresultVuln;
import com.tiji.center.pojo.Vuln;
import com.tiji.center.service.CheckresultService;
import com.tiji.center.service.CheckresultVulnService;
import com.tiji.center.service.VulnService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * checkresultVuln控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/checkresultVuln")
public class CheckresultVulnController {

    @Autowired
    private CheckresultVulnService checkresultVulnService;
    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private VulnService vulnService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", checkresultVulnService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultVulnService.findById(id));
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
        Page<CheckresultVuln> pageList = checkresultVulnService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(checkresultVuln -> {
            String vulnid = checkresultVuln.getVulnid();
            String checkresultid = checkresultVuln.getCheckresultid();
            if (!StringUtils.isEmpty(vulnid)) {
                Vuln vuln = vulnService.findById(vulnid);
                if (!Objects.isNull(vuln)) {
                    checkresultVuln.setVulnid(vuln.getName());
                }
            }
            if (!StringUtils.isEmpty(checkresultid)) {
                Checkresult checkresult = checkresultService.findById(checkresultid);
                if (!Objects.isNull(checkresult)) {
                    checkresultVuln.setCheckresultid(checkresult.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<CheckresultVuln>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", checkresultVulnService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param checkresultVuln
     */
    @PostMapping
    public Result add(@RequestBody CheckresultVuln checkresultVuln) {
        checkresultVulnService.add(checkresultVuln);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param checkresultVuln
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody CheckresultVuln checkresultVuln, @PathVariable String id) {
        checkresultVuln.setId(id);
        checkresultVulnService.update(checkresultVuln);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        checkresultVulnService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody String[] ids) {
        for (String id : ids) {
            checkresultVulnService.deleteById(id);
        }
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
        return new Result(true, StatusCode.OK, "查询成功", checkresultVulnService.findByIds(ids));
    }

}

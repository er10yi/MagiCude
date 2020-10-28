package com.tiji.center.controller;

import com.tiji.center.pojo.Solution;
import com.tiji.center.pojo.Vuln;
import com.tiji.center.service.SolutionService;
import com.tiji.center.service.VulnService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * solution控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/solution")
public class SolutionController {

    @Autowired
    private SolutionService solutionService;
    @Autowired
    private VulnService vulnService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", solutionService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", solutionService.findById(id));
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
        Page<Solution> pageList = solutionService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(Solution -> {
            String vulnid = Solution.getVulnid();
            if (!StringUtils.isEmpty(vulnid)) {
                Vuln vuln = vulnService.findById(vulnid);
                if (!Objects.isNull(vuln)) {
                    Solution.setVulnid(vuln.getName());
                }
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Solution>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", solutionService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param solution
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Solution solution) {
        solutionService.add(solution);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param solution
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Solution solution, @PathVariable String id) {
        solution.setId(id);
        solutionService.update(solution);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        solutionService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        solutionService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据vulnId查询
     *
     * @param vulnId vulnId
     * @return
     */
    @RequestMapping(value = "/vuln/{vulnId}", method = RequestMethod.GET)
    public Result findAllByVulnId(@PathVariable String vulnId) {
        return new Result(true, StatusCode.OK, "查询成功", solutionService.findAllByVulnId(vulnId));
    }


}

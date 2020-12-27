package com.tiji.center.controller;

import com.tiji.center.pojo.Categorysecond;
import com.tiji.center.pojo.Categorytop;
import com.tiji.center.service.CategorysecondService;
import com.tiji.center.service.CategorytopService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * categorytop控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/categorytop")
public class CategorytopController {

    @Autowired
    private CategorytopService categorytopService;
    @Autowired
    private CategorysecondService categorysecondService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", categorytopService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", categorytopService.findById(id));
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
        Page<Categorytop> pageList = categorytopService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Categorytop>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", categorytopService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param categorytop
     */
    @PostMapping
    public Result add(@RequestBody Categorytop categorytop) {
        String categorytopName = categorytop.getName();
        Categorytop categorytopInDb = categorytopService.findByName(categorytopName);
        if (Objects.isNull(categorytopInDb)) {
            categorytopService.add(categorytop);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：名称重复");
        }

    }

    /**
     * 修改
     *
     * @param categorytop
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Categorytop categorytop, @PathVariable String id) {
        categorytop.setId(id);
        categorytopService.update(categorytop);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        categorytopService.deleteById(id);
        //同时把second的topid置空
        Categorysecond categorysecond = categorysecondService.findByCategorytopid(id);
        if (!Objects.isNull(categorysecond)) {
            categorysecond.setCategorytopid(null);
            categorysecondService.update(categorysecond);
        }
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @PostMapping(value = "/deleteids")
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        categorysecondService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}

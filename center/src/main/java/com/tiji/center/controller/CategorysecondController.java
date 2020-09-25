package com.tiji.center.controller;

import com.tiji.center.pojo.Categorysecond;
import com.tiji.center.service.CategorysecondService;
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
 * categorysecond控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/categorysecond")
public class CategorysecondController {

    @Autowired
    private CategorysecondService categorysecondService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", categorysecondService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", categorysecondService.findById(id));
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
        Page<Categorysecond> pageList = categorysecondService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Categorysecond>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", categorysecondService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param categorysecond
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Categorysecond categorysecond) {
        //categorysecondService.add(categorysecond);
        //return new Result(true, StatusCode.OK,"增加成功");
        String categorysecondName = categorysecond.getName();
        Categorysecond categorysecondInDb = categorysecondService.findByName(categorysecondName);
        if (Objects.isNull(categorysecondInDb)) {
            categorysecondService.add(categorysecond);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：二级分类名称重复");
        }
    }

    /**
     * 修改
     *
     * @param categorysecond
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Categorysecond categorysecond, @PathVariable String id) {
        categorysecond.setId(id);
        categorysecondService.update(categorysecond);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        categorysecondService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        categorysecondService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findByCategorysecondIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", categorysecondService.findByIds(ids));
    }
}

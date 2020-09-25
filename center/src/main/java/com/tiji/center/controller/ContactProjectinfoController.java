package com.tiji.center.controller;

import com.tiji.center.pojo.ContactProjectinfo;
import com.tiji.center.service.ContactProjectinfoService;
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
 * contactProjectinfo控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/contactProjectinfo")
public class ContactProjectinfoController {

    @Autowired
    private ContactProjectinfoService contactProjectinfoService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", contactProjectinfoService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", contactProjectinfoService.findById(id));
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
        Page<ContactProjectinfo> pageList = contactProjectinfoService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<ContactProjectinfo>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", contactProjectinfoService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param contactProjectinfo
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody ContactProjectinfo contactProjectinfo) {
        ContactProjectinfo contactProjectinfoInDb = contactProjectinfoService.findByContactidAndProjectinfoid(contactProjectinfo.getContactid(), contactProjectinfo.getProjectinfoid());
        if (Objects.isNull(contactProjectinfoInDb)) {
            contactProjectinfoService.add(contactProjectinfo);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：联系人和项目信息重复");
        }
    }

    /**
     * 修改
     *
     * @param contactProjectinfo
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody ContactProjectinfo contactProjectinfo, @PathVariable String id) {
        contactProjectinfo.setId(id);
        contactProjectinfoService.update(contactProjectinfo);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        contactProjectinfoService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        contactProjectinfoService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据projectinfoid查询
     *
     * @param projectinfoid projectinfoid
     * @return
     */
    @RequestMapping(value = "/projectinfo/{projectinfoid}", method = RequestMethod.GET)
    public Result findAllByProjectinfoid(@PathVariable String projectinfoid) {
        return new Result(true, StatusCode.OK, "查询成功", contactProjectinfoService.findAllByProjectinfoid(projectinfoid));
    }
}

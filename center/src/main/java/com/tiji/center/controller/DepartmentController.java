package com.tiji.center.controller;

import com.tiji.center.pojo.Contact;
import com.tiji.center.pojo.ContactProjectinfo;
import com.tiji.center.pojo.Department;
import com.tiji.center.pojo.Projectinfo;
import com.tiji.center.service.ContactProjectinfoService;
import com.tiji.center.service.ContactService;
import com.tiji.center.service.DepartmentService;
import com.tiji.center.service.ProjectinfoService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * department控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private IdWorker idWorker;
    /**
     * 批量导入部门项目信息联系人
     */
    @Autowired
    private ProjectinfoService projectinfoService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactProjectinfoService contactProjectinfoService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", departmentService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", departmentService.findById(id));
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
        Page<Department> pageList = departmentService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Department>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", departmentService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param department
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Department department) {
        String departmentname = department.getDepartmentname();
        Department departmentInDb = departmentService.findByDepartmentname(departmentname);
        if (Objects.isNull(departmentInDb)) {
            departmentService.add(department);
            return new Result(true, StatusCode.OK, "增加成功");
        } else {
            return new Result(false, StatusCode.ERROR, "增加失败：部门名称重复");
        }
    }

    /**
     * 修改
     *
     * @param department
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Department department, @PathVariable String id) {
        department.setId(id);
        departmentService.update(department);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        departmentService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        departmentService.deleteAllByIds(ids);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @RequestMapping(value = "/batchAdd", method = RequestMethod.POST)
    public Result batchAdd(@RequestParam("file") MultipartFile file) {
        if (Objects.isNull(file) || file.getSize() == 0) {
            return new Result(false, StatusCode.ERROR, "文件为空");
        }
        long fileSize = file.getSize();
        if (fileSize / 1024 / 1024 > 3) {
            return new Result(false, StatusCode.ERROR, "文件大小不能超过 3M");
        }
        String fileContentType = file.getContentType();
        assert fileContentType != null;
        if (!fileContentType.equals("text/plain")) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }
        String fileOriginalFilename = file.getOriginalFilename();
        assert fileOriginalFilename != null;
        String suffix = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf(".") + 1);
        if (!suffix.equals("txt")) {
            return new Result(false, StatusCode.ERROR, "文件只能是 txt 格式");
        }

        String line;
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {
            while ((line = bf.readLine()) != null) {
                String departmentname = line.split("\\|")[0];
                String projectinfoname = line.split("\\|")[1];
                String name = line.split("\\|")[2];
                String email = line.split("\\|")[3];
                String phone = line.split("\\|")[4];
                if (!Objects.isNull(departmentname) && !departmentname.isEmpty()) {
                    //部门不存在，直接新增
                    Department department = departmentService.findByDepartmentname(departmentname);
                    String departmentId;
                    if (Objects.isNull(department)) {
                        departmentId = idWorker.nextId() + "";
                        departmentService.add(new Department(departmentId, departmentname));
                    } else {
                        //部门存在
                        departmentId = department.getId();
                    }
                    if (!Objects.isNull(projectinfoname) && !projectinfoname.isEmpty()) {
                        Projectinfo projectinfo = projectinfoService.findByDepartmentidAndProjectname(departmentId, projectinfoname);
                        String projectinfoId;
                        //部门不存在当前项目信息，直接新增
                        if (Objects.isNull(projectinfo)) {
                            projectinfoId = idWorker.nextId() + "";
                            projectinfoService.add(new Projectinfo(projectinfoId, departmentId, projectinfoname, false, false, new Date(), false));
                        } else {
                            //项目信息存在
                            projectinfoId = projectinfo.getId();
                        }

                        if (!Objects.isNull(name) && !name.isEmpty() && !Objects.isNull(email) && !email.isEmpty()) {
                            Contact contact = contactService.findByNameAndEmail(name, email);
                            String contactId;
                            //联系人不存在
                            //新增联系人、联系人项目信息关联
                            if (Objects.isNull(contact)) {
                                contactId = idWorker.nextId() + "";
                                contactService.add(new Contact(contactId, name, email, phone));
                                contactProjectinfoService.add(new ContactProjectinfo(idWorker.nextId() + "", contactId, projectinfoId));
                            } else {
                                //联系人存在
                                contactId = contact.getId();
                                //更新联系人项目信息关联
                                contactProjectinfoService.add(new ContactProjectinfo(idWorker.nextId() + "", contactId, projectinfoId));
                                //更新电话
                                if (!Objects.isNull(phone) && !phone.isEmpty() && !phone.equals("暂无")) {
                                    //没有电话，更新电话
                                    if (Objects.isNull(contact.getPhone())) {
                                        contact.setPhone(phone);
                                        contactService.update(contact);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
        return new Result(true, StatusCode.OK, "部门项目信息联系人已上传处理，请稍后查看");
    }
}

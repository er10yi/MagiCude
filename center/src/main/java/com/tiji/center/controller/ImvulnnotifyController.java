package com.tiji.center.controller;

import com.tiji.center.pojo.Imvulnnotify;
import com.tiji.center.pojo.Notifylog;
import com.tiji.center.pojo.Sendmailconfig;
import com.tiji.center.service.ImvulnnotifyService;
import com.tiji.center.service.NotifylogService;
import com.tiji.center.service.SendmailconfigService;
import com.tiji.center.util.NotifyUtil;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;
import util.IdWorker;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * imvulnnotify控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/imvulnnotify")
public class ImvulnnotifyController {

    @Autowired
    private ImvulnnotifyService imvulnnotifyService;
    @Autowired
    private NotifylogService notifylogService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    JavaMailSenderImpl mailSender;
    @Autowired
    private SendmailconfigService sendmailconfigService;
    private final String imvulnnotifyKey = "imvulnnotify";

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", imvulnnotifyService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @GetMapping(value = "/{id}")
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", imvulnnotifyService.findById(id));
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
        Page<Imvulnnotify> pageList = imvulnnotifyService.findSearch(searchMap, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Imvulnnotify>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", imvulnnotifyService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param imvulnnotify
     */
    @PostMapping
    public Result add(@RequestBody Imvulnnotify imvulnnotify) {
        imvulnnotifyService.add(imvulnnotify);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param imvulnnotify
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Imvulnnotify imvulnnotify, @PathVariable String id) {
        redisTemplate.delete(imvulnnotifyKey);
        for (Field field : imvulnnotify.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!"id".equals(field.getName())) {
                redisTemplate.delete(field.getName());
            }
        }
        imvulnnotify.setId(id);
        imvulnnotifyService.update(imvulnnotify);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        imvulnnotifyService.deleteById(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }


    /**
     * 钉钉群机器人测试消息
     *
     * @return
     */
    @GetMapping(value = "/sendding")
    public Result sendDingTalkTest() {
        Date date = new Date();
        String dtSendFlag = "dtSendFlag";
        Boolean dtFlagExist = redisTemplate.hasKey(dtSendFlag);
        if (!Objects.isNull(dtFlagExist) && dtFlagExist) {
            return new Result(false, StatusCode.ERROR, "发送失败：发送测试消息需要间隔10秒");
        } else {
            try {
                Map<String, Object> imvulnnotifyMap = new HashMap<>();
                Imvulnnotify imvulnnotify = imvulnnotifyService.findAll().get(0);
                String secret = imvulnnotify.getSecret();
                String dingtalkmessageurl = imvulnnotify.getDingtalkmessageurl();
                String risk = imvulnnotify.getRisk();
                String infoMsg = "";
                if (StringUtils.isEmpty(risk)) {
                    infoMsg = ",请注意,风险等级未设置,漏洞将无法推送到群里";
                }
                if (StringUtils.isEmpty(secret) || StringUtils.isEmpty(dingtalkmessageurl)) {
                    return new Result(false, StatusCode.ERROR, "发送失败：签名密钥,Webhook地址配置错误");
                }
                for (Field field : imvulnnotify.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (!"id".equals(field.getName())) {
                        Object o = field.get(imvulnnotify);
                        if (o instanceof Boolean) {
                            if ("true".equals(o.toString())) {
                                o = "true";
                            } else {
                                o = "";
                            }
                        }
                        imvulnnotifyMap.put(field.getName(), o);
                    }
                }
                NotifyUtil.sendDingTalk(idWorker, notifylogService, "钉钉群机器人测试消息", imvulnnotifyMap, date);

                redisTemplate.opsForValue().set(dtSendFlag, dtSendFlag);
                redisTemplate.expire(dtSendFlag, 10, TimeUnit.SECONDS);
                return new Result(true, StatusCode.OK, "测试消息已发送,如果未收到消息,可前往提醒日志查看异常记录" + infoMsg);
            } catch (Exception e) {
                notifylogService.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", null, "钉钉群机器人测试消息发送失败", false, e.getMessage(), date));
                return new Result(false, StatusCode.ERROR, "发送失败：可前往提醒日志查看异常消息");
            }

        }
    }

    /**
     * 企微群机器人测试消息
     *
     * @return
     */
    @GetMapping(value = "/sendcorwc")
    public Result sendCorWechatTest() {
        Date date = new Date();
        String corWcSendFlag = "corWcSendFlag";
        Boolean wcFlagExist = redisTemplate.hasKey(corWcSendFlag);
        if (!Objects.isNull(wcFlagExist) && wcFlagExist) {
            return new Result(false, StatusCode.ERROR, "发送失败：发送测试消息需要间隔10秒");
        } else {
            try {
                Map<String, Object> imvulnnotifyMap = new HashMap<>();
                Imvulnnotify imvulnnotify = imvulnnotifyService.findAll().get(0);
                String wechatmessageurl = imvulnnotify.getWechatmessageurl();
                String risk = imvulnnotify.getRisk();
                String infoMsg = "";
                if (StringUtils.isEmpty(risk)) {
                    infoMsg = ",请注意,风险等级未设置,漏洞将无法推送到群里";
                }
                if (StringUtils.isEmpty(wechatmessageurl)) {
                    return new Result(false, StatusCode.ERROR, "发送失败：Webhook地址配置错误");
                }
                for (Field field : imvulnnotify.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    if (!"id".equals(field.getName())) {
                        Object o = field.get(imvulnnotify);
                        if (o instanceof Boolean) {
                            if ("true".equals(o.toString())) {
                                o = "true";
                            } else {
                                o = "";
                            }
                        }
                        imvulnnotifyMap.put(field.getName(), o);
                    }
                }
                NotifyUtil.sendWeChet(idWorker, notifylogService, "企微群机器人测试消息", imvulnnotifyMap, date);
                redisTemplate.opsForValue().set(corWcSendFlag, corWcSendFlag);
                redisTemplate.expire(corWcSendFlag, 10, TimeUnit.SECONDS);
                return new Result(true, StatusCode.OK, "测试消息已发送,如果未收到消息,可前往提醒日志查看异常记录" + infoMsg);
            } catch (Exception e) {
                notifylogService.add(new Notifylog(idWorker.nextId() + "", "W", "企微group", null, "企微群机器人测试消息发送失败", false, e.getMessage(), date));
                return new Result(false, StatusCode.ERROR, "发送失败：可前往提醒日志查看异常记录");
            }
        }
    }

    /**
     * 默认提醒邮箱列表发送测试邮件
     *
     * @return
     */
    @GetMapping(value = "/sendemail")
    public Result sendEmailTest() {
        Date date = new Date();
        String emailSendFlag = "emailSendFlag";
        Boolean emailFlagExist = redisTemplate.hasKey(emailSendFlag);
        if (!Objects.isNull(emailFlagExist) && emailFlagExist) {
            return new Result(false, StatusCode.ERROR, "发送失败：发送测试邮件需要间隔15秒");
        } else {
            try {
                Sendmailconfig sendmailconfig = sendmailconfigService.findAll().get(0);
                String sendFrom = sendmailconfig.getSendfrom();
                String sendTo = sendmailconfig.getSendto();
                //设置邮箱信息
                mailSender.setHost(sendmailconfig.getSendhost());
                mailSender.setUsername(sendFrom);
                mailSender.setPassword(sendmailconfig.getSendpassword());
                String[] sendToArray = sendTo.split(",");
                String risk = sendmailconfig.getSendtorisk();
                String infoMsg = "";
                if (StringUtils.isEmpty(risk)) {
                    infoMsg = ",请注意,风险等级未设置,漏洞报告无法发送";
                }
                if (sendToArray.length == 0 || StringUtils.isEmpty(sendFrom) || StringUtils.isEmpty(sendTo)) {
                    return new Result(false, StatusCode.ERROR, "发送失败：邮箱host,账号,密码/授权码,提醒邮箱列表配置错误");
                }
                for (String mail : sendToArray) {
                    NotifyUtil.sendSimpleMail(mailSender, sendFrom, mail, "魔方测试邮件", "魔方测试邮件");
                }
                notifylogService.add(new Notifylog(idWorker.nextId() + "", "E", "默认收件人列表", null, "魔方测试邮件", true, null, date));
                redisTemplate.opsForValue().set(emailSendFlag, emailSendFlag);
                redisTemplate.expire(emailSendFlag, 15, TimeUnit.SECONDS);
                return new Result(true, StatusCode.OK, "测试邮件已发送,如果未收到邮件,可前往提醒日志查看异常记录" + infoMsg);
            } catch (Exception e) {
                notifylogService.add(new Notifylog(idWorker.nextId() + "", "E", "默认收件人列表", null, "默认提醒邮箱列表测试邮件发送失败", false, e.getMessage(), date));
                return new Result(false, StatusCode.ERROR, "发送失败：可前往提醒日志查看异常记录");
            }
        }
    }
}

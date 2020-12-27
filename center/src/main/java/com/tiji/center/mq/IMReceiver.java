package com.tiji.center.mq;

import com.tiji.center.pojo.*;
import com.tiji.center.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import util.ExcpUtil;

import java.util.*;

/**
 * @author 贰拾壹
 * @create 2019-11-12 15:17
 */
@Component
@RabbitListener(queues = "imresult")
public class IMReceiver {
    private final static Logger logger = LoggerFactory.getLogger(IMReceiver.class);
    @Autowired
    private VulnService vulnService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ProjectinfoService projectinfoService;
    @Autowired
    private ContactProjectinfoService contactProjectinfoService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RabbitHandler
    public void getMessage(Map<String, Object> resultMap) {
        try {
            if (!resultMap.isEmpty()) {
                String imResultKey = "imResultList";
                String projectinfoid = (String) resultMap.get("projectinfoid");
                String risk = (String) resultMap.get("risk");
                String ip = (String) resultMap.get("ip");
                String port = (String) resultMap.get("port");
                String protocol = (String) resultMap.get("protocol");
                String service = (String) resultMap.get("service");
                String version = (String) resultMap.get("version");
                String pluginName = (String) resultMap.get("pluginName");

                List<String> vulnResultList = new ArrayList<>();
                if (!Objects.isNull(resultMap.get("vulnIdList"))) {
                    List<String> vulnIdList = (List<String>) resultMap.get("vulnIdList");
                    for (String vulnId : vulnIdList) {
                        Vuln vuln = vulnService.findById(vulnId);
                        vulnResultList.add(vuln.getName());
                    }
                }

                String result;
                //漏洞
                if (!StringUtils.isEmpty(risk)) {
                    result = "**" + vulnResultList + "**" + " " + risk + " " + ip + " " + port + " " + service + " " + version + " " + pluginName;
                } else {
                    //资产
                    if(StringUtils.isEmpty(protocol)){
                        protocol="";
                    }
                    if(StringUtils.isEmpty(service)){
                        service="";
                    }
                    if(StringUtils.isEmpty(version)){
                        version="";
                    }
                    result = ip + " " + port + " " + protocol + " " + service + " " + version;
                }

                List<String> nameList = new ArrayList<>();
                //有项目编号
                if (!Objects.isNull(projectinfoid)) {
                    Map<String, String> searchMap = new HashMap<>();
                    searchMap.put("projectinfoid", projectinfoid);
                    List<ContactProjectinfo> contactProjectinfoList = contactProjectinfoService.findSearch(searchMap);
                    List<Contact> contactList = new ArrayList<>();
                    for (ContactProjectinfo contactProjectinfo : contactProjectinfoList) {
                        String contactid = contactProjectinfo.getContactid();
                        contactList.add(contactService.findById(contactid));
                    }
                    for (Contact contact : contactList) {
                        String name = contact.getName();
                        nameList.add(name);
                    }
                }

                //im通知默认联系人
                String projectName = null;
                if (!Objects.isNull(projectinfoid)) {
                    Projectinfo projectinfo = projectinfoService.findById(projectinfoid);
                    if (!Objects.isNull(projectinfo)) {
                        projectName = projectinfo.getProjectname();
                    }
                }
                if (Objects.isNull(projectName)) {
                    projectName = "";
                }
                String resultWithContact = result + " " + projectName + " " + nameList;
                //先进先出
                redisTemplate.opsForList().leftPush(imResultKey, resultWithContact);
            }
        } catch (Exception e) {
            logger.error("IMReceiver Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

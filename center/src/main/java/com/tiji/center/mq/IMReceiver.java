package com.tiji.center.mq;

import com.google.gson.Gson;
import com.tiji.center.pojo.*;
import com.tiji.center.service.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.ExcpUtil;
import util.IdWorker;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    private ImvulnnotifyService imvulnnotifyService;
    @Autowired
    private ContactService contactService;
    @Autowired
    private ProjectinfoService projectinfoService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ContactProjectinfoService contactProjectinfoService;
    @Autowired
    private NotifylogService notifylogService;

    private static String signUrl(String secret, String url) throws NoSuchAlgorithmException, InvalidKeyException {
        Long timestamp = System.currentTimeMillis();
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);
        //https://oapi.dingtalk.com/robot/send?access_token=XXXXXX&timestamp=XXX&sign=XXX
        return url + "&timestamp=" + timestamp + "&sign=" + sign;
    }

    @RabbitHandler
    public void getMessage(Map<String, Object> resultMap) {
        try {
            //钉钉一分钟最多10条群消息推送，每条消息发送前休眠7秒，60/7 = 8.57
            Thread.sleep(7000);
            Imvulnnotify imvulnnotify = imvulnnotifyService.findAll().get(0);
            String messageUrl = imvulnnotify.getMessageurl();
            String[] receiverArray = imvulnnotify.getReceiver().split(",");
            String messageTitle = imvulnnotify.getMessagetitle();
            String messagePrefix = imvulnnotify.getMessageprefix();
            String messageSuffix = imvulnnotify.getMessagesuffix();
            String messageCharset = imvulnnotify.getMessagecharset();

            String secret = imvulnnotify.getSecret();
            Boolean notifyall = imvulnnotify.getNotifyall();

            List<String> receiverList = new ArrayList<>(Arrays.asList(receiverArray));
            String projectinfoid = (String) resultMap.get("projectinfoid");
            List<String> vulnIdList = (List<String>) resultMap.get("vulnIdList");
            String risk = (String) resultMap.get("risk");
            String ip = (String) resultMap.get("ip");
            String port = (String) resultMap.get("port");
            String service = (String) resultMap.get("service");
            String version = (String) resultMap.get("version");
            String pluginName = (String) resultMap.get("pluginName");
            List<String> vulnResultList = new ArrayList<>();
            for (String vulnId : vulnIdList) {
                Vuln vuln = vulnService.findById(vulnId);
                vulnResultList.add(vuln.getName());
            }
            //通知
            String result = vulnResultList + "\t" + risk + "\t" + ip + "\t" + port + "\t" + service + "\t" + version + "\t" + pluginName;

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
            String resultWithContact = result + "\t" + projectName + "\t" + nameList;

            //
            List<Notifylog> notifyLogList = new ArrayList<>();
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("content", messageTitle + "\n" + messagePrefix + "\n" + resultWithContact + "\n" + messageSuffix + "\n");

            //@所有或指定联系人，两者只能生效一个
            Map<String, Object> atMap = new HashMap<>();
            List<String> mobileList = new ArrayList<>();
            //mobileList.add("185xxxxxx");
            if (!Objects.isNull(notifyall) && notifyall) {
                atMap.put("isAtAll", true);
            }

            //atMap.put("atMobiles", mobileList);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("msgtype", "text");
            dataMap.put("text", contentMap);
            dataMap.put("at", atMap);

            String reqStr = new Gson().toJson(dataMap);
            String signUrl = signUrl(secret, messageUrl);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(signUrl);
            httpPost.addHeader("Content-Type", "application/json");
            //httpPost.addHeader("Connection", "close");
            httpPost.setEntity(new StringEntity(reqStr, messageCharset));
            String dResponse = "";
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                dResponse = EntityUtils.toString(response.getEntity(), messageCharset);
                if (dResponse.contains("ok")) {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", result, true, null, new Date()));
                }
                logger.info("Ding talk response: " + EntityUtils.toString(response.getEntity(), messageCharset));
                //System.out.print(EntityUtils.toString(response.getEntity(), messageCharset));
            } catch (IOException e) {
                if (!dResponse.contains("ok")) {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", result, false, e.getMessage() + "\n" + dResponse, new Date()));
                }
            }
            notifylogService.batchAdd(notifyLogList);
        } catch (Exception e) {
            logger.error("IMReceiver Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

package com.tiji.center.util;

import com.google.gson.Gson;
import com.tiji.center.pojo.Notifylog;
import com.tiji.center.service.NotifylogService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import util.ExcpUtil;
import util.IdWorker;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author 贰拾壹
 * @create 2019-11-05 14:51
 */
public class NotifyUtil {
    static class ImResponse {
        private String errcode;
        private String errmsg;

        public String getErrcode() {
            return errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

    }

    private final static Logger logger = LoggerFactory.getLogger(NotifyUtil.class);

    public static void sendMail2ProjectOwner(String sendFrom, String sendMailConfigAssetSubject, String sendMailConfigAssetContent, Map<String, List<File>> projectInfoAndContactWithFilelistMap, JavaMailSenderImpl mailSender, NotifylogService notifylogService, IdWorker idWorker) {
        Set<Map.Entry<String, List<File>>> entries = projectInfoAndContactWithFilelistMap.entrySet();
        List<Notifylog> notifyLogList = new ArrayList<>();

//        for (Map.Entry<String, List<File>> stringListEntry : entries) {
        entries.parallelStream().forEach((stringListEntry) -> {
            String nameAndEmail = stringListEntry.getKey();
            String name = nameAndEmail.split("-\\+-")[0];
            String emails = nameAndEmail.split("-\\+-")[1];

            //发邮件
            Map<String, File> fileMap = new LinkedHashMap<>();
            List<File> fileList = stringListEntry.getValue();
            List<String> fileNameList = new ArrayList<>();
            fileList.forEach(file -> {
                String attachmentName = file.getName();
                fileMap.put(attachmentName, file);
                fileNameList.add(attachmentName);
                //System.out.println(name + " " + email + " " + attachmentName);
            });
            String fileListString = fileNameList.toString().replaceAll("[\\[\\]]", "");
            String[] emailArray = emails.split(",");
            for (String email : emailArray) {

                //System.out.println("给 " + name + " " + email + " 发邮件成功 " + fileListString);
                try {
                    //NotifyUtil.sendMail(mailSender, sendFrom, email, sendMailConfigAssetSubject, name + " " + sendMailConfigAssetContent + sendFrom, fileMap);
                    NotifyUtil.sendMailWithAttach(mailSender, sendFrom, email, sendMailConfigAssetSubject, sendMailConfigAssetContent, fileMap);
                    //记录发送成功
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", name, email, fileListString, true, null, new Date()));
                    //System.out.println("给" + name + " " + email + " 发邮件成功" + fileListString);
                } catch (Exception e) {
                    //记录发送失败
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", name, email, fileListString, false, e.getMessage(), new Date()));
                    //System.out.println("给" + name + " " + email + "发邮件失败:" + fileListString + e.getMessage());
                    logger.info("send mail 2 owner Exception here: " + e);
                }
            }
        });
        notifylogService.batchAdd(notifyLogList);
    }


    //发送带附件
    public static void sendMailWithAttach(JavaMailSenderImpl mailSender, String sendFrom, String sendTo, String subject, String content, Map<String, File> fileMap) throws MessagingException {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage, true);
        mimeMessageHelper.setFrom(sendFrom);
        mimeMessageHelper.setTo(sendTo);
        //邮件主题
        mimeMessageHelper.setSubject(subject);
        //邮件内容
        mimeMessageHelper.setText(content);
        Set<Map.Entry<String, File>> entrySet = fileMap.entrySet();
        for (Map.Entry<String, File> fileEntry : entrySet) {
            String attachmentName = fileEntry.getKey();
            File file = fileEntry.getValue();
            mimeMessageHelper.addAttachment(attachmentName, file);
        }
        mailSender.send(mimeMailMessage);
    }

    public static void sendSimpleMail(JavaMailSenderImpl mailSender, String sendFrom, String sendTo, String subject, String content) throws MessagingException {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage);
        mimeMessageHelper.setFrom(sendFrom);
        mimeMessageHelper.setTo(sendTo);
        //邮件主题
        mimeMessageHelper.setSubject(subject);
        //邮件内容
        mimeMessageHelper.setText(content);
        mailSender.send(mimeMailMessage);
    }


    //根据titleArrays设置标题
    public static void setSheetHeaderTitle(Sheet sheet, String[] titleArrays) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < titleArrays.length; i++) {
            sheet.setColumnWidth(i, 16 * 256);
            Cell cell = row.createCell(i);
            cell.setCellValue(titleArrays[i]);
        }
    }


    public static void setCellData(Sheet sheet, Row row, int rowNumber, Object cellData) {
        String cellDataString = "";
        int cellDataStringLength = 4;
        if (!Objects.isNull(cellData)) {
            cellDataString = cellData.toString().replaceAll("[\\[\\]]", "");
            cellDataStringLength = Math.min(cellDataString.length(), 10);
        }
        sheet.setColumnWidth(rowNumber, cellDataStringLength * 512);
        Cell cell = row.createCell(rowNumber);
        cell.setCellValue(cellDataString);
    }

    /**
     * 企微群机器人测试消息
     */
    public static void sendWeChet(IdWorker idWorker, NotifylogService notifylogService, String resultWithContact, Map imvulnnotifyMap, Date date) {
        List<Notifylog> notifyLogList = new ArrayList<>();
        String resultWithContactReplaceAll = resultWithContact.replaceAll("> \\*\\*", "").replaceAll("\\*\\*\\s", " ").replaceAll(">\\s", " ");
        try {
            String wechatmessageurl = (String) imvulnnotifyMap.get("wechatmessageurl");
            //暂时用不上
            //String[] receiverArray = ((String) imvulnnotifyMap.get("wechatreceiver")).split(",");
            String messagetitle = (String) imvulnnotifyMap.get("messagetitle");
            String messageprefix = (String) imvulnnotifyMap.get("messageprefix");
            String messagesuffix = (String) imvulnnotifyMap.get("messagesuffix");
            String messagecharset = (String) imvulnnotifyMap.get("messagecharset");

            String wechatnotifyall = (String) imvulnnotifyMap.get("wechatnotifyall");

            if (StringUtils.isEmpty(wechatmessageurl)) {
                notifyLogList.add(new Notifylog(idWorker.nextId() + "", "W", "企微group", "", resultWithContact, false, "webhook地址配置错误", date));
                notifylogService.batchAdd(notifyLogList);
                return;
            }

            Map<String, String> contentMap = new HashMap<>();
            if (!StringUtils.isEmpty(messagetitle)) {
                messagetitle = "**" + messagetitle + "**" + "\n";
            } else {
                messagetitle = "";
            }
            if (!StringUtils.isEmpty(messageprefix)) {
                messageprefix = messageprefix + "\n";
            } else {
                messageprefix = "";
            }
            if (!StringUtils.isEmpty(messagesuffix)) {
                messagesuffix = messagesuffix + "\n";
            } else {
                messagesuffix = "";
            }


            String massgeType = "markdown";
            //@all不支持markdown格式，转成text
            if ("true".equals(wechatnotifyall)) {
                massgeType = "text";
                contentMap.put("mentioned_mobile_list", "@all");
                contentMap.put("content", (messagetitle + messageprefix + resultWithContact + "\n" + messagesuffix).replaceAll("[*>]", ""));
            } else {
                contentMap.put("content", messagetitle + messageprefix + resultWithContact + "\n" + messagesuffix);
            }

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("msgtype", massgeType);
            dataMap.put(massgeType, contentMap);

            String reqStr = new Gson().toJson(dataMap);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(wechatmessageurl);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(reqStr, messagecharset));
            ImResponse wcResponse = null;
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String resString = EntityUtils.toString(response.getEntity(), messagecharset);
                wcResponse = new Gson().fromJson(resString, ImResponse.class);
                if ("0".equals(wcResponse.getErrcode()) && wcResponse.getErrmsg().contains("ok")) {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "W", "企微group", "", resultWithContactReplaceAll, true, null, date));
                } else {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "W", "企微group", "", resultWithContactReplaceAll, false, resString, date));
                }
                logger.info("Ding talk response: " + EntityUtils.toString(response.getEntity(), messagecharset));
            } catch (IOException e) {
                if (Objects.isNull(wcResponse)) {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "W", "企微group", "", resultWithContactReplaceAll, false, "发送失败,异常消息:\n" + e.getMessage(), date));
                }
            }
        } catch (Exception e) {
            notifyLogList.add(new Notifylog(idWorker.nextId() + "", "W", "企微group", "", resultWithContactReplaceAll, false, "发送失败,异常消息:\n" + e.getMessage(), date));
        }
        notifylogService.batchAdd(notifyLogList);
    }

    /**
     * 钉钉群机器人测试消息
     */
    public static void sendDingTalk(IdWorker idWorker, NotifylogService notifylogService, String resultWithContact, Map imvulnnotifyMap, Date date) {
        List<Notifylog> notifyLogList = new ArrayList<>();
        String resultWithContactRepalceAll = resultWithContact.replaceAll("> \\*\\*", "").replaceAll("\\*\\*\\s", " ").replaceAll(">\\s", " ");
        try {
            String dingtalkmessageurl = (String) imvulnnotifyMap.get("dingtalkmessageurl");
            //暂时用不上
            //String[] receiverArray = ((String) imvulnnotifyMap.get("dingtalkreceiver")).split(",");

            String messagetitle = (String) imvulnnotifyMap.get("messagetitle");
            String messageprefix = (String) imvulnnotifyMap.get("messageprefix");
            String messagesuffix = (String) imvulnnotifyMap.get("messagesuffix");
            String messagecharset = (String) imvulnnotifyMap.get("messagecharset");

            String secret = (String) imvulnnotifyMap.get("secret");
            if (StringUtils.isEmpty(dingtalkmessageurl) || StringUtils.isEmpty(secret)) {
                notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", resultWithContactRepalceAll, false, "签名密钥和webhook地址配置错误", date));
                notifylogService.batchAdd(notifyLogList);
                return;
            }
            String dingtalknotifyall = (String) imvulnnotifyMap.get("dingtalknotifyall");
            String contentTitle = messagetitle;
            if (StringUtils.isEmpty(messagetitle)) {
                messagetitle = "【魔方】提醒";
            }
            if (!StringUtils.isEmpty(messageprefix)) {
                messageprefix = "\n" + messageprefix + "\n";
            } else {
                messageprefix = "";
            }
            if (!StringUtils.isEmpty(messagesuffix)) {
                messagesuffix = messagesuffix + "\n";
            } else {
                messagesuffix = "";
            }
            if (!StringUtils.isEmpty(contentTitle)) {
                contentTitle = "**" + messagetitle + "**" + "\n";
            } else {
                contentTitle = "";
            }
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("title", messagetitle);
            contentMap.put("text", contentTitle + messageprefix + resultWithContact + "\n" + messagesuffix);
            //@所有或指定联系人，两者只能生效一个
            Map<String, Object> atMap = new HashMap<>();
            List<String> mobileList = new ArrayList<>();
            //mobileList.add("185xxxxxx");
            if ("true".equals(dingtalknotifyall)) {
                atMap.put("isAtAll", true);
            }


            //atMap.put("atMobiles", mobileList);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("msgtype", "markdown");
            dataMap.put("markdown", contentMap);
            dataMap.put("at", atMap);

            String reqStr = new Gson().toJson(dataMap);
            String signUrl = signUrl(secret, dingtalkmessageurl);

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(signUrl);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(reqStr, messagecharset));
            ImResponse dingResponse = null;
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String dResponse = EntityUtils.toString(response.getEntity(), messagecharset);
                dingResponse = new Gson().fromJson(dResponse, ImResponse.class);
                if ("0".equals(dingResponse.getErrcode()) && "ok".equals(dingResponse.getErrmsg())) {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", resultWithContactRepalceAll, true, null, date));
                } else {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", resultWithContactRepalceAll, false, dResponse, date));
                }
                logger.info("Ding talk response: " + EntityUtils.toString(response.getEntity(), messagecharset));
            } catch (IOException e) {
                if (Objects.isNull(dingResponse)) {
                    notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", resultWithContactRepalceAll, false, "发送失败,异常消息:\n" + e.getMessage(), date));
                }
            }
        } catch (Exception e) {
            notifyLogList.add(new Notifylog(idWorker.nextId() + "", "D", "钉钉group", "", resultWithContactRepalceAll, false, "发送失败,异常消息:\n" + e.getMessage(), date));
        }
        notifylogService.batchAdd(notifyLogList);
    }

    /**
     * 钉钉消息签名
     *
     * @param secret
     * @param url
     * @return
     */
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

}

package com.tiji.center.util;

import com.tiji.center.pojo.Notifylog;
import com.tiji.center.service.NotifylogService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import util.IdWorker;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.*;

/**
 * @author 贰拾壹
 * @create 2019-11-05 14:51
 */
public class NotifyUtil {
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
                    NotifyUtil.sendMail(mailSender, sendFrom, email, sendMailConfigAssetSubject, sendMailConfigAssetContent, fileMap);
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
    public static void sendMail(JavaMailSenderImpl mailSender, String sendFrom, String sendTo, String subject, String content, Map<String, File> fileMap) throws MessagingException {
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
}

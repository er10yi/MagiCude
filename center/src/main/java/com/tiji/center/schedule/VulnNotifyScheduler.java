package com.tiji.center.schedule;

import com.tiji.center.pojo.Notifylog;
import com.tiji.center.pojo.Projectvulnnotify;
import com.tiji.center.pojo.Sendmailconfig;
import com.tiji.center.service.NotifylogService;
import com.tiji.center.service.ProjectvulnnotifyService;
import com.tiji.center.service.SendmailconfigService;
import com.tiji.center.service.TaskipService;
import com.tiji.center.util.NotifyUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import util.ExcpUtil;
import util.IdWorker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * @author 贰拾壹
 * @create 2019-11-05 17:39
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class VulnNotifyScheduler implements Job {
    private final static Logger logger = LoggerFactory.getLogger(VulnNotifyScheduler.class);
    @Autowired
    JavaMailSenderImpl mailSender;
    //统一所有报告和单个报告的时间戳
    LocalTime localTime = LocalTime.now();
    @Autowired
    private TaskipService taskipService;
    @Autowired
    private ProjectvulnnotifyService projectvulnnotifyService;
    @Autowired
    private SendmailconfigService sendmailconfigService;
    @Autowired
    private NotifylogService notifylogService;
    @Autowired
    private IdWorker idWorker;
    private String timePrefix = LocalDate.now().toString().replace("-", "") + "-" + localTime.getHour() + localTime.getMinute() + " ";


    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Notifylog> notifyLogList = new ArrayList<>();
        Date date = new Date();
        try {
            //分页导出资产数据
            long allAssetsCount = taskipService.findAllVulnsCount();
            if (allAssetsCount > 0) {
                //然后才发报告
                List<Projectvulnnotify> projectvulnnotifyList = projectvulnnotifyService.findAll();
                String projectVulnNotifyRisk = projectvulnnotifyList.get(0).getRisk();
                List<Sendmailconfig> sendmailconfigList = sendmailconfigService.findAll();
                Sendmailconfig sendmailconfig = sendmailconfigList.get(0);
                String sendFrom = sendmailconfig.getSendfrom();
                String sendTo = sendmailconfig.getSendto();
                String[] sendToArray = sendTo.split(",");
                String sendmailconfigVulnsubject = sendmailconfig.getVulnsubject();
                String sendmailconfigVulncontent = sendmailconfig.getVulncontent();
                String author = sendmailconfig.getExcelauthor();
                String sendtorisk = sendmailconfig.getSendtorisk();

                if(StringUtils.isEmpty(sendtorisk)){
                    notifylogService.add(new Notifylog(idWorker.nextId() + "", "E", null, null,  null, false, "默认提醒邮箱漏洞报告发送失败，异常消息：风险等级未定义", date));
                    return;
                }
                //设置邮箱信息
                mailSender.setHost(sendmailconfig.getSendhost());
                mailSender.setUsername(sendFrom);
                mailSender.setPassword(sendmailconfig.getSendpassword());

                String lastFileName = "漏洞报告";
                String[] titleArraysWithContact = new String[]{"一级分类", "二级分类", "漏洞名称", "风险", "ip", "ip上线时间", "ip下线时间", "端口", "服务", "版本", "端口发现时间", "端口关闭时间", "检测插件列表", "检测结果列表", "漏洞发现时间", "漏洞描述", "参考", "影响范围", "解决方案", "解决方案代码示例", "解决方案配置示例", "项目", "联系人列表", "联系邮箱列表", "联系电话列表"};
                //ide运行用这个
                //String path = this.getClass().getClassLoader().getResource("").getPath() + "report/vuln/";
                //jar包部署用这个
                String path = "report/vuln/";
                File file1 = new File(path);
                if (!file1.exists()) {
                    boolean mkdirs = file1.mkdirs();
                }

                long pageSize = 1000;

                //所有漏洞报告
                String firstFileNameAll = "所有";
                String fileNameAll = timePrefix + firstFileNameAll + "-" + lastFileName + ".xlsx";
                File fileAll = new File(path + fileNameAll);
                db2Excel(pageSize, author, firstFileNameAll, lastFileName, titleArraysWithContact, fileAll, sendtorisk,notifyLogList);
                //有联系人漏洞报告
                String firstFileNameAllWithContact = "有项目联系人";
                String fileNameAllWithContact = timePrefix + firstFileNameAllWithContact + "-" + lastFileName + ".xlsx";
                File fileAllWithContact = new File(path + fileNameAllWithContact);
                db2Excel(pageSize, author, firstFileNameAllWithContact, lastFileName, titleArraysWithContact, fileAllWithContact, sendtorisk,notifyLogList);
                //无联系人漏洞报告
                String firstFileNameAllNoContact = "无项目联系人";
                String fileNameAllNoContact = timePrefix + firstFileNameAllNoContact + "-" + lastFileName + ".xlsx";
                File fileAllNoContact = new File(path + fileNameAllNoContact);
                db2Excel(pageSize, author, firstFileNameAllNoContact, lastFileName, titleArraysWithContact, fileAllNoContact, sendtorisk,notifyLogList);
                //发邮件
                Map<String, File> fileMap = new LinkedHashMap<>();
                fileMap.put(fileAll.getName(), fileAll);
                fileMap.put(fileAllWithContact.getName(), fileAllWithContact);
                fileMap.put(fileAllNoContact.getName(), fileAllNoContact);
                if (!fileMap.isEmpty()) {
                    //20201221默认提醒增加提醒日志
                    for (String mail : sendToArray) {
                        try {
                            NotifyUtil.sendMailWithAttach(mailSender, sendFrom, mail, sendmailconfigVulnsubject, sendmailconfigVulncontent, fileMap);
                            notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", null, mail, fileNameAll + " " + fileNameAllWithContact + " " + fileNameAllNoContact, true, null, date));
                            //System.out.println("sending ...");
                        } catch (Exception e) {
                            notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", null, mail, fileNameAll + " " + fileNameAllWithContact + " " + fileNameAllNoContact, false, "默认提醒邮箱漏洞报告发送失败，异常消息：" + e.getMessage(), date));
                            logger.info("all vuln notify Exception here: " + e);
                        }
                    }
                }
                fileMap.clear();

                //项目负责人
                //分页导出检查结果数据

                //分页导出资产数据
                //long allAssetsCount = taskipService.findAllAssetsCount();

                //100条测试用
                //long allAssetsCount = 100;
                //long pageSize = 50;
                long loopSize = (long) Math.ceil(allAssetsCount / (double) pageSize);
                long offset = 0;

                String[] titleArraysWithoutContact = new String[]{"一级分类", "二级分类", "漏洞名称", "风险", "ip", "ip上线时间", "ip下线时间", "端口", "服务", "版本", "端口发现时间", "端口关闭时间", "检测插件列表", "检测结果列表", "漏洞发现时间", "漏洞描述", "参考", "影响范围", "解决方案", "解决方案代码示例", "解决方案配置示例"};

                Map<String, Map<Object, Object>> totalResultMap = new LinkedHashMap<>();
                Map<String, List<String>> contactMap = new HashMap<>();
                for (long step = 0; step < loopSize; step++) {
                    List<List<String>> allVulnsByPage = taskipService.findAllVulnsByPage(offset, pageSize);
                    for (List<String> singleResultList : allVulnsByPage) {
                        String tvrisk = singleResultList.get(3);
                        //risk在db中才写到表格
                        if (projectVulnNotifyRisk.contains(tvrisk)) {
                            if (!Objects.isNull(singleResultList.get(21))) {
                                String projectInfoName = singleResultList.get(21);
                                String contactNameString = singleResultList.get(22);
                                String contactEmailString = singleResultList.get(23);
                                //关联 项目名称 - 联系人信息
                                if (!Objects.isNull(contactNameString)) {
                                    List<String> contactList = new ArrayList<>();
                                    String[] contactNameArray = contactNameString.split(";");
                                    for (int i = 0; i < contactNameArray.length; i++) {
                                        contactList.add(contactNameArray[i] + "-+-" + contactEmailString.split(";")[i]);
                                    }
                                    contactMap.put(projectInfoName, contactList);
                                }

                                if (!totalResultMap.containsKey(projectInfoName)) {

                                    //当前不存在，新建并写到Excel
                                    XSSFWorkbook workbook1 = new XSSFWorkbook();
                                    POIXMLProperties xmlProps = workbook1.getProperties();
                                    POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties();
                                    coreProps.setCreator(author);
                                    Workbook workbook = new SXSSFWorkbook(workbook1, 100, true, true);
                                    //Workbook workbook = new SXSSFWorkbook();

                                    Sheet sheet = workbook.createSheet(projectInfoName);
                                    //设置标题
                                    NotifyUtil.setSheetHeaderTitle(sheet, titleArraysWithoutContact);
                                    //写数据
                                    int index = 1;
                                    index = writeCellData(sheet, index, singleResultList, false);

                                    //添加当前id，并新建
                                    Map<Object, Object> projectInfoIdMap = new HashMap<>();
                                    projectInfoIdMap.put(projectInfoName + "sheet", sheet);
                                    projectInfoIdMap.put(projectInfoName + "workbook", workbook);
                                    projectInfoIdMap.put(projectInfoName + "index", index);
                                    totalResultMap.put(projectInfoName, projectInfoIdMap);
                                } else {
                                    //添加到已存在的
                                    Map<Object, Object> projectInfoIdMap = totalResultMap.get(projectInfoName);
                                    Sheet sheet = (Sheet) projectInfoIdMap.get(projectInfoName + "sheet");

                                    Workbook workbook = (Workbook) projectInfoIdMap.get(projectInfoName + "workbook");
                                    //写数据
                                    int index = (int) projectInfoIdMap.get(projectInfoName + "index");
                                    index = writeCellData(sheet, index, singleResultList, false);

                                    projectInfoIdMap.put(projectInfoName + "sheet", sheet);
                                    projectInfoIdMap.put(projectInfoName + "workbook", workbook);
                                    projectInfoIdMap.put(projectInfoName + "index", index);
                                    totalResultMap.put(projectInfoName, projectInfoIdMap);
                                }
                            }
                        }
                    }
                    offset = offset + pageSize;
                }


                Map<String, Object> projectInfoNameAndFileMap = new LinkedHashMap<>();
                //写到文件
                Set<Map.Entry<String, Map<Object, Object>>> entries = totalResultMap.entrySet();
                for (Map.Entry<String, Map<Object, Object>> entry : entries) {

                    String projectInfoName = entry.getKey();
                    Map<Object, Object> projectInfoIdMap = entry.getValue();

                    Workbook workbookInMap = (Workbook) projectInfoIdMap.get(projectInfoName + "workbook");
                    Sheet sheetInMap = (Sheet) projectInfoIdMap.get(projectInfoName + "sheet");

                    String fileName = timePrefix + sheetInMap.getSheetName() + "-" + lastFileName + ".xlsx";
                    File file = new File(path + fileName);
                    //String path = this.getClass().getClassLoader().getResource("").getPath() + "report/vuln/";
                    if (!file.exists()) {
                        file1.mkdirs();
                    }
                    try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
                        // 导出
                        workbookInMap.write(fileOutputStream);
                        workbookInMap.close();
                    } catch (IOException e) {
                        notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", null, null, null, false, "漏洞报告无法写入文件，异常消息：" + e.getMessage(), date));
                        logger.info("File2disk Vuln owner Exception here: " + e);
                    }

                    projectInfoNameAndFileMap.put(projectInfoName, file);

                }

                //将文件与项目人联系方式绑定
                Map<String, List<File>> projectInfoAndContactWithFilelistMap = new LinkedHashMap<>();
                Set<Map.Entry<String, Object>> entrySet = projectInfoNameAndFileMap.entrySet();
                for (Map.Entry<String, Object> entry : entrySet) {
                    String projectInfoName = entry.getKey();
                    File file = (File) entry.getValue();
                    List<String> contactList = contactMap.get(projectInfoName);
                    if (!Objects.isNull(contactList)) {
                        for (String contactNameEmailString : contactList) {
                            if (!projectInfoAndContactWithFilelistMap.containsKey(contactNameEmailString)) {
                                List<File> fileList = new LinkedList<>();
                                fileList.add(file);
                                projectInfoAndContactWithFilelistMap.put(contactNameEmailString, fileList);
                            } else {
                                List<File> fileList = projectInfoAndContactWithFilelistMap.get(contactNameEmailString);
                                fileList.add(file);
                                projectInfoAndContactWithFilelistMap.put(contactNameEmailString, fileList);
                            }
                        }
                    }
                }
                totalResultMap.clear();
                //发邮件
                NotifyUtil.sendMail2ProjectOwner(sendFrom, sendmailconfigVulnsubject, sendmailconfigVulncontent, projectInfoAndContactWithFilelistMap, mailSender, notifylogService, idWorker);
            }
        } catch (Exception e) {
            notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", null, null, null, false, "默认提醒邮箱漏洞报告发送失败，异常消息：" + e.getMessage(), date));
            logger.error("VulnNotifyScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
        notifylogService.batchAdd(notifyLogList);
    }

    public void db2Excel(long pageSize, String author, String firstFileName, String lastFileName, String[] titleArraysWithContact, File file, String sendtorisk,List<Notifylog> notifyLogList) {
        //分页导出资产数据
        long allAssetsCount = taskipService.findAllVulnsCount();
        if (allAssetsCount > 0) {
            XSSFWorkbook workbook1 = new XSSFWorkbook();
            POIXMLProperties xmlProps = workbook1.getProperties();
            POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties();
            coreProps.setCreator(author);
            Workbook workbook = new SXSSFWorkbook(workbook1, 100, true, true);

            try (FileOutputStream fileOutputStream = new FileOutputStream(file);) {
                //long pageSize = 100;

                //100条测试用
                //long allAssetsCount = 100;
                //long pageSize = 50;
                long loopSize = (long) Math.ceil(allAssetsCount / (double) pageSize);
                long offset = 0;
                int index = 1;
                int sheetRow = 0;
                int sheetTab = 1;

                for (long step = 0; step < loopSize; step++) {
                    //List<List<String>> allVulnsByPage = taskipService.findAllAssetsByPage(offset, pageSize);
                    List<List<String>> allVulnsByPage = taskipService.findAllVulnsByPage(offset, pageSize);
                    for (List<String> singleResultList : allVulnsByPage) {
                        String tvrisk = singleResultList.get(3);
                        //risk在db中才写到表格
                        if (sendtorisk.contains(tvrisk)) {
                            String sheetName = firstFileName + lastFileName + "-" + sheetTab;
                            Sheet sheet = workbook.getSheet(sheetName);
                            if (Objects.isNull(sheet)) {
                                sheet = workbook.createSheet(sheetName);
                                NotifyUtil.setSheetHeaderTitle(sheet, titleArraysWithContact);
                            }
                            //最大行1048576-1，分页
                            if (sheetRow == 1048575) {
                                sheetTab++;
                                sheet = workbook.createSheet(firstFileName + lastFileName + "-" + sheetTab);
                                //设置标题
                                NotifyUtil.setSheetHeaderTitle(sheet, titleArraysWithContact);
                                sheetRow = 0;
                                index = 1;
                            }

                            if ("所有".equals(firstFileName)) {
                                index = writeCellData(sheet, index, singleResultList, true);
                                sheetRow++;
                            }
                            if ("有项目联系人".equals(firstFileName) && !Objects.isNull(singleResultList.get(21))) {
                                index = writeCellData(sheet, index, singleResultList, true);
                                sheetRow++;
                            }
                            if ("无项目联系人".equals(firstFileName) && Objects.isNull(singleResultList.get(21))) {
                                index = writeCellData(sheet, index, singleResultList, true);
                                sheetRow++;
                            }
                        }
                    }
                    offset = offset + pageSize;
                }

                fileOutputStream.flush();
                workbook.write(fileOutputStream);
                workbook.close();
            } catch (IOException e) {
                notifyLogList.add(new Notifylog(idWorker.nextId() + "", "E", null, null, null, false, "漏洞导出到Excel失败，异常消息：" + e.getMessage(), new Date()));
                logger.info("vuln report 2Excel Exception here: " + e);
            }
        }
    }

    //设置内容
    public int writeCellData(Sheet sheet, int index, List<String> singleResultList, boolean allData) {
        Row row;
        row = sheet.createRow(index++);
        NotifyUtil.setCellData(sheet, row, 0, singleResultList.get(0));
        NotifyUtil.setCellData(sheet, row, 1, singleResultList.get(1));
        NotifyUtil.setCellData(sheet, row, 2, singleResultList.get(2));
        NotifyUtil.setCellData(sheet, row, 3, singleResultList.get(3));
        NotifyUtil.setCellData(sheet, row, 4, singleResultList.get(4));
        NotifyUtil.setCellData(sheet, row, 5, singleResultList.get(5));
        NotifyUtil.setCellData(sheet, row, 6, singleResultList.get(6));
        NotifyUtil.setCellData(sheet, row, 7, singleResultList.get(7));
        NotifyUtil.setCellData(sheet, row, 8, singleResultList.get(8));
        NotifyUtil.setCellData(sheet, row, 9, singleResultList.get(9));
        NotifyUtil.setCellData(sheet, row, 10, singleResultList.get(10));
        NotifyUtil.setCellData(sheet, row, 11, singleResultList.get(11));
        NotifyUtil.setCellData(sheet, row, 12, singleResultList.get(12));
        NotifyUtil.setCellData(sheet, row, 13, singleResultList.get(13));
        NotifyUtil.setCellData(sheet, row, 14, singleResultList.get(14));
        NotifyUtil.setCellData(sheet, row, 15, singleResultList.get(15));
        NotifyUtil.setCellData(sheet, row, 16, singleResultList.get(16));
        NotifyUtil.setCellData(sheet, row, 17, singleResultList.get(17));
        NotifyUtil.setCellData(sheet, row, 18, singleResultList.get(18));
        NotifyUtil.setCellData(sheet, row, 19, singleResultList.get(19));
        NotifyUtil.setCellData(sheet, row, 20, singleResultList.get(20));

        if (allData && !Objects.isNull(singleResultList.get(21))) {
            NotifyUtil.setCellData(sheet, row, 21, singleResultList.get(21));
            NotifyUtil.setCellData(sheet, row, 22, singleResultList.get(22));
            NotifyUtil.setCellData(sheet, row, 23, singleResultList.get(23));
            NotifyUtil.setCellData(sheet, row, 24, singleResultList.get(24));
        }
        return index;
    }

}

package com.tiji.agent.thread;


import com.tiji.agent.plugin.JavaPluginChecker;
import com.tiji.agent.util.HttpJsoupUtil;
import jep.Interpreter;
import jep.JepException;
import jep.SharedInterpreter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import util.ExcpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.currentThread;

/**
 * JepChecker多线程
 *
 * @author 贰拾壹
 * @create 2020-08-05 22:15
 */
public class CheckerThread implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(CheckerThread.class);
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final String taskId, workType, ip, port, pluginName, args, timeout, pluginCode, validateType, service, version, httpValidateApi, dnsValidateIp;
    private final RedisTemplate<String, String> redisTemplate;


    public CheckerThread(String taskId, String workType, String targetIps, String httpValidateApi, String dnsValidateIp, RabbitMessagingTemplate rabbitMessagingTemplate, RedisTemplate<String, String> redisTemplate) {
        String[] selfdArr = targetIps.split("<=-=>");
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.taskId = taskId;
        this.workType = workType;
        this.ip = selfdArr[0];
        this.port = selfdArr[1];
        this.pluginName = selfdArr[2];
        this.args = selfdArr[3];
        this.timeout = selfdArr[4];
        this.pluginCode = selfdArr[5];
        this.validateType = selfdArr[6];
        this.service = selfdArr[7];
        this.version = selfdArr[8];

        this.httpValidateApi = httpValidateApi;
        this.dnsValidateIp = dnsValidateIp;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        try {
            String sliceIPListSizeName = "sliceIPListSize_" + taskId;
            Boolean existSliceIPListSizeName = redisTemplate.hasKey(sliceIPListSizeName);
            if (!Objects.isNull(existSliceIPListSizeName) && existSliceIPListSizeName) {

                if (!currentThread().isInterrupted()) {
                    Map<String, List<String>> payloadMap = new HashMap<>();
                    //用户名密码
                    if (pluginName.contains("WeakPass")) {
                        userPassDict2PayloadMap(payloadMap, redisTemplate);
                    }
                    //版本
                    if (pluginName.contains("VerDetect")) {
                        String versionName = version.split("\\s")[0];
                        //nginx版本检测
                        if ("nginx".equals(versionName)) {
                            payloadMap.put("rawVersion", Collections.singletonList(version));
                            nginxVersion2PayloadMap(versionName, timeout, payloadMap);
                        }
                    }
                    //辅助验证，http或dns标记
                    if (validateType.contains("http")) {
                        payloadMap.put("http_validate", Collections.singletonList(httpValidateApi));

                    }
                    if (validateType.contains("dns")) {
                        payloadMap.put("dns_validate", Collections.singletonList(dnsValidateIp));
                    }

                    StringBuilder scanResult = new StringBuilder();

                    //java的selfd插件
                    if (pluginName.startsWith("Java")) {
                        List<String> withList = Arrays.asList("JavaFTPWeakPass", "JavaMemcachedStats", "JavaMongoDbListDb", "JavaMsSqlServerWeakPass", "JavaMysqlWeakPass", "JavaNginxVerDetect", "JavaOracleWeakPass", "JavaPostgresSqlWeakPass", "JavaRedisWeakPass", "JavaSambaWeakPass", "JavaSSHWeakPass", "JavaZookeeperEnvi");
                        if (withList.contains(pluginName)) {
                            scanResult = JavaPluginChecker.start(pluginName, ip, port, args, timeout, payloadMap);
                        }
                    } else if (pluginName.startsWith("HTTP")) {
                        //HTTP json插件，调用jsoup
                        String redisDictUserAgent = "userAgentSet_";
                        String userAgent;
                        Boolean existUserAgent = redisTemplate.hasKey(redisDictUserAgent);
                        if (!Objects.isNull(existUserAgent) && existUserAgent) {
                            userAgent = redisTemplate.opsForSet().randomMember(redisDictUserAgent);
                        } else {
                            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36";
                        }
                        scanResult = HttpJsoupUtil.getResponse(ip, port, userAgent, pluginCode);

                    } else {
                        //Python3
                        //检测python import的模块是否已经安装
                        checkPythonImport(pluginCode);
                        try (Interpreter interp = new SharedInterpreter()) {
                            interp.exec(pluginCode);
                            Object result = interp.invoke("check", ip, port, args, timeout, payloadMap);
                            scanResult.append(result);
                        }
                    }

                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("workType", workType);
                    resultMap.put("taskId", taskId);
                    resultMap.put("pluginName", pluginName);
                    resultMap.put("ip", ip);
                    resultMap.put("port", port);
                    if (scanResult.length() == 0) {
                        resultMap.put("scanResult", "scanResult");
                    } else {
                        resultMap.put("scanResult", scanResult.toString());
                    }
                    if (!currentThread().isInterrupted()) {
                        rabbitMessagingTemplate.convertAndSend("scanresult", resultMap);
                        //change set to list
                        redisTemplate.opsForList().leftPush("accomplishTaskList_" + taskId, pluginName + ":" + ip + ":" + port);
                    }
                }
            }
        } catch (Exception e) {
            //异常的任务
            //任务未被终止
            if (!currentThread().isInterrupted()) {
                //change set to list
                //异常也要返回检测结果...不然无法标记漏洞修复
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("workType", workType);
                resultMap.put("taskId", taskId);
                resultMap.put("pluginName", pluginName);
                resultMap.put("ip", ip);
                resultMap.put("port", port);
                resultMap.put("scanResult", "scanResult");
                if (!currentThread().isInterrupted()) {
                    rabbitMessagingTemplate.convertAndSend("scanresult", resultMap);
                    redisTemplate.opsForList().leftPush("accomplishTaskList_" + taskId, pluginName + ":" + ip + ":" + port);
                }

            }
            //log一下
            //System.err.println(e + " _ " + ip + ":" + port + " " + pluginName);
            //e.printStackTrace();
            logger.info("CheckerThread Exception here: " + ip + ":" + port + " " + pluginName + " _ " + ExcpUtil.buildErrorMessage(e));
        }

    }

    private void checkPythonImport(String pluginCode) {
        List<String> moduleNameList = new ArrayList<>();
        String regex = "(?:^|)import.*|(?:^|)from.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pluginCode);
        while (matcher.find()) {
            moduleNameList.add(matcher.group().split("\\s")[1]);
        }

        List<String> pipSource = new ArrayList<>(Arrays.asList("https://pypi.douban.com/simple/", "https://pypi.mirrors.ustc.edu.cn/simple/", "https://pypi.tuna.tsinghua.edu.cn/simple/", "https://pypi.hustunique.com/", "https://mirrors.aliyun.com/pypi/simple/"));
        moduleNameList.parallelStream().forEach(moduleName -> {
            try (Interpreter interp = new SharedInterpreter()) {
                interp.exec("import " + moduleName);
            } catch (JepException e) {
                logger.info("Check python import : " + moduleName + " not installed...Installing now");
                int i = 0;
                while (i < 6) {
                    String[] pipArgs;
                    if (i != 5) {
                        pipArgs = new String[]{"pip3", "install", moduleName, "-i", pipSource.get(i)};
                    } else {
                        pipArgs = new String[]{"pip3", "install", moduleName};
                    }
                    try {
                        Process process = Runtime.getRuntime().exec(pipArgs);
                        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                             BufferedReader brE = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {

                            String line;
                            while ((line = br.readLine()) != null) {
//                                System.out.println(line);
                            }
                            while ((line = brE.readLine()) != null) {
//                            System.out.println(line);
                            }
                        }
                    } catch (IOException ioException) {
                    }
                    i++;
                }
                logger.info("Check python import : " + moduleName + " install success");
            }
        });

    }

    private void userPassDict2PayloadMap(Map<String, List<String>> payloadMap, RedisTemplate<String, String> redisTemplate) {

        String redisDictUsername = "dictUsernameList_";
        String redisDictPassword = "dictPasswordList_";
        Boolean existUsername = redisTemplate.hasKey(redisDictUsername);
        Boolean existPassword = redisTemplate.hasKey(redisDictPassword);
        List<String> usernameList = new ArrayList<>();
        List<String> passwordList = new ArrayList<>();
        if (!Objects.isNull(existUsername) && existUsername) {
            usernameList = redisTemplate.opsForList().range(redisDictUsername, 0, -1);
        }
        if (!Objects.isNull(existPassword) && existPassword) {
            passwordList = redisTemplate.opsForList().range(redisDictPassword, 0, -1);
        }

        if (Objects.isNull(usernameList) || usernameList.isEmpty()) {
            usernameList = new ArrayList<>(Arrays.asList("root", "test", "sa", "admin", "postgres", "administrator", "oracle", "system", "weblogic", "cisco", "ftp", "www", "db", "wwwroot", "data", "web", "manager", "jboss", "system"));
        }
        if (Objects.isNull(passwordList) || passwordList.isEmpty()) {
            passwordList = new ArrayList<>(Arrays.asList("admin", "root", "admin123", "admin@123", "admin123456", "root123", "root123456", "123456", "!QAZ@WSX", "1qaz2wsx", "oracle", "system", "postgres"));
        }
        payloadMap.put("username", usernameList);
        payloadMap.put("password", passwordList);
    }

    private void nginxVersion2PayloadMap(String versionName, String timeout, Map<String, List<String>> payloadMap) {
        String mainlineKey = "Mainline version";
        String stableKey = "Stable version";

        //判断redis中是否有version
        Boolean existVersion = redisTemplate.hasKey(versionName);
        //没有，到官网取
        if (Objects.isNull(existVersion) || !existVersion) {
            try {
                Connection connection = Jsoup.connect("http://nginx.org/en/download.html");
                Connection.Response response = connection.execute();
                connection.timeout(Integer.parseInt(timeout));
                Document doc = response.parse();
                Elements mainlineElements = doc.getElementsContainingOwnText(mainlineKey);
                Elements stableElements = doc.getElementsContainingOwnText(stableKey);
                Elements mainlineTable = mainlineElements.parents().next("table");
                Elements stableTable = stableElements.parents().next("table");
                String mainlineVersion = mainlineTable.select("td").eachText().get(1).split("\\s")[0].split("-")[1];
                String stableVersion = stableTable.select("td").eachText().get(1).split("\\s")[0].split("-")[1];

                redisTemplate.opsForValue().set(versionName + mainlineKey, mainlineVersion);
                redisTemplate.opsForValue().set(versionName + stableKey, stableVersion);
                redisTemplate.opsForValue().set(versionName, versionName);

                redisTemplate.expire(versionName + mainlineKey, 1, TimeUnit.DAYS);
                redisTemplate.expire(versionName + stableKey, 1, TimeUnit.DAYS);
                redisTemplate.expire(versionName, 1, TimeUnit.DAYS);
            } catch (Exception ignored) {
            }
        }
        String mainlineVersionInRedis = redisTemplate.opsForValue().get(versionName + mainlineKey);
        String stableVersionInRedis = redisTemplate.opsForValue().get(versionName + stableKey);
        payloadMap.put(mainlineKey, Collections.singletonList(mainlineVersionInRedis));
        payloadMap.put(stableKey, Collections.singletonList(stableVersionInRedis));

    }
}

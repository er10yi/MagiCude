package com.tiji.agent.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.currentThread;


/**
 * Gather
 *
 * @author 贰拾壹
 * @create 2018-08-25 23:25
 */
public class AgentGatherHelper {

    private final static Logger logger = LoggerFactory.getLogger(AgentGatherHelper.class);

    public String getAllAddress() throws SocketException {
        StringBuilder stringBuilder = new StringBuilder();
        InetAddress ip;
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            // 遍历所有ip
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                ip = ips.nextElement();
                if (Objects.isNull(ip)) {
                    continue;
                }
                String sIP = ip.getHostAddress();
                if (Objects.isNull(sIP) || sIP.contains(":")) {
                    continue;
                }
                stringBuilder.append(sIP).append("\n");
            }
        }
        return stringBuilder.toString().trim();
    }

    public Map<String, String> getHttpResponseAndHtmlContent(String url, String userAgent) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        //javax.net.ssl.SSLHandshakeException: Certificates do not conform to algorithm 暂时不能处理
        Map<String, String> resultMap = new HashMap<>();
        StringBuilder urlNameAndLink = new StringBuilder();
        //为本地HttpsURLConnection配置一个“万能证书”
        // 重置HttpsURLConnection的DefaultHostnameVerifier，使其对任意站点进行验证时都返回true
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        // 创建随机证书生成工厂
        //SSLContext context = SSLContext.getInstance("TLS");
        SSLContext context = SSLContext.getInstance("TLSv1.3");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        // 重置httpsURLConnection的DefaultSSLSocketFactory， 使其生成随机证书
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        Connection connection = Jsoup.connect(url);
        connection.followRedirects(true);
        connection.ignoreHttpErrors(true);
        connection.ignoreContentType(true);
        Map<String, String> requestHeaderMap = new HashMap<>();
        requestHeaderMap.put("User-Agent", userAgent);
        requestHeaderMap.put("Connection", "close");
        connection.headers(requestHeaderMap);
        Connection.Response response = connection.execute();


        Document doc = response.parse();
        Element body = doc.body();
        String title = doc.title();
        resultMap.put("title", title);

        Map<String, String> headersMap = response.headers();
        StringBuilder headerBuilder = new StringBuilder();
        headersMap.forEach((key, value) -> headerBuilder.append(key).append(" : ").append(value).append("\n"));
        resultMap.put("header", headerBuilder.toString());
        resultMap.put("response", body.toString());

        //响应头
        //Server
        //X-Powered-By
        //Set-Cookie
        //WWW-Authenticate
        resultMap.put("server", headersMap.get("Server"));
        resultMap.put("x_Powered_By", headersMap.get("X-Powered-By"));
        resultMap.put("set_Cookie", headersMap.get("Set-Cookie"));
        resultMap.put("www_Authenticate", headersMap.get("WWW-Authenticate"));


        //TODO title为空，动态js解析一下？
        //感觉没必要....
        //如果js的语法不严格，htmlunit会报错
        //pom文件需要把htmlunit依赖取消注释
      /*  if (Objects.isNull(title) || (!Objects.isNull(bodyWholeText) && bodyWholeText.contains("JavaScript"))) {
            WebClient wc = new WebClient(BrowserVersion.CHROME);
            //是否使用不安全的SSL
            wc.getOptions().setUseInsecureSSL(true);
            //启用JS解释器，默认为true
            wc.getOptions().setJavaScriptEnabled(true);
            //禁用CSS
            wc.getOptions().setCssEnabled(false);
            //js运行错误时，是否抛出异常
            wc.getOptions().setThrowExceptionOnScriptError(false);
            //状态码错误时，是否抛出异常
            wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
            //是否允许使用ActiveX
            wc.getOptions().setActiveXNative(false);
            //等待js时间
            wc.waitForBackgroundJavaScript(600 * 1000);
            //设置Ajax异步处理控制器即启用Ajax支持
            wc.setAjaxController(new NicelyResynchronizingAjaxController());
            //设置超时时间
            wc.getOptions().setTimeout(1000000);
            //不跟踪抓取
            wc.getOptions().setDoNotTrackEnabled(false);
            WebRequest request = new WebRequest(new URL(url));
            request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");

            //模拟浏览器打开一个目标网址
            HtmlPage htmlPage = wc.getPage(request);
            //Thread.sleep(1000);//这个线程的等待 因为js加载需要时间的
            //以xml形式获取响应文本
            String xml = htmlPage.asXml();

            // Jsoup解析处理
            doc = Jsoup.parse(xml);

            System.out.println("wc url: " + url);
            System.out.println("wc title: " + doc.title());
            System.out.println(doc.wholeText().replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", ""));

        }*/

        //remove blank line
        String bodyWholeText = body.wholeText().replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");

        //body.text().length()<3000
        if (body.text().length() > 3000) {
            bodyWholeText = bodyWholeText.substring(0, 3000);
        }
        resultMap.put("bodyWholeText", bodyWholeText);
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        Elements links = doc.select("a[href]");
        Set<String> totalSet = htmlElements2Set(media, "abs:src");
        totalSet.addAll(htmlElements2Set(imports, "abs:href"));
        totalSet.addAll(htmlElements2Set(links, "abs:href"));
        if (!totalSet.isEmpty()) {
            totalSet.forEach(item -> {
                String itemTemp;
                if (item.contains(".js") && !item.endsWith(".js")) {
                    //去掉.js的时间戳
                    itemTemp = item.split("\\.js")[0] + ".js";
                } else if (item.contains("css?") && !item.endsWith(".css")) {
                    //去掉.css?啥的...
                    itemTemp = item.split("css")[0] + "css";
                } else if (item.contains("png?") && !item.endsWith(".png")) {
                    //去掉png?啥的...
                    itemTemp = item.split("png")[0] + "png";
                } else if (item.contains("svg?") && !item.endsWith(".svg")) {
                    //去掉svg?啥的...
                    itemTemp = item.split("svg")[0] + "svg";
                } else if (item.contains("ico?") && !item.endsWith(".ico")) {
                    //去掉ico?啥的...
                    itemTemp = item.split("ico")[0] + "ico";
                } else if (item.contains("gif?") && !item.endsWith(".gif")) {
                    //去掉gif?啥的...
                    itemTemp = item.split("gif")[0] + "gif";
                }
                //
                else {
                    itemTemp = item;
                }
                String urlName = itemTemp.split("<\\+>")[0];
                String urlLink = itemTemp.split("<\\+>")[1];
                urlNameAndLink.append(urlName).append("<|>").append(urlLink).append("<+>");
            });
        }
        resultMap.put("urlNameAndLink", urlNameAndLink.toString());
        return resultMap;
    }

    private Set<String> htmlElements2Set(Elements elements, String attrKey) {
        Set<String> set = new HashSet<>();
        if (elements.size() > 0) {
            elements.forEach(element -> {
                String attr = element.attr(attrKey);
                String text = element.text();
                if (attr.length() > 0) {
                    set.add(text + "<+>" + attr);
                }
            });
        }
        return set;
    }


    // 用个static final的试试
    //暂时没能将 Runtime.getRuntime().exec换成PROCESS_BUILDER

//    private static final ProcessBuilder PROCESS_BUILDER = new ProcessBuilder();

    public StringBuilder scanResult2StringBuilder(String taskId, String workType, String ip, String port, String options, String rate, String nmapPath, String massPath, RedisTemplate<String, String> redisTemplate) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!currentThread().isInterrupted()) {
            String basePath = massPath;
            StringBuilder cmd;
            if (Objects.isNull(port) || port.isEmpty()) {
                port = "1-65535";
            }
            cmd = new StringBuilder(basePath + " " + ip + " -p" + port + " " + options + " --rate=" + rate);

            if ("nse".equals(workType) || "nmap".equals(workType) || Objects.isNull(rate)) {
                basePath = nmapPath;
                //带-p，结果从mass过来的、nse处理、nmap全端口分组
                if (ip.contains("-p")) {
                    cmd = new StringBuilder(basePath + " " + ip + " " + options);
                } else if ("regular".equals(port)) {
                    cmd = new StringBuilder(basePath + " " + ip + " " + options);
                } else {
                    cmd = new StringBuilder(basePath + " " + ip + " -p" + port + " " + options);
                }
            }
            //nmap -sn，仅仅保留-sn选项
            if (cmd.toString().contains("-sn")) {
                cmd = new StringBuilder(cmd.toString().replaceAll(" -p1-65535", "").replaceAll(options, " -sn"));
            }

            String sliceIPListSizeName = "sliceIPListSize_" + taskId;
            Boolean existSliceIPListSizeName = redisTemplate.hasKey(sliceIPListSizeName);

            if (!Objects.isNull(existSliceIPListSizeName) && existSliceIPListSizeName) {

                try {
                    Process process = Runtime.getRuntime().exec(cmd.toString());
                    //System.out.println(format + " PID " + process.pid() + " start. Command: " + cmd);
                    //扫描日志
                    long pid = process.pid();
                    logger.info(taskId + " " + workType + " PID " + pid + " start. Command: " + cmd);

                    if (!currentThread().isInterrupted()) {
                        //change set to list
                        redisTemplate.opsForList().leftPush("totalTaskList_" + taskId, pid + "");
                    }

                    if (!currentThread().isInterrupted()) {
                        //扫描进程结束
                        CompletableFuture<Process> completableFuture = process.onExit();
                        // Print a message when process terminates
                        completableFuture.thenAcceptAsync(processOnExit -> {
                            if (!currentThread().isInterrupted()) {
                                if (!Objects.isNull(processOnExit) && !processOnExit.isAlive()) {
                                    //change set to list
                                    redisTemplate.opsForList().leftPush("accomplishTaskList_" + taskId, processOnExit.pid() + "");
                                    //扫描日志
                                    logger.info(taskId + " " + workType + " Job " + processOnExit.pid() + " terminated");
                                    processOnExit.destroy();
                                }
                            }
                        });
                    }


                    try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                         BufferedReader brE = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                        //启动一个新进程，用于清空ErrorStream，防止process阻塞
                        //lambda替代匿名Runnable
                        new Thread(() -> {
                            try {
                                while (brE.readLine() != null) {
                                }
                            } catch (Exception ignored) {

                            }
                        }).start();

                        String line;
                        //127.0.0.1 -p6379 --script redis-info
                        if ("nse".equals(workType)) {
                            stringBuilder.append(ip);
                        }
                        while ((line = br.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                    } catch (IOException ignored) {
                    }
                } catch (IOException ignored) {

                }
            }
            return stringBuilder;
        }
        return null;
    }


}
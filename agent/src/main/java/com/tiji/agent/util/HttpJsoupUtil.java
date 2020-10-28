package com.tiji.agent.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author 贰拾壹
 * @create 2020-09-02 21:59
 */
public class HttpJsoupUtil {
    public static StringBuilder getResponse(String ip, String port, String userAgent, String jsonString) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        StringBuilder resultStringBuilder = new StringBuilder();
        if (!Objects.isNull(jsonString) && !jsonString.isEmpty()) {

            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            //url
            JsonElement jsonProtocol = jsonObject.get("protocol");
            String protocol;
            if (Objects.isNull(jsonProtocol)) {
                protocol = "http";
            } else {
                protocol = jsonProtocol.getAsString();
            }
            //https证书
            if ("https".equals(protocol)) {
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
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

            }

            JsonElement jsonUrl = jsonObject.get("url");
            String tempUrl = jsonUrl.getAsString();
            String url = protocol + "://" + ip + ":" + port + tempUrl;

            //method
            JsonElement jsonMethod = jsonObject.get("method");
            String tempMethod;
            if (Objects.isNull(jsonMethod)) {
                tempMethod = "get";
            } else {
                tempMethod = jsonMethod.getAsString();
            }
            Connection.Method method = switch (tempMethod.toLowerCase()) {
                case "post" -> Connection.Method.POST;
                case "put" -> Connection.Method.PUT;
                case "delete" -> Connection.Method.DELETE;
                case "patch" -> Connection.Method.PATCH;
                case "head" -> Connection.Method.HEAD;
                case "options" -> Connection.Method.OPTIONS;
                case "trace" -> Connection.Method.TRACE;
                default -> Connection.Method.GET;
            };

            Connection connection = Jsoup.connect(url);
            connection.followRedirects(true);
            connection.ignoreHttpErrors(true);
            connection.ignoreContentType(true);

            connection.header("User-Agent", userAgent);

            //header
            JsonElement jsonHeaders = jsonObject.get("headers");
            String contentType = null;
            if (!Objects.isNull(jsonHeaders)) {
                Set<Map.Entry<String, JsonElement>> dataEntrySet = jsonHeaders.getAsJsonObject().entrySet();
                for (Map.Entry<String, JsonElement> entry : dataEntrySet) {
                    if (entry.getKey().equals("Content-Type")) {
                        contentType = entry.getValue().getAsString();
                    }
                    connection.header(entry.getKey(), entry.getValue().getAsString());
                }
            }

            //data
            JsonElement jsonData = jsonObject.get("data");
            if (!Objects.isNull(jsonData)) {
                //json格式参数
                if ("application/json".equals(contentType)) {
                    connection.requestBody(jsonData.getAsJsonObject().toString());
                } else {
                    //常规参数
                    jsonData.getAsJsonObject().entrySet().forEach(entry -> connection.data(entry.getKey(), entry.getValue().getAsString()));
                }
            }

            //response
            Connection.Response response = connection.method(method).execute();

            resultStringBuilder.append("*********** response headers ***********").append("\n");

            //headers
            response.headers().forEach((key, value) -> resultStringBuilder.append(key).append(" : ").append(value).append("\n"));
            Document doc = response.parse();
            String title = doc.title();
            resultStringBuilder.append("*********** title ***********").append("\n");
            resultStringBuilder.append(title).append("\n");
            resultStringBuilder.append("*********** body ***********").append("\n");

            resultStringBuilder.append(doc);
        }
        return resultStringBuilder;
    }

}

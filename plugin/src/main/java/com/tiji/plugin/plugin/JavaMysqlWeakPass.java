package com.tiji.plugin.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author 贰拾壹
 * @create 2020-04-04 0:54
 */
public class JavaMysqlWeakPass {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) throws ClassNotFoundException {
        List<String> usernameList = payloadMap.get("username");
        List<String> passwordList = payloadMap.get("password");
        passwordList.add("");
        StringBuilder resultStringBuilder = new StringBuilder();
        boolean usernameFlag = false;


        Class.forName("com.mysql.jdbc.Driver");

        for (String username : usernameList) {
            if (usernameFlag) {
                break;
            }
            for (String password : passwordList) {
                DriverManager.setLoginTimeout(Integer.parseInt(timeout) / 1000);
                try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + cmd + "?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=" + timeout + "&socketTimeout=" + timeout, username, password)) {
                    resultStringBuilder.append("用户名密码: ").append(username).append(":").append(password).append("\n");
                    resultStringBuilder.append("catalog: ").append(connection.getCatalog());
                    connection.close();
                    usernameFlag = true;
                    break;
                } catch (SQLException e) {
                    //e.printStackTrace();
                }
            }
        }
        return resultStringBuilder;
    }
}

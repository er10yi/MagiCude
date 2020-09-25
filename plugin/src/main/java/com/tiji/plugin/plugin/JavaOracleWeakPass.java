package com.tiji.plugin.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author 贰拾壹
 * @create 2020-04-27 11:53
 */
public class JavaOracleWeakPass {

    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) throws ClassNotFoundException {
        List<String> usernameList = payloadMap.get("username");
        List<String> passwordList = payloadMap.get("password");
        Collections.shuffle(usernameList);
        Collections.shuffle(passwordList);

        StringBuilder resultStringBuilder = new StringBuilder();
        boolean usernameFlag = false;
        boolean dbFlag = false;
        Class.forName("oracle.jdbc.driver.OracleDriver");

        String[] splitCMDs = cmd.split(",");
        //每个账号9次，默认超过十次会锁定
        int count = 1;
        Collections.shuffle(Arrays.asList(splitCMDs));
        for (String splitCMD : splitCMDs) {
            if (dbFlag) {
                break;
            }
            for (String username : usernameList) {
                if (usernameFlag) {
                    break;
                }
                for (String password : passwordList) {
                    DriverManager.setLoginTimeout(Integer.parseInt(timeout) / 1000);
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@//" + ip + ":" + port + "/" + splitCMD, username, password)) {
                        resultStringBuilder.append("用户名密码: ").append(username).append(":").append(password).append("\n");
                        resultStringBuilder.append("server name: ").append(splitCMD).append("\n");
                        resultStringBuilder.append("connection: ").append(connection.toString()).append("\n");
                        resultStringBuilder.append("schema: ").append(connection.getSchema());
                        connection.close();
                        usernameFlag = true;
                        dbFlag = true;
                        break;
                    } catch (SQLException e) {
                        //if (e.toString().contains("the account is locked")) {
                        //    System.out.println(username + " " + password);
                        //}
                        //System.out.println(e.toString());
                        //e.printStackTrace();
                    }
                    if (count == 9) {
                        count = 1;
                        break;
                    }
                    count++;
                }
            }
        }
        return resultStringBuilder;
    }

}

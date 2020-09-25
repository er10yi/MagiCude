package com.tiji.plugin.plugin;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 贰拾壹
 * @create 2020-04-08 16:32
 */
public class JavaFTPWeakPass {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) {
        List<String> usernameList = payloadMap.get("username");
        List<String> passwordList = payloadMap.get("password");
        usernameList.add(0, "ftp");
        passwordList.add(0, "ftp");

        StringBuilder resultStringBuilder = new StringBuilder();
        boolean usernameFlag = false;
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(Integer.parseInt(timeout));
        for (String username : usernameList) {
            if (usernameFlag) {
                break;
            }
            for (String password : passwordList) {
                try {
                    ftpClient.connect(ip, Integer.parseInt(port));
                    int reply = ftpClient.getReplyCode();

                    if (!FTPReply.isPositiveCompletion(reply)) {
                        ftpClient.disconnect();
                        return null;
                    }
                    if (!ftpClient.login(username, password)) {
                        ftpClient.logout();
                        ftpClient.disconnect();
                    } else {
                        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                        ftpClient.enterLocalPassiveMode();
                        ftpClient.setControlEncoding("UTF-8");
                        FTPFile[] listFiles = ftpClient.listFiles();
                        if (!Objects.isNull(listFiles)) {
                            int dirLength = Math.min(listFiles.length, Integer.parseInt(cmd));
                            for (int i = 0; i < dirLength; i++) {
                                resultStringBuilder.append(listFiles[i].toFormattedString()).append("\n");
                            }
                        }
                        ftpClient.logout();
                        ftpClient.disconnect();

                        if ("ftp".equals(username)) {
                            resultStringBuilder.insert(0, "FTP允许匿名访问\n");
                        } else {
                            resultStringBuilder.insert(0, "用户名密码: " + username + ":" + password + "\n");
                        }
                        usernameFlag = true;
                        break;
                    }
                } catch (ConnectException e) {
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        } catch (IOException ignored) {
                        }
                    }
                    usernameFlag = true;
                    break;
                } catch (IOException e) {
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        } catch (IOException ignored) {
                        }
                    }
                } finally {
                    if (ftpClient.isConnected()) {
                        try {
                            ftpClient.logout();
                            ftpClient.disconnect();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ignored) {
            }
        }
        return resultStringBuilder;
    }
}
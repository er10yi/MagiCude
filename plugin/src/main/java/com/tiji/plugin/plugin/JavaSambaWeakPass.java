package com.tiji.plugin.plugin;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

/**
 * @author 贰拾壹
 * @create 2020-04-30 12:24
 */
public class JavaSambaWeakPass {
    public StringBuilder start(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) {
        List<String> usernameList = payloadMap.get("username");
        List<String> passwordList = payloadMap.get("password");
        StringBuilder resultStringBuilder = new StringBuilder();
        boolean usernameFlag = false;
        resultStringBuilder.append("SambaWeakPass\n");
        try {
            SmbFile smbFileNoPass = new SmbFile("smb://" + ip + "/");
            smbFileNoPass.setConnectTimeout(Integer.parseInt(timeout));
            if (!smbFileNoPass.exists()) {
                resultStringBuilder.append("匿名访问\n");
                resultStringBuilder.append("No file list");
            } else {
                SmbFile[] files = smbFileNoPass.listFiles();
                if (files.length != 0) {
                    resultStringBuilder.append("匿名访问\n");
                    for (SmbFile f : files) {
                        resultStringBuilder.append(f.getName()).append("\n");
                    }
                } else {
                    resultStringBuilder.append("Empty samba files");
                }
            }
        } catch (MalformedURLException | SmbException e) {
            //e.printStackTrace();
        }

        for (String username : usernameList) {
            if (usernameFlag) {
                break;
            }
            for (String password : passwordList) {
                try {
                    SmbFile smbFile = new SmbFile("smb://" + username + ":" + password + "@" + ip + "/");
                    smbFile.setConnectTimeout(Integer.parseInt(timeout));
                    if (!smbFile.exists()) {
                        resultStringBuilder.append("用户名密码: ").append(username).append(":").append(password).append("\n");
                        resultStringBuilder.append("\nNo file list");
                    } else {
                        SmbFile[] files = smbFile.listFiles();
                        if (files.length != 0) {
                            resultStringBuilder.append("用户名密码: ").append(username).append(":").append(password).append("\n");
                            for (SmbFile f : files) {
                                resultStringBuilder.append(f.getName()).append("\n");
                            }
                        } else {
                            resultStringBuilder.append("Empty samba files");
                        }
                    }
                    usernameFlag = true;
                    break;

                } catch (MalformedURLException | SmbException e) {
                    //e.printStackTrace();
                }
            }
        }
        return resultStringBuilder;
    }
}

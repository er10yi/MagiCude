package com.tiji.plugin.plugin;

import com.tiji.plugin.util.PluginUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * @author 贰拾壹
 * @create 2020-04-03 15:18
 */
public class JavaMongoDbListDb {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (Socket socket = new Socket(ip, Integer.parseInt(port));
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();) {
            socket.setSoTimeout(Integer.parseInt(timeout));
            out.write(PluginUtil.hexStr2Byte(cmd));
            out.flush();
            byte[] bytes = new byte[2048];
            int length = in.read(bytes);
            String result = new String(bytes, 0, length);
            if (!result.contains("errmsg")) {
                resultStringBuilder.append(result);
            }
        } catch (IOException ignored) {
        }
        return resultStringBuilder;
    }
}

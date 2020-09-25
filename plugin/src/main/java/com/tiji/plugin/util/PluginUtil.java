package com.tiji.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author 贰拾壹
 * @create 2020-04-01 23:05
 */
public class PluginUtil {
    public static StringBuilder getSocketResult(String ip, String port, String cmd, String timeout) {
        StringBuilder resultStringBuilder = new StringBuilder();
        String command = cmd + "\r\n";

        try (Socket socket = new Socket(ip, Integer.parseInt(port));
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();) {
            String result = PluginUtil.exeCommand(timeout, command, socket, out, in);
            resultStringBuilder.append(result);
        } catch (IOException ignored) {
        }
        return resultStringBuilder;
    }

    /**
     * 16进制字符串转换成为2进制
     *
     * @param hex
     * @return
     */
    public static byte[] hexStr2Byte(String hex) {
        ByteBuffer bf = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            bf.put(b);
        }
        return bf.array();
    }

    public static String exeCommand(String timeout, String command, Socket socket, OutputStream out, InputStream in) throws IOException {
        socket.setSoTimeout(Integer.parseInt(timeout));
        out.write(command.getBytes(StandardCharsets.UTF_8));
        out.flush();
        byte[] bytes = new byte[2048];
        int length = in.read(bytes);
        return new String(bytes, 0, length, StandardCharsets.UTF_8);
    }
}

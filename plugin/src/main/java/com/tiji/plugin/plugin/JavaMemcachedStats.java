package com.tiji.plugin.plugin;

import com.tiji.plugin.util.PluginUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Memcached插件
 *
 * @author 贰拾壹
 * @create 2018-09-05 15:26
 */

public class JavaMemcachedStats {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) throws IOException {
        return PluginUtil.getSocketResult(ip, port, cmd, timeout);
    }
}

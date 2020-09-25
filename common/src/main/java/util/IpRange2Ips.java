package util;

import org.apache.commons.net.util.SubnetUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.LongStream;

/**
 * 解析ip地址
 * ip段转换成ip
 * 单个ip不变
 * 192.168.1.1 -> ip
 * cidr换成单个ip
 * 192.168.1.1/24 -> ips
 * ip段换成单个ip
 * 192.168.1.1-192.168.2.2 -> ips
 *
 * @author 贰拾壹
 * @create 2019-06-24 19:12
 */

public class IpRange2Ips {
    private static Long ip2num(String ip) {
        String[] ips = ip.split("\\.");
        return Long.parseLong(ips[0]) << 24 | Long.parseLong(ips[1]) << 16 | Long.parseLong(ips[2]) << 8 | Long.parseLong(ips[3]);
    }

    private static String num2ip(Long num) {
        return ((num >> 24) & 0xff) + "." + ((num >> 16) & 0xff) + "." + ((num >> 8) & 0xff) + "." + (num & 0xff);
    }

    public static Set<String> genIp(String targetIp) {
        Set<String> set = new LinkedHashSet<>();
        if (targetIp.contains("-")) {
            Long start = ip2num(targetIp.split("-")[0]);
            Long end = ip2num(targetIp.split("-")[1]);
            LongStream.rangeClosed(start, end).forEach(ip -> {
                if ((ip & 0xff) == 0)
                    return;
                set.add(num2ip(ip));
            });
        } else if (targetIp.contains("/")) {
            SubnetUtils subnetUtils = new SubnetUtils(targetIp);
            String[] IPs = subnetUtils.getInfo().getAllAddresses();
            set.addAll(Arrays.asList(IPs));
        } else {
            set.add(targetIp);
        }
        return set;
    }
}

package com.tiji.plugin.plugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 贰拾壹
 * @create 2020-04-21 13:58
 */
public class JavaNginxVerDetect {
    public static StringBuilder check(String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        String rawVersion = payloadMap.get("rawVersion").get(0);
        String mainlineVersion = payloadMap.get("Mainline version").get(0);
        String stableVersion = payloadMap.get("Stable version").get(0);
        String[] splitVersion = rawVersion.split("\\s");
        if (splitVersion.length == 2) {
            String realVersion = rawVersion.split("\\s")[1];
            if (!Objects.isNull(realVersion) && !realVersion.isEmpty()) {
                int realCpMainline = compareVersion(mainlineVersion, realVersion);
                int realCpStable = compareVersion(stableVersion, realVersion);
                //当前nginx为最新的mainlineVersion或者stableVersion
                if (realCpMainline == 0 || realCpStable == 0) {
                    resultStringBuilder.append(rawVersion).append(" is up to date");
                } else {
                    //小于主线版本
                    if (realCpMainline == 1) {
                        resultStringBuilder.append(rawVersion).append(" is out of date\n");
                        resultStringBuilder.append("Mainline version: ").append(mainlineVersion).append("\n");
                    }
                    //小于稳定版
                    if (realCpStable == 1) {
                        resultStringBuilder.append("Stable version: ").append(stableVersion);
                    }
                    //TODO CVE版本比较，应该很简单...好像可以从官网的changelog找cve号

                }
            }
        }
        return resultStringBuilder;
    }

    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，1代表左边大，-1代表右边大
     * Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
     */
    private static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen && (diff = Long.parseLong(version1Array[index]) - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }

    }
}

package com.tiji.agent.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author 贰拾壹
 * @create 2019-08-15 11:29
 */
public class JavaPluginChecker {
    public static StringBuilder start(String className, String ip, String port, String cmd, String timeout, Map<String, List<String>> payloadMap) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = Class.forName("com.tiji.plugin.plugin." + className);
        Method startMethod = clazz.getMethod("check", String.class, String.class, String.class, String.class, Map.class);
        Object obj = clazz.getDeclaredConstructor().newInstance();
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(startMethod.invoke(obj, ip, port, cmd, timeout, payloadMap));
        return resultBuilder;
    }

}

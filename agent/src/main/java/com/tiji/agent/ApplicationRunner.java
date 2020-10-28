package com.tiji.agent;

import jep.MainInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 * @author 贰拾壹
 * 启动时运行的方法
 * @create 2020-08-10 21:20
 */
@Component
public class ApplicationRunner implements org.springframework.boot.ApplicationRunner {
    @Value("${jep.absolutePath}")
    private String absolutePath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MainInterpreter.setJepLibraryPath(absolutePath);
    }
}

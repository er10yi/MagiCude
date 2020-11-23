package com.tiji.agent;

import com.tiji.agent.util.AgentGatherHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import util.ExcpUtil;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * agent启动时发送配置信息
 *
 * @author 贰拾壹
 * @create 2019-10-09 17:31
 */
@Component
public class ConfigSenderApplicationRunner implements ApplicationRunner {
    private final static Logger logger = LoggerFactory.getLogger(ConfigSenderApplicationRunner.class);
    @Value("${nmap.path}")
    private String nmapPath;
    @Value("${mass.path}")
    private String massPath;
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    private String allAddress;

    @Override
    public void run(ApplicationArguments args) {
        try {
            AgentGatherHelper agentGatherHelper = new AgentGatherHelper();
            InetAddress addr;
            String agentName;
            try {
                addr = InetAddress.getLocalHost();
                agentName = "agent_" + addr.getHostName();
            } catch (UnknownHostException e) {
                //e.printStackTrace();
                agentName = "agent_" + "UnknownHostException";
            }


            Map<String, String> agentConfig = new HashMap<>();
            agentConfig.put("agentName", agentName);
            agentConfig.put("nmapPath", nmapPath);
            agentConfig.put("massPath", massPath);
            agentConfig.put("online", agentName);
            try {
                allAddress = agentGatherHelper.getAllAddress();
            } catch (SocketException e) {
                allAddress = e.getLocalizedMessage();
            }
            agentConfig.put("ipAddress", allAddress);

            rabbitMessagingTemplate.convertAndSend("agentconfig", agentConfig);
        } catch (Exception e) {
            logger.error("ConfigSenderApplicationRunner Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }


}

package com.tiji.center.schedule;

import com.tiji.center.pojo.Agent;
import com.tiji.center.service.AgentService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import util.ExcpUtil;
import util.IdWorker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 贰拾壹
 * @create 2019-09-30 10:32
 */
public class AgentHeartbeatMonitorScheduler implements Job {
    private final static Logger logger = LoggerFactory.getLogger(AgentHeartbeatMonitorScheduler.class);
    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;
    @Autowired
    private AgentService agentService;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            List<Agent> agentList1 = agentService.findAll();
            for (Agent agent : agentList1) {
                int timeouts = Integer.parseInt(agent.getTimeouts());
                if (timeouts == 5) {
                    if (agent.getOnline()) {
                        agent.setOnline(false);
                        agent.setTimeouts("5");
                        agentService.update(agent);
                    }
                } else if (timeouts < 5) {
                    if (!agent.getOnline()) {
                        agent.setTimeouts(++timeouts + "");
                        agentService.update(agent);
                    }
                }
            }

            Message<?> agentConfigMessage = rabbitMessagingTemplate.receive("agentconfig");

            boolean flag1 = false;
            boolean flag2;
            if (Objects.isNull(agentConfigMessage)) {
                flag1 = true;
            } else {
                //将所有agent的online标志置为false
                agentService.updateAgentSetOnlineFalse();
                getAgentConfigMessage(agentConfigMessage);
                //TijiHelper.getAgentConfigMessage(agentService,idWorker,agentConfigMessage);
                //getAgentConfigMessage(agentConfigMessage);
            }
            flag2 = getHeartbeat();
            if (flag1 && flag2) {
                List<Agent> agentList = agentService.findAll();
                for (Agent agent : agentList) {
                    if (agent.getOnline()) {
                        agent.setOnline(false);
                        agentService.update(agent);
                    }
                }
                System.out.println("no agent online");
            }
            System.out.println("sending heartbeat");
            sendHeartbeat();
        } catch (Exception e) {
            logger.error("AgentHeartbeatMonitorScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }


    //TODO 处理agent cpu和内存状态
    private void getAgentConfigMessage(Message<?> agentConfigMessage) {
        if (!Objects.isNull(agentConfigMessage)) {
            Map<String, String> agentConfig = (Map<String, String>) agentConfigMessage.getPayload();
            String agentName = agentConfig.get("agentName");
            String nmapPath = agentConfig.get("nmapPath");
            String massPath = agentConfig.get("massPath");
            String online = agentConfig.get("online");
            String ipAddress = agentConfig.get("ipAddress");
            String onlineFlag = online + ipAddress;

            if (!Objects.isNull(agentName) && !Objects.isNull(nmapPath) && !Objects.isNull(massPath) && !Objects.isNull(online)) {
                Agent dbAgent = agentService.findByNameAndIpaddress(agentName, ipAddress);
                //新增一个agent记录
                if (Objects.isNull(dbAgent)) {
                    agentService.add(new Agent(idWorker.nextId() + "", agentName, nmapPath, massPath, ipAddress, true, "0"));
                }
            }
            //
            if (!Objects.isNull(online)) {
                List<Agent> agentList = agentService.findAll();
                for (Agent agent : agentList) {
                    String name = agent.getName();
                    String ipaddress = agent.getIpaddress();
                    if (!onlineFlag.equals(name + ipaddress)) {
                        //agent.setOnline(false);
                    } else {
                        agent.setOnline(true);
                        agent.setTimeouts("0");
                    }
                    agentService.update(agent);
                }
            }
        }
    }

    private boolean getHeartbeat() {
        while (true) {
            Message<?> agentConfigMessage = rabbitMessagingTemplate.receive("agentconfig");
            if (!Objects.isNull(agentConfigMessage)) {
                getAgentConfigMessage(agentConfigMessage);
                //TijiHelper.getAgentConfigMessage(agentService,idWorker,agentConfigMessage);
            } else {
                return true;
            }
        }
    }

    private void sendHeartbeat() {
        List<Agent> agentList = agentService.findAll();
        if (!agentList.isEmpty()) {
            Map<String, String> taskConfig = new HashMap<>();
            taskConfig.put("status", "heartbeat");
            //rabbitMessagingTemplate.convertAndSend("heartbeatfanout", "", taskConfig);
            rabbitMessagingTemplate.convertAndSend("tijifanout", "", taskConfig);
        }
    }
}

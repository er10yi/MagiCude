package com.tiji.agent.thread;


import com.tiji.agent.util.AgentGatherHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.currentThread;

/**
 * @author 贰拾壹
 * @create 2019-08-29 11:23
 */
public class ParserThread implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ParserThread.class);
    private final String taskId, userAgent, workType, url, assetPortId;
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final RedisTemplate<String, String> redisTemplate;

    public ParserThread(String taskId, String userAgent, String workType, String assetPortIdWithUrlAndPort, RabbitMessagingTemplate rabbitMessagingTemplate, RedisTemplate<String, String> redisTemplate) {
        this.taskId = taskId;
        this.userAgent = userAgent;
        this.workType = workType;
        this.assetPortId = assetPortIdWithUrlAndPort.split("\\|")[0];
        this.url = assetPortIdWithUrlAndPort.split("\\|")[1];
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void run() {
        try {
            //结果丢map里返回
            if (!currentThread().isInterrupted()) {
                AgentGatherHelper agentGatherHelper = new AgentGatherHelper();

                String sliceIPListSizeName = "sliceIPListSize_" + taskId;
                Boolean existSliceIPListSizeName = redisTemplate.hasKey(sliceIPListSizeName);
                if (!Objects.isNull(existSliceIPListSizeName) && existSliceIPListSizeName) {
                    Map<String, String> parserResultMap = agentGatherHelper.getHttpResponseAndHtmlContent(url, userAgent);
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("workType", workType);
                    resultMap.put("taskId", taskId);
                    resultMap.put("assetPortId", assetPortId);
                    resultMap.put("url", url);
                    resultMap.put("server", parserResultMap.get("server"));
                    resultMap.put("x_Powered_By", parserResultMap.get("x_Powered_By"));
                    resultMap.put("set_Cookie", parserResultMap.get("set_Cookie"));
                    resultMap.put("www_Authenticate", parserResultMap.get("www_Authenticate"));
                    resultMap.put("title", parserResultMap.get("title"));
                    resultMap.put("bodyWholeText", parserResultMap.get("bodyWholeText"));
                    resultMap.put("scanResult", parserResultMap.get("urlNameAndLink"));

                    if (!currentThread().isInterrupted()) {
                        rabbitMessagingTemplate.convertAndSend("scanresult", resultMap);

                        //change set to list
                        redisTemplate.opsForList().leftPush("accomplishTaskList_" + taskId, assetPortId + "_" + url);
                    }
                }
            }
        } catch (Exception e) {
            //异常的任务
            //任务未被终止
            //http paser不会标记漏洞，所以异常不用返回
            if (!currentThread().isInterrupted()) {
                //change set to list
                redisTemplate.opsForList().leftPush("accomplishTaskList_" + taskId, assetPortId + "_" + url);

            }
            //log一下
            //System.err.println(e + " _ " + taskId + " " + url);
            logger.info("ParserThread Exception here: " + taskId + " " + url + " _ " + e);
        }
    }
}

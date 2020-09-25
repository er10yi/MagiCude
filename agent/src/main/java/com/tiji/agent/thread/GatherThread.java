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
 * Gather多线程
 *
 * @author 贰拾壹
 * @create 2019-08-15 17:21
 */

public class GatherThread implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(GatherThread.class);
    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final String ip, port, options, rate, workType, nmapPath, massPath, taskId;
    private final RedisTemplate<String, String> redisTemplate;

    public GatherThread(String taskId, String workType, String ip, RabbitMessagingTemplate rabbitMessagingTemplate, String port, String options, String rate, String nmapPath, String massPath, RedisTemplate<String, String> redisTemplate) {
        this.taskId = taskId;
        this.ip = ip;
        this.port = port;
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
        this.options = options;
        this.rate = rate;
        this.workType = workType;
        this.nmapPath = nmapPath;
        this.massPath = massPath;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run() {
        try {
            if (!currentThread().isInterrupted()) {
                String sliceIPListSizeName = "sliceIPListSize_" + taskId;
                Boolean existSliceIPListSizeName = redisTemplate.hasKey(sliceIPListSizeName);
                if (!Objects.isNull(existSliceIPListSizeName) && existSliceIPListSizeName) {
                    AgentGatherHelper agentGatherHelper = new AgentGatherHelper();
                    StringBuilder scanResult = agentGatherHelper.scanResult2StringBuilder(taskId, workType, ip, port, options, rate, nmapPath, massPath, redisTemplate);
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("workType", workType);
                    resultMap.put("taskId", taskId);
                    if (scanResult.length() == 0) {
                        resultMap.put("scanResult", "scanResult");
                    } else {
                        resultMap.put("scanResult", scanResult.toString());
                    }
                    if (!currentThread().isInterrupted()) {
                        rabbitMessagingTemplate.convertAndSend("scanresult", resultMap);
                    }
                }
            }
            //} catch (IOException e) {
        } catch (Exception e) {
            //需要向accomplishTaskList 加1，获取不到process的pid
            if (!currentThread().isInterrupted()) {
                //change set to list
                //异常也要返回检测结果...不然无法标记漏洞修复
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("workType", workType);
                resultMap.put("taskId", taskId);
                resultMap.put("scanResult", "scanResult");
                if (!currentThread().isInterrupted()) {
                    rabbitMessagingTemplate.convertAndSend("scanresult", resultMap);
                    redisTemplate.opsForList().leftPush("accomplishTaskList_" + taskId, ip);
                }

            }
            logger.info("GatherThread Exception here: " + taskId + " " + ip + " _ " + e);
        }
    }
}

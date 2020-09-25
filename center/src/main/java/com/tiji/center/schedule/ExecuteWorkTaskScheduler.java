package com.tiji.center.schedule;


import com.tiji.center.pojo.Nmapconfig;
import com.tiji.center.service.NmapconfigService;
import com.tiji.center.service.TaskDispatcherService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import util.ExcpUtil;

import java.util.Objects;

/**
 * @author 贰拾壹
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class ExecuteWorkTaskScheduler implements Job {

    private final static Logger logger = LoggerFactory.getLogger(ExecuteWorkTaskScheduler.class);
    @Autowired
    private TaskDispatcherService taskDispatcherService;
    @Autowired
    private NmapconfigService nmapconfigService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            String taskId = (String) jobDataMap.get("taskId");
            String taskTargetIp = (String) jobDataMap.get("taskTargetIp");
            String taskWorkType = (String) jobDataMap.get("taskWorkType");
            if ("assetip".equals(taskTargetIp) && ("nse".equals(taskWorkType) || "selfd".equals(taskWorkType) || "httpp".equals(taskWorkType))) {
                taskDispatcherService.executeTotalCheck(taskId);
            } else {
                //标准任务
                if ("mass2Nmap".equals(taskWorkType)) {
                    Nmapconfig nmapconfig = nmapconfigService.findByTaskid(taskId);
                    if (Objects.isNull(nmapconfig)) {
                        throw new RuntimeException("nmap配置为空");
                    }
                }
                try {
                    taskDispatcherService.executeWork(taskId);
                } catch (InterruptedException e) {
                    logger.info("taskDispatcherService Exception here: " + e);
                }
            }
        } catch (Exception e) {
            logger.error("ExecuteWorkTaskScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

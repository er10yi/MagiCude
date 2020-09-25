package com.tiji.center.schedule;


import com.tiji.center.service.TaskDispatcherService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import util.ExcpUtil;

/**
 * @author 贰拾壹
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class ExecuteTotalCheckTaskScheduler implements Job {

    private final static Logger logger = LoggerFactory.getLogger(AgentHeartbeatMonitorScheduler.class);
    @Autowired
    private TaskDispatcherService taskDispatcherService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            String taskId = (String) jobDataMap.get("taskId");
            taskDispatcherService.executeTotalCheck(taskId);
        } catch (Exception e) {
            logger.error("ExecuteTotalCheckTaskScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

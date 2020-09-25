package com.tiji.center;

import com.tiji.center.pojo.Cronjob;
import com.tiji.center.pojo.Task;
import com.tiji.center.schedule.*;
import com.tiji.center.schedule.quartz.QuartzJob;
import com.tiji.center.schedule.quartz.QuartzJobService;
import com.tiji.center.service.*;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xbill.DNS.Record;
import org.xbill.DNS.*;
import util.ExcpUtil;
import util.IdWorker;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author 贰拾壹
 * 启动时运行的方法
 * @create 2019-09-30 10:15
 */
@Component
public class MonitorApplicationRunner implements ApplicationRunner {
    private final static Logger logger = LoggerFactory.getLogger(MonitorApplicationRunner.class);
    @Autowired
    private QuartzJobService quartzJobService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CronjobService cronjobService;
    @Autowired
    private DictionaryusernameService dictionaryusernameService;
    @Autowired
    private DictionarypasswordService dictionarypasswordService;
    @Autowired
    private UseragentService useragentService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void run(ApplicationArguments args) {

        try {
            //设置Http辅助验证关键词
            setHttpValidateKey();
            //dns辅助验证
            startDNSServer();

            //刷新redis中的用户名密码字典
            freshDictUsernameRedisCache();
            //刷新redis中的UserAgent
            freshUserAgentRedisCache();

            //每10秒，监控所有任务状态
            Cronjob taskStatusMonitor = cronjobService.findByName("任务状态监控");
            if (!Objects.isNull(taskStatusMonitor.getCronexpression()) && !taskStatusMonitor.getCronexpression().isEmpty()) {
                runScheduler(taskStatusMonitor.getCronexpression(), TaskStatusMonitorScheduler.class);
            }

            //每5分钟，监控agent心跳包
            Cronjob agentHeartbeatMonitor = cronjobService.findByName("agent心跳包监控");
            if (!Objects.isNull(agentHeartbeatMonitor.getCronexpression()) && !agentHeartbeatMonitor.getCronexpression().isEmpty()) {
                runScheduler(agentHeartbeatMonitor.getCronexpression(), AgentHeartbeatMonitorScheduler.class);
            }
            //凌晨3点，凌晨执行的所有任务都丢这里
            //dns解析获取hostname
            //nse结果获取hostname
            //根据ip更新白名单
            //根据项目更新白名单
            Cronjob midnightTask = cronjobService.findByName("每天执行一次的任务");
            if (!Objects.isNull(midnightTask.getCronexpression()) && !midnightTask.getCronexpression().isEmpty()) {
                runScheduler(midnightTask.getCronexpression(), MidnightTaskScheduler.class);
            }
            //早上7:33，发资产报告
            Cronjob assetNotify = cronjobService.findByName("邮件资产报告");
            if (!Objects.isNull(assetNotify.getCronexpression()) && !assetNotify.getCronexpression().isEmpty()) {
                runScheduler(assetNotify.getCronexpression(), AssetNotifyScheduler.class);
            }
            //早上7:03，发漏洞报告
            Cronjob vulnNotify = cronjobService.findByName("邮件漏洞报告");
            if (!Objects.isNull(vulnNotify.getCronexpression()) && !vulnNotify.getCronexpression().isEmpty()) {
                runScheduler(vulnNotify.getCronexpression(), VulnNotifyScheduler.class);
            }
            //早上6点，统计报表数据
            Cronjob statistics = cronjobService.findByName("统计报表数据");
            if (!Objects.isNull(statistics.getCronexpression()) && !statistics.getCronexpression().isEmpty()) {
                runScheduler(statistics.getCronexpression(), StatisticsScheduler.class);
            }
            //恢复所有非子任务的cron任务
            resumeCronTask();
        } catch (Exception e) {
            logger.error("MonitorApplicationRunner Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }

    private void resumeCronTask() throws SchedulerException {
        //查询所有非子任务的cron任务
        List<Task> cronTaskList = taskService.findAllByCrontaskIsTrueAndTaskparentidIsNull();
        for (Task task : cronTaskList) {
            String taskId = task.getId();
            String worktype = task.getWorktype();
            String taskTargetIp = task.getTargetip();
            String taskWorkType = task.getWorktype();

            if (!Objects.isNull(worktype) && !worktype.isEmpty()) {
                String cronExpression = task.getCronexpression();
                if (!Objects.isNull(cronExpression) && !cronExpression.isEmpty()) {
                    String jobKeyName = "jobKeyName_" + taskId;
                    String jobKeyGroup = "jobKeyGroup_" + taskId;
                    JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
                    if ("mass".equals(worktype) || "mass2Nmap".equals(worktype) || "nmap".equals(worktype)) {
                        Map<String, Object> jobDataMap = new HashMap<>();
                        jobDataMap.put("taskId", taskId);
                        jobDataMap.put("taskTargetIp", taskTargetIp);
                        jobDataMap.put("taskWorkType", taskWorkType);
                        //jobDataMap.put("taskInfo", taskInfo);

                        QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, jobDataMap, ExecuteWorkTaskScheduler.class);
                        quartzJobService.scheduleJob(quartzJob);
                    }
                    boolean checkTask = "nse".equals(worktype) || "selfd".equals(worktype) || "httpp".equals(worktype);
                    if (checkTask) {
                        Map<String, String> jobDataMap = new HashMap<>();
                        jobDataMap.put("taskId", taskId);

                        if ("assetip".equals(task.getTargetip())) {
                            QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, jobDataMap, ExecuteTotalCheckTaskScheduler.class);
                            quartzJobService.scheduleJob(quartzJob);
                        } else {
                            QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, jobDataMap, ExecuteCheckTaskScheduler.class);
                            quartzJobService.scheduleJob(quartzJob);
                        }

                    }
                }
            }
        }
    }

    private void runScheduler(String cronExpression, Class<? extends Job> jobClass) throws SchedulerException {
        String jobKeyName = "jobKeyName_" + jobClass.getSimpleName();
        String jobKeyGroup = "jobKeyGroup_" + jobClass.getSimpleName();
        JobKey jobKey = JobKey.jobKey(jobKeyName, jobKeyGroup);
        QuartzJob quartzJob = new QuartzJob(jobKey, cronExpression, null, jobClass);
        quartzJobService.scheduleJob(quartzJob);

    }

    /**
     * 设置用户名密码字典
     */
    private void freshDictUsernameRedisCache() {
        String redisDictUsername = "dictUsernameList_";
        String redisDictPassword = "dictPasswordList_";
        redisTemplate.delete(redisDictUsername);
        redisTemplate.delete(redisDictPassword);
        List<String> allUsername = dictionaryusernameService.findAllUsername();
        List<String> allPassword = dictionarypasswordService.findAllPassword();
        if (!allUsername.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(redisDictUsername, allUsername);
        }
        if (!allPassword.isEmpty()) {
            redisTemplate.opsForList().leftPushAll(redisDictPassword, allPassword);
        }
    }

    /**
     * 设置ua
     */
    private void freshUserAgentRedisCache() {
//        String redisUserAgent = "userAgentList_";
        String redisUserAgent = "userAgentSet_";
        redisTemplate.delete(redisUserAgent);
        List<String> allUserAgent = useragentService.findAllDistinctUserAgentList();
        allUserAgent.parallelStream().forEach(ua -> redisTemplate.opsForSet().add(redisUserAgent, ua));
//        if (!allUserAgent.isEmpty()) {
//            redisTemplate.opsForList().leftPushAll(redisUserAgent, allUserAgent);
//        }
    }


    /**
     * 设置Http辅助验证关键词
     */
    private void setHttpValidateKey() {
        redisTemplate.opsForValue().set("HttpValidateKey_", idWorker.nextId() + "");
    }

    /**
     * 启动DNS辅助验证服务
     */
    private void startDNSServer() throws SocketException {
        DatagramSocket socket = new DatagramSocket(53);
        new Thread(() -> {
            InetAddress sourceIpAddr;
            int sourcePort;
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    sourceIpAddr = request.getAddress();
                    sourcePort = request.getPort();
                    Message message = new Message(request.getData());
                    Record question = message.getQuestion();
                    String domain = question.getName().toString();
                    logger.info("============= DNS Query Start =================");
                    logger.info("sourceIpAddr: " + sourceIpAddr);
                    logger.info("sourcePort: " + sourcePort);
                    logger.info("message: " + message);
                    logger.info("question: " + question);
                    logger.info("domain: " + domain);
                    logger.info("============= DNS Query End =================");
                    try {
                        //正常域名
                        sendResponse(socket, sourceIpAddr, sourcePort, message, question, Address.getByName(domain));
                    } catch (UnknownHostException e) {
                        //未知域名，全部指向本机ip，用于selfd插件dns辅助验证自定义域名
                        sendResponse(socket, sourceIpAddr, sourcePort, message, question, InetAddress.getLocalHost());
                    }
                } catch (IOException e) {
                    logger.error("MonitorApplicationRunner DNSServer Exception: " + ExcpUtil.buildErrorMessage(e));
                }
            }
        }).start();

    }

    private void sendResponse(DatagramSocket socket, InetAddress targetIp, int targetPort, Message message, Record record, InetAddress inetAddress) throws IOException {
        Record answer = new ARecord(record.getName(), record.getDClass(), 64, inetAddress);
        message.getHeader().setFlag(Flags.AA);
        message.getHeader().setFlag(Flags.QR);
        message.addRecord(answer, Section.ANSWER);
        byte[] buffer = message.toWire();
        DatagramPacket response = new DatagramPacket(buffer, buffer.length, targetIp, targetPort);
        socket.send(response);
    }
}

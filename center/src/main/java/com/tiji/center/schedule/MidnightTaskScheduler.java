package com.tiji.center.schedule;


import com.tiji.center.service.*;
import com.tiji.center.util.HostNameUtil;
import com.tiji.center.util.WhitelistUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import util.ExcpUtil;
import util.IdWorker;

import java.util.Date;

/**
 * @author 贰拾壹
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MidnightTaskScheduler implements Job {

    private final static Logger logger = LoggerFactory.getLogger(MidnightTaskScheduler.class);
    @Autowired
    private AssetipService assetipService;
    @Autowired
    private AssetportService assetportService;
    @Autowired
    private HostService hostService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CheckresultService checkresultService;
    @Autowired
    private ProjectinfoService projectinfoService;
    @Autowired
    private IpwhitelistService ipwhitelistService;
    @Autowired
    private IpportwhitelistService ipportwhitelistService;
    @Autowired
    private ProjectportwhitelistService projectportwhitelistService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            //dns解析获取hostname
            try {
                HostNameUtil.dnsResolver(assetipService, hostService, idWorker, new Date());
            } catch (InterruptedException e) {
                logger.info("dnsResolver Exception here: " + e);
            }
            //nse结果获取hostname
            HostNameUtil.nseResultParser(assetportService, checkresultService, hostService, idWorker, new Date());

            //根据ip更新白名单
            WhitelistUtil.markIpWhitelist(assetipService, assetportService, ipwhitelistService, ipportwhitelistService);
            //根据项目更新白名单
            WhitelistUtil.markProjectInfoWhitelist(projectinfoService, assetipService, assetportService, projectportwhitelistService);
        } catch (Exception e) {
            logger.error("MidnightTaskScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }
}

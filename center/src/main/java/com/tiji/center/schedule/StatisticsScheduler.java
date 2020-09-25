package com.tiji.center.schedule;


import com.tiji.center.pojo.Statistics;
import com.tiji.center.service.StatisticsService;
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
import java.util.Map;
import java.util.Objects;

/**
 * @author 贰拾壹
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class StatisticsScheduler implements Job {

    private final static Logger logger = LoggerFactory.getLogger(StatisticsScheduler.class);
    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private IdWorker idWorker;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {

            //Statistics统计数据更新
            String ipCount = statisticsService.findIpCount();
            String ipCountOnline = statisticsService.findIpCountOnline();
            String portCount = statisticsService.findPortCount();
            String portCountOnline = statisticsService.findPortCountOnline();

            Map<String, String> checkresultCountMap = statisticsService.findCheckresultCountMap();
            Map<String, String> checkresultCountOnlineMap = statisticsService.findCheckresultCountOnlineMap();

            String riskportCount = statisticsService.findRiskportCount();
            String riskportCountOnline = statisticsService.findRiskportCountOnline();
            String riskserviceCount = statisticsService.findRiskserviceCount();
            String riskserviceCountOnline = statisticsService.findRiskserviceCountOnline();
            String riskversionCount = statisticsService.findRiskversionCount();
            String riskversionCountOnline = statisticsService.findRiskversionCountOnline();

            long fatalLong = parseRiskCount2Long(checkresultCountMap, "致命");
            long criticalLong = parseRiskCount2Long(checkresultCountMap, "严重");
            long highLong = parseRiskCount2Long(checkresultCountMap, "高危");
            long mediumLong = parseRiskCount2Long(checkresultCountMap, "中危");
            long lowLong = parseRiskCount2Long(checkresultCountMap, "低危");
            long infoLong = parseRiskCount2Long(checkresultCountMap, "信息");


            long fatalLongOnline = parseRiskCount2Long(checkresultCountOnlineMap, "致命");
            long criticalLongOnline = parseRiskCount2Long(checkresultCountOnlineMap, "严重");
            long highLongOnline = parseRiskCount2Long(checkresultCountOnlineMap, "高危");
            long mediumLongOnline = parseRiskCount2Long(checkresultCountOnlineMap, "中危");
            long lowLongOnline = parseRiskCount2Long(checkresultCountOnlineMap, "低危");
            long infoLongOnline = parseRiskCount2Long(checkresultCountOnlineMap, "信息");

            long checkresultCount = fatalLong + criticalLong + highLong + mediumLong + lowLong + infoLong;
            long checkresultCountOnline = fatalLongOnline + criticalLongOnline + highLongOnline + mediumLongOnline + lowLongOnline + infoLongOnline;

            Statistics statistics = new Statistics(idWorker.nextId() + "",
                    ipCount, ipCountOnline, portCount, portCountOnline,
                    String.valueOf(checkresultCount), String.valueOf(checkresultCountOnline),
                    String.valueOf(infoLong), String.valueOf(lowLong), String.valueOf(mediumLong), String.valueOf(highLong), String.valueOf(criticalLong), String.valueOf(fatalLong),
                    String.valueOf(infoLongOnline), String.valueOf(lowLongOnline), String.valueOf(mediumLongOnline), String.valueOf(highLongOnline), String.valueOf(criticalLongOnline), String.valueOf(fatalLongOnline),
                    String.valueOf(riskportCount), String.valueOf(riskportCountOnline), String.valueOf(riskserviceCount), String.valueOf(riskserviceCountOnline), String.valueOf(riskversionCount), String.valueOf(riskversionCountOnline),
                    new Date());
            statisticsService.add(statistics);

        } catch (Exception e) {
            logger.error("StatisticsScheduler Exception here: " + ExcpUtil.buildErrorMessage(e));
        }
    }

    public long parseRiskCount2Long(Map<String, String> checkresultCountMap, String risk) {
        String riskInMap = checkresultCountMap.get(risk);
        return Objects.isNull(riskInMap) ? 0 : Long.parseLong(riskInMap);

    }
}

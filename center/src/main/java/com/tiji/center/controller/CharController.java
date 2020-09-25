package com.tiji.center.controller;

import com.tiji.center.service.StatisticsService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * char控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/char")
public class CharController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 查询ip端口数
     *
     * @return
     */
    @RequestMapping(value = "/ipportcount", method = RequestMethod.GET)
    public Result findIpPortCount() {
        List<String> ipPortCountList = statisticsService.findIpPortCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询未下线ip端口数
     *
     * @return
     */
    @RequestMapping(value = "/ipportcountonline", method = RequestMethod.GET)
    public Result findIpPortCountOnline() {
        List<String> ipPortCountList = statisticsService.findIpPortCountOnline();
        return getResult(ipPortCountList);
    }

    public Result getResult(List<String> ipPortCountList) {
        Map<String, String> singleMap;
        List<Map<String, String>> resulList = new ArrayList<>();
        for (String ipportcount : ipPortCountList) {
            singleMap = new HashMap<>();
            String name = ipportcount.split(",")[0];
            String value = ipportcount.split(",")[1];
            singleMap.put("name", name);
            singleMap.put("value", value);
            resulList.add(singleMap);
        }
        return new Result(true, StatusCode.OK, "查询成功", resulList);
    }


    /**
     * 查询服务数
     *
     * @return
     */
    @RequestMapping(value = "/servicecount", method = RequestMethod.GET)
    public Result findServiceCount() {
        List<String> ipPortCountList = statisticsService.findServiceCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询未下线服务数
     *
     * @return
     */
    @RequestMapping(value = "/servicecountonline", method = RequestMethod.GET)
    public Result findServiceCountOnline() {
        List<String> ipPortCountList = statisticsService.findServiceCountOnline();
        return getResult(ipPortCountList);
    }

    /**
     * 查询版本
     *
     * @return
     */
    @RequestMapping(value = "/versioncount", method = RequestMethod.GET)
    public Result findVersionCount() {
        List<String> ipPortCountList = statisticsService.findVersionCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询未下线版本数
     *
     * @return
     */
    @RequestMapping(value = "/versioncountonline", method = RequestMethod.GET)
    public Result findVersionCountOnline() {
        List<String> ipPortCountList = statisticsService.findVersionCountOnline();
        return getResult(ipPortCountList);
    }

    /**
     * 查询webinfo中server数
     *
     * @return
     */
    @RequestMapping(value = "/webinfoservercount", method = RequestMethod.GET)
    public Result findWebinfoServerCount() {
        List<String> ipPortCountList = statisticsService.findWebinfoServerCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询高危端口数
     *
     * @return
     */
    @RequestMapping(value = "/riskportcount", method = RequestMethod.GET)
    public Result findRiskPortCount() {
        List<String> ipPortCountList = statisticsService.findRiskPortCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询未下线高危端口数
     *
     * @return
     */
    @RequestMapping(value = "/riskportcountonline", method = RequestMethod.GET)
    public Result findRiskPortCountOnline() {
        List<String> ipPortCountList = statisticsService.findRiskPortCountOnline();
        return getResult(ipPortCountList);
    }

    /**
     * 查询高危服务数
     *
     * @return
     */
    @RequestMapping(value = "/riskservicecount", method = RequestMethod.GET)
    public Result findRiskServiceCount() {
        List<String> ipPortCountList = statisticsService.findRiskServiceCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询未下线高危服务数
     *
     * @return
     */
    @RequestMapping(value = "/riskservicecountonline", method = RequestMethod.GET)
    public Result findRiskServiceCountOnline() {
        List<String> ipPortCountList = statisticsService.findRiskServiceCountOnline();
        return getResult(ipPortCountList);
    }

    /**
     * 查询高危版本数
     *
     * @return
     */
    @RequestMapping(value = "/riskversioncount", method = RequestMethod.GET)
    public Result findRiskVersionCount() {
        List<String> ipPortCountList = statisticsService.findRiskVersionCount();
        return getResult(ipPortCountList);
    }

    /**
     * 查询未下线高危版本数
     *
     * @return
     */
    @RequestMapping(value = "/riskversioncountonline", method = RequestMethod.GET)
    public Result findRiskVersionCountOnline() {
        List<String> ipPortCountList = statisticsService.findRiskVersionCountOnline();
        return getResult(ipPortCountList);
    }


    /**
     * 查询所有风险数
     *
     * @return
     */
    @RequestMapping(value = "/riskcount", method = RequestMethod.GET)
    public Result findRiskCount() {
        List<String> ipPortCountList = statisticsService.findRiskCount();
        return getResult(ipPortCountList);
    }


    /**
     * 查询未修复风险数
     *
     * @return
     */
    @RequestMapping(value = "/riskcountonline", method = RequestMethod.GET)
    public Result findRiskCountOnline() {
        List<String> ipPortCountList = statisticsService.findRiskCountOnline();
        return getResult(ipPortCountList);
    }

    /**
     * 查询所有漏洞数
     *
     * @return
     */
    @RequestMapping(value = "/riskvulncount", method = RequestMethod.GET)
    public Result findRiskVulnCount() {
        List<String> ipPortCountList = statisticsService.findRiskVulnCount();
        return getResult(ipPortCountList);
    }


    /**
     * 查询未修复漏洞数
     *
     * @return
     */
    @RequestMapping(value = "/riskvulncountonline", method = RequestMethod.GET)
    public Result findRiskVulnCountOnline() {
        List<String> ipPortCountList = statisticsService.findRiskVulnCountOnline();
        return getResult(ipPortCountList);
    }

    /**
     * 查询statistics
     *
     * @return
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public Result findAllStatistics() {
        return new Result(true, StatusCode.OK, "查询成功", statisticsService.findAll());
    }

}

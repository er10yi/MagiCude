package com.tiji.center.util;

import com.tiji.center.pojo.Assetip;
import com.tiji.center.pojo.Assetport;
import com.tiji.center.pojo.Checkresult;
import com.tiji.center.pojo.Host;
import com.tiji.center.service.AssetipService;
import com.tiji.center.service.AssetportService;
import com.tiji.center.service.CheckresultService;
import com.tiji.center.service.HostService;
import com.tiji.center.thread.DNSResolveThread;
import org.springframework.util.DigestUtils;
import util.IdWorker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 贰拾壹
 * @create 2019-11-22 15:10
 */
public class HostNameUtil {

    public static void dnsResolver(AssetipService assetipService, HostService hostService, IdWorker idWorker, Date date) throws InterruptedException {
        //DNS解析
        BlockingQueue<String> rawResultQueue = new LinkedBlockingQueue<>();
        List<String> allIpaddressv4 = assetipService.findAllDistinctIpaddressv4ListAndPassivetimeIsNull();
        List<Host> activeHostList = new ArrayList<>();
        System.setProperty("networkaddress.cache.ttl", "0");
        System.setProperty("networkaddress.cache.negative.ttl", "0");
        for (String ipv4 : allIpaddressv4) {
            getDNSResolve(rawResultQueue, ipv4);
        }

        for (String rawResult : rawResultQueue) {
            boolean addHostFlag = true;
            String ip = rawResult.split(":")[0];
            Assetip assetip = assetipService.findByIpaddressv4AndPassivetimeIsNull(ip);
            if (assetip != null) {
                String assetipId = assetip.getId();
                String hostname = rawResult.split(":")[1];
                String temp = hostname.split("\\.")[0];
                String owner = temp.substring(temp.indexOf("-") + 1);
                String md5Base = hostname + owner;
                String incomeDigest = DigestUtils.md5DigestAsHex(md5Base.toLowerCase().getBytes());
                List<Host> hostList = hostService.findByAssetIpId(assetipId);
                if (!hostList.isEmpty()) {
                    for (Host host : hostList) {
                        String hostname1 = host.getHostname();
                        String owner1 = host.getOwner();
                        String dbBase = hostname1 + owner1;
                        String dbDigest = DigestUtils.md5DigestAsHex(dbBase.toLowerCase().getBytes());
                        if (incomeDigest.equals(dbDigest)) {
                            addHostFlag = false;
                        }
                    }
                }
                if (addHostFlag) {
                    //TODO 这里可以得到新增的host信息或者发生改变的host信息
                    Host host = new Host(idWorker.nextId() + "", assetipId, null, hostname, null, null, null, owner, date, null);
                    activeHostList.add(host);
                }
            }
        }
        hostService.batchAdd(activeHostList);
    }

    public static void nseResultParser(AssetportService assetportService, CheckresultService checkresultService, HostService hostService, IdWorker idWorker, Date date) {
        List<Host> nseActiveHostList = new ArrayList<>();
        //nse插件：根据ntlm信息获取DNS_Computer_Name
        //DNS_Computer_Name
        //资产端口编号->资产ip编号
        //判断host是否存在
        //不存在，新增
        String[] ntlmNseNameArray = new String[]{"http-ntlm-info", "imap-ntlm-info", "ms-sql-ntlm-info", "nntp-ntlm-info", "pop3-ntlm-info", "rdp-nlm-info", "smtp-ntlm-info", "telnet-ntlm-info"};
        List<Checkresult> checkresultList = new ArrayList<>();
        //根据nse获取检测结果
        for (String ntlmNseName : ntlmNseNameArray) {
            checkresultList.addAll(checkresultService.findAllByName(ntlmNseName));
        }
        //获取资产端口编号
        for (Checkresult checkresult : checkresultList) {
            String assetportid = checkresult.getAssetportid();
            String result = checkresult.getResult();
            Pattern pattern = Pattern.compile("(?:^|\\n)DNS_Computer_Name.*");
            Matcher matcher = pattern.matcher(result);
            String hostname = null;
            if (matcher.find()) {
                String dnsComputerName = matcher.group(0);
                hostname = dnsComputerName.split(":\\s")[1];
            }
            //nse检查结果中hostname不为空
            if (!Objects.isNull(hostname)) {
                boolean addHostFlag = true;
                Assetport assetport = assetportService.findById(assetportid);
                //获取资产ip编号
                String assetipid = assetport.getAssetipid();
                String temp = hostname.split("\\.")[0];
                String owner = temp.substring(temp.indexOf("-") + 1);
                //获取host
                List<Host> hostList = hostService.findByAssetIpId(assetipid);
                //当前不存在host
                if (hostList.isEmpty()) {
                    //TODO 这里可以得到新增的host信息
                    nseActiveHostList.add(new Host(idWorker.nextId() + "", assetipid, null, hostname, null, null, null, owner, date, "add by " + checkresult.getName()));
                } else {
                    //存在host
                    String md5Base = hostname + owner;
                    String incomeDigest = DigestUtils.md5DigestAsHex(md5Base.toLowerCase().getBytes());
                    for (Host host : hostList) {
                        String hostname1 = host.getHostname();
                        String owner1 = host.getOwner();
                        String dbBase = hostname1 + owner1;
                        String dbDigest = DigestUtils.md5DigestAsHex(dbBase.toLowerCase().getBytes());
                        if (incomeDigest.equals(dbDigest)) {
                            addHostFlag = false;
                        }
                    }
                    if (addHostFlag) {
                        //host信息有更改
                        nseActiveHostList.add(new Host(idWorker.nextId() + "", assetipid, null, hostname, null, null, null, owner, date, "change by " + checkresult.getName()));
                    }
                }
            }
        }
        hostService.batchAdd(nseActiveHostList);
    }

    public static void getDNSResolve(BlockingQueue<String> rawResultQueue, String rawIP) throws InterruptedException {
        String[] ip_split = rawIP.split("\\.");
        //ip一到四位，String直接转byte会溢出，先转short，再由short强转成byte
        byte ip1 = (byte) Short.parseShort(ip_split[0]);
        byte ip2 = (byte) Short.parseShort(ip_split[1]);
        byte ip3 = (byte) Short.parseShort(ip_split[2]);
        byte ip4 = (byte) Short.parseShort(ip_split[3]);
        byte[] ip = new byte[]{ip1, ip2, ip3, ip4};
        DNSResolveThread dnsLookupThread = new DNSResolveThread(ip, rawIP, rawResultQueue);
        Thread thread = new Thread(dnsLookupThread);
        thread.start();
        //设置解析线程存活的时间，超时终止
        thread.join(10);
    }
}

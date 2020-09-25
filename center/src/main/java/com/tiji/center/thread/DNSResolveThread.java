package com.tiji.center.thread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

/**
 * @author 贰拾壹
 * @create 2019-01-07 14:34
 */

public class DNSResolveThread implements Runnable {
    private InetAddress inetAddress;
    private byte[] addr;
    private String rawIP;
    private BlockingQueue<String> rawResultQueue;

    public DNSResolveThread() {
    }

    public DNSResolveThread(byte[] addr, String rawIp, BlockingQueue<String> rawResultQueue) {
        this.addr = addr;
        this.rawIP = rawIp;
        this.rawResultQueue = rawResultQueue;
    }

    @Override
    public void run() {
        try {
            InetAddress add = InetAddress.getByAddress(addr);
            set(add);
        } catch (UnknownHostException ignored) {
        }
    }

    private synchronized void set(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        if (!this.rawIP.equals(getIPHostName())) {
            try {
                rawResultQueue.put(this.rawIP + ":" + getIPHostName());
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    private synchronized String getIPHostName() {
        return this.inetAddress == null ? null : inetAddress.getHostName();
    }
}

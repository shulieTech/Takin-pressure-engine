/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.flpt.pressure.engine.util;

import com.sun.management.OperatingSystemMXBean;
import io.shulie.flpt.pressure.engine.entity.health.DiskUsage;
import io.shulie.flpt.pressure.engine.entity.health.LoadInfo;
import io.shulie.flpt.pressure.engine.entity.health.NetUsageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author angju
 * @date 2018/5/17 13:27
 */
public class SystemResourceUtil {
    private static final Logger logger = LoggerFactory.getLogger(SystemResourceUtil.class);

    private static final int CPUTIME = 500;
    private static final int PERCENT = 100;
    private static final int FAULTLENGTH = 10;
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final String NET_CARD_MAX_SPEED = "1000";


    /**
     * 获得Linux cpu使用率
     * @return float efficiency
     * @throws IOException
     * @throws InterruptedException
     */
    public static float getCpuInfo() throws IOException, InterruptedException {
        File file = new File("/proc/stat");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        StringTokenizer token = new StringTokenizer(br.readLine());
        token.nextToken();
        long user1 = Long.parseLong(token.nextToken() + "");
        long nice1 = Long.parseLong(token.nextToken() + "");
        long sys1 = Long.parseLong(token.nextToken() + "");
        long idle1 = Long.parseLong(token.nextToken() + "");


        Thread.sleep(1000);


        br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)));
        token = new StringTokenizer(br.readLine());
        token.nextToken();
        long user2 = Long.parseLong(token.nextToken());
        long nice2 = Long.parseLong(token.nextToken());
        long sys2 = Long.parseLong(token.nextToken());
        long idle2 = Long.parseLong(token.nextToken());


        return (float) ((user2 + sys2 + nice2) - (user1 + sys1 + nice1)) * 100
                / (float) ((user2 + nice2 + sys2 + idle2) - (user1 + nice1
                + sys1 + idle1));
    }


    /**
     * 获得cpu使用率
     * @return
     */
    public static double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                    + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";


            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);


            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));


            if ((c0 != null) && (c1 != null)) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];


                return Double.valueOf(
                        (PERCENT * (busytime) * 1.0) / (busytime + idletime))
                        .intValue();
            } else {
                return 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();


            return 0;
        }
    }


    /**
     * 读取cpu相关信息
     * @param proc
     * @return
     */
    private static long[] readCpu(final Process proc) {
        long[] retn = new long[2];


        try {
            proc.getOutputStream().close();


            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();


            if ((line == null) || (line.length() < FAULTLENGTH)) {
                return null;
            }


            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;


            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }


                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = substring(line, capidx, cmdidx - 1).trim();
                String cmd = substring(line, cmdidx, kmtidx - 1).trim();


                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }


                String s1 = substring(line, kmtidx, rocidx - 1).trim();
                String s2 = substring(line, umtidx, wocidx - 1).trim();


                if (caption.equals("System Idle Process")
                        || caption.equals("System")) {
                    if (s1.length() > 0) {
                        idletime += Long.valueOf(s1).longValue();
                    }


                    if (s2.length() > 0) {
                        idletime += Long.valueOf(s2).longValue();
                    }


                    continue;
                }


                if (s1.length() > 0) {
                    kneltime += Long.valueOf(s1).longValue();
                }


                if (s2.length() > 0) {
                    usertime += Long.valueOf(s2).longValue();
                }
            }


            retn[0] = idletime;
            retn[1] = kneltime + usertime;


            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                logger.error("error {}",e);
            }
        }


        return null;
    }


    /**
     * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
     *
     * @param src
     *            要截取的字符串
     * @param start_idx
     *            开始坐标（包括该坐标)
     * @param end_idx
     *            截止坐标（包括该坐标）
     * @return
     */
    private static String substring(String src, int start_idx, int end_idx) {
        byte[] b = src.getBytes();
        String tgt = "";


        for (int i = start_idx; i <= end_idx; i++) {
            tgt += (char) b[i];
        }


        return tgt;
    }


    /**
     * 获取free内存
     * @return
     */
    public static long getFreeMemery() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        // 总的物理内存
        Long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        return freePhysicalMemorySize/1024/1024;
    }

    /**
     * 获取总内存
     * @return
     */
    public static long getTotalMemery() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        // 总的物理内存
        Long totalPhysicalMemorySize = osmxb.getTotalPhysicalMemorySize();
        return totalPhysicalMemorySize/1024/1024;
    }

    // 获取总内存
    public static long getUsedMemery() {
        return getTotalMemery() - getFreeMemery();
    }


    public static int getCpuNum(){
        int cpuNum = 0;
        Runtime r = Runtime.getRuntime();
        String command = "cat /proc/stat";
        BufferedReader in1 = null;
        Process pro1 = null;
        try{
            pro1 = r.exec(command);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while((line=in1.readLine()) != null){
                if(line.contains("cpu")){
                    cpuNum++;
                }
            }
            return cpuNum - 1;
        }catch (Exception e){
            logger.error("get load error:",e);
            return cpuNum;
        }finally {
            if(in1 != null){
                try {
                    in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(pro1 != null){
                pro1.destroy();
            }
        }
    }

    public static LoadInfo getLoad(){
        LoadInfo loadInfo = new LoadInfo();
        Runtime r = Runtime.getRuntime();
        String command = "top -b -n 1";
        BufferedReader in1 = null;
        Process pro1 = null;
        try{
            pro1 = r.exec(command);
            pro1.waitFor(2,TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while((line=in1.readLine()) != null){
                if(line.contains("top")){
                    String temp = line.split("load average:")[1];
                    loadInfo.setLoad_1(temp.split(",")[0]);
                    loadInfo.setLoad_2(temp.split(",")[1]);
                    loadInfo.setLoad_3(temp.split(",")[2]);
                    break;
                }
            }
            return loadInfo;
        }catch (Exception e){
            logger.error("get load error:",e);
            return loadInfo;
        }finally {
            if(in1 != null){
                try {
                    in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(pro1 != null){
                pro1.destroy();
            }
        }
    }

    public static String getIoWait(){
        //%Cpu(s):  1.5 us,  0.2 sy,  0.0 ni, 98.2 id,  0.1 wa,  0.0 hi,  0.0 si,  0.0 st
        String ioWait = null;
        Runtime r = Runtime.getRuntime();
        String command = "top -b -n 1";
        BufferedReader in1 = null;
        Process pro1 = null;
        try{
            pro1 = r.exec(command);
            pro1.waitFor(2,TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while((line=in1.readLine()) != null){
                if(line.contains("Cpu(s)")){
                    ioWait = line.split(",")[4];
                    break;
                }
            }
            return ioWait;
        }catch (Exception e){
            logger.error("getIoWait error:",e);
            return ioWait;
        }finally {
            if(in1 != null){
                try {
                    in1.close();
                } catch (IOException e) {
                    logger.error("error {}",e);
                }
            }
            if(pro1 != null){
                pro1.destroy();
            }
        }
    }

    public static List<DiskUsage> getDiskUsage(){
        Runtime r = Runtime.getRuntime();
        String command = "iostat -d -x 1 1";
        BufferedReader in1 = null;
        Process pro1 = null;
        List<DiskUsage> diskUsageList = new ArrayList<>();
        try{
            pro1 = r.exec(command);
            pro1.waitFor(2,TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while((line=in1.readLine()) != null){
                if(line.contains("Device")){
//                    System.out.println("Device info:"+line);
                }else if(line.contains("Linux")){
//                    System.out.println("Linux info:"+line);
                }else if(StringUtils.isNotBlank(line)){
                    DiskUsage diskUsage = new DiskUsage();
                    diskUsage.setDiskName(line.split(" ")[0]);
                    diskUsage.setUtil(line.split(" ")[line.split(" ").length-1]);
                    diskUsageList.add(diskUsage);
                }
            }
            return diskUsageList;
        }catch (Exception e){
            logger.error("getDiskUsage error:",e);
            return diskUsageList;
        }finally {
            if(in1 != null){
                try {
                    in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(pro1 != null){
                pro1.destroy();
            }
        }
    }



    private static List<NetUsageInfo> getNetCard(){
        String agentIp = LOCAL_ADDRESS;
        Process pro1 = null;
        Runtime r = Runtime.getRuntime();
        BufferedReader in1 = null;
        List<NetUsageInfo> netUsageInfoList = new ArrayList<>();
        try {
//            String command = "ifconfig";
            String[] command = {"/bin/sh", "-c", "ifconfig"};

            //第一次采集流量数据
            pro1 = r.exec(command);
            pro1.waitFor(2,TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            NetUsageInfo netUsageInfo = null;
            while((line=in1.readLine()) != null){
                if(line.contains("flags") || line.contains("encap:Ethernet")){
                    if(netUsageInfo != null && netUsageInfo.getIp() != null){
                        netUsageInfoList.add(netUsageInfo);
                    }
                    netUsageInfo = new NetUsageInfo();
                    netUsageInfo.setName(line.split(" ")[0]);
                }else if(line.contains("inet") && !line.contains("inet6")){
                    if(netUsageInfo != null && line.contains(agentIp)){
                        netUsageInfo.setIp(agentIp);
                    }
                }
//                    System.out.println(line);
            }
            return netUsageInfoList;
        } catch (Exception e) {
            logger.error("getNetUsage error:",e);
            return netUsageInfoList;
        } finally {
            try {
                if(in1 != null){
                    in1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(pro1 != null){
                pro1.destroy();
            }
        }
    }

    private static void getMaxNetSpeed(List<NetUsageInfo> list){
        String temp = NET_CARD_MAX_SPEED;
        if(StringUtils.isNotBlank(temp)){
            for(NetUsageInfo netUsageInfo : list){
                netUsageInfo.setMaxSpeed(temp);
            }
        }else {
            for(NetUsageInfo netUsageInfo : list){
                Process pro1 = null;
                Runtime r = Runtime.getRuntime();
                BufferedReader in1 = null;
                try {
                    String command = "ethtool " + netUsageInfo.getName();
                    //第一次采集流量数据
                    pro1 = r.exec(command);
                    pro1.waitFor(2,TimeUnit.SECONDS);
                    in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
                    String line;
                    while((line=in1.readLine()) != null){
                        if(line.startsWith("Speed")){
                            netUsageInfo.setMaxSpeed(line.replace("Speed:","").replaceAll(" ",""));
                        }
                    }
                } catch (Exception e) {
                    logger.error("getMaxNetSpeed error:",e);
                } finally {
                    try {
                        if(in1 != null){
                            in1.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(pro1 != null){
                        pro1.destroy();
                    }
                }
            }
        }
    }


    private static void getNetUsage(List<NetUsageInfo> list){
        for(NetUsageInfo netUsageInfo : list){
            Process pro1 = null;
            Process pro2 = null;
            Runtime r1 = Runtime.getRuntime();
            Runtime r2 = Runtime.getRuntime();
            BufferedReader in1 = null;
            BufferedReader in2 = null;
            try {
                String command = "cat /proc/net/dev";
                //第一次采集流量数据
                pro1 = r1.exec(command);
                pro1.waitFor(2,TimeUnit.SECONDS);
                Thread.sleep(1000);
                pro2 = r2.exec(command);
                pro2.waitFor(2,TimeUnit.SECONDS);
                in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
                in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
                String line;
                long receive1 = 0;
                long send1 = 0 ;
                long receive2 = 0;
                long send2 = 0;
                while((line=in1.readLine()) != null){
                    if(line.contains(netUsageInfo.getName())){
                        String [] temp = line.replace(netUsageInfo.getName(),"").split("\\s+");
                        receive1 = Long.valueOf(temp[2]);
                        send1 = Long.valueOf(temp[9]);
                    }
                }

                while((line=in2.readLine()) != null){
                    if(line.contains(netUsageInfo.getName())){
                        String [] temp = line.replace(netUsageInfo.getName(),"").split("\\s+");
                        receive2 = Long.valueOf(temp[2]);
                        send2 = Long.valueOf(temp[9]);
                    }
                }
                BigDecimal receive = new BigDecimal(receive2 - receive1);
                BigDecimal send = new BigDecimal(send2 - send1);
                netUsageInfo.setReceive(receive.divide(new BigDecimal(1024*1024),6, RoundingMode.HALF_UP).floatValue());
                netUsageInfo.setSend(send.divide(new BigDecimal(1024*1024),6, RoundingMode.HALF_UP).floatValue());
            } catch (Exception e) {
                logger.error("getNetUsage error:",e);
            } finally {
                try {
                    if(in1 != null){
                        in1.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if(in2 != null){
                        in2.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(pro1 != null){
                    pro1.destroy();
                }

                if(pro2 != null){
                    pro2.destroy();
                }
            }
        }

    }


    public static List<NetUsageInfo> getNetUsageInfo(){
        List<NetUsageInfo> list = getNetCard();
        getMaxNetSpeed(list);
        getNetUsage(list);
        return list;
    }

    public static Map<String ,Object> getServerInfo() {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("cpu", getCpuInfo());
        } catch (Exception e) {
            logger.error("【cpu】" + e.getMessage(), e);
        }

        try {
            map.put("memery", getUsedMemery());
        } catch (Exception e) {
            logger.error("【memery】" + e.getMessage(), e);
        }

        try {
            map.put("load", getLoad());
        } catch (Exception e) {
            logger.error("【load】" + e.getMessage(), e);
        }

        try {
            map.put("io", getIoWait());
        } catch (Exception e) {
            logger.error("【io】" + e.getMessage(), e);
        }

        try {
            map.put("disk", getDiskUsage());
        } catch (Exception e) {
            logger.error("【disk】" + e.getMessage(), e);
        }

        return map;
    }

    /**
     * 获取服务器IP地址
     * @return
     */
    public static String getLocalInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Throwable t) {
        }
        return "127.0.0.1";
    }
    /**
     * 获取系统能承受的最大并发数
     * 计算标准：2核3G最大并发1500个
     */
    public static int getMaxThreadNum() {
        int cpuNum =  Runtime.getRuntime().availableProcessors();
        double memorySize = Runtime.getRuntime().maxMemory()/1024d/1024d/1024d;
        double rate = Math.min(cpuNum/2d, memorySize/3d);
        int maxThread = (int)Math.floor(1500*rate);
        logger.info("cpu="+cpuNum+", memory="+memorySize+"G"+", rate="+rate+", maxThread="+maxThread);
        return maxThread;
    }

    public static void main(String[] args) {
//        try {
//            System.out.println("cpu:"+getCpuInfo());
//            System.out.println("memery:"+getUsedMemery());
//            System.out.println("load:"+JsonUtils.obj2Json(getLoad()));
//            System.out.println("io:"+getIoWait());
//            System.out.println("disk:"+getDiskUsage());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        System.out.println(getLocalInetAddress());
    }
}


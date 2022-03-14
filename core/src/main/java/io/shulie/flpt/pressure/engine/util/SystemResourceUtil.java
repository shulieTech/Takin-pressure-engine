package io.shulie.flpt.pressure.engine.util;

import com.sun.management.OperatingSystemMXBean;
import io.shulie.flpt.pressure.engine.entity.health.DiskUsage;
import io.shulie.flpt.pressure.engine.entity.health.LoadInfo;
import io.shulie.flpt.pressure.engine.entity.health.NetUsageInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 昂驹
 */
@Slf4j
public class SystemResourceUtil {
    private static final int CPU_TIME = 500;
    private static final int PERCENT = 100;
    private static final int FAULT_LENGTH = 10;
    private static final String LOCAL_ADDRESS = "127.0.0.1";
    private static final String NET_CARD_MAX_SPEED = "1000";

    /**
     * 获得Linux cpu使用率
     *
     * @return float efficiency
     * @throws IOException io异常
     * @throws InterruptedException 线程打断异常
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

        return (float)((user2 + sys2 + nice2) - (user1 + sys1 + nice1)) * 100
            / (float)((user2 + nice2 + sys2 + idle2) - (user1 + nice1
            + sys1 + idle1));
    }

    /**
     * 获得cpu使用率
     *
     * @return CPU使用了
     */
    public static double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";

            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPU_TIME);

            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));

            if ((c0 != null) && (c1 != null)) {
                long idleTime = c1[0] - c0[0];
                long busyTime = c1[1] - c0[1];

                return Double.valueOf(
                        (PERCENT * (busyTime) * 1.0) / (busyTime + idleTime))
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
     *
     * @param proc 进程
     * @return -
     */
    private static long[] readCpu(final Process proc) {
        long[] retn = new long[2];

        try {
            proc.getOutputStream().close();

            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();

            if ((line == null) || (line.length() < FAULT_LENGTH)) {
                return null;
            }

            int capIdx = line.indexOf("Caption");
            int cmdIdx = line.indexOf("CommandLine");
            int rocIdx = line.indexOf("ReadOperationCount");
            int umtIdx = line.indexOf("UserModeTime");
            int kmtIdx = line.indexOf("KernelModeTime");
            int wocIdx = line.indexOf("WriteOperationCount");
            long idleTime = 0;
            long kenelTime = 0;
            long userTime = 0;

            while ((line = input.readLine()) != null) {
                if (line.length() < wocIdx) {
                    continue;
                }

                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = substring(line, capIdx, cmdIdx - 1).trim();
                String cmd = substring(line, cmdIdx, kmtIdx - 1).trim();

                if (cmd.contains("wmic.exe")) {
                    continue;
                }

                String s1 = substring(line, kmtIdx, rocIdx - 1).trim();
                String s2 = substring(line, umtIdx, wocIdx - 1).trim();

                if ("System Idle Process".equals(caption)
                    || "System".equals(caption)) {
                    if (s1.length() > 0) {
                        idleTime += Long.parseLong(s1);
                    }

                    if (s2.length() > 0) {
                        idleTime += Long.parseLong(s2);
                    }

                    continue;
                }

                if (s1.length() > 0) {
                    kenelTime += Long.parseLong(s1);
                }

                if (s2.length() > 0) {
                    userTime += Long.parseLong(s2);
                }
            }

            retn[0] = idleTime;
            retn[1] = kenelTime + userTime;

            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                log.error("读取cpu相关信息异常:", e);
            }
        }

        return null;
    }

    /**
     * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
     *
     * @param src        要截取的字符串
     * @param startIndex 开始坐标（包括该坐标)
     * @param endIndex   截止坐标（包括该坐标）
     * @return -
     */
    private static String substring(String src, int startIndex, int endIndex) {
        byte[] b = src.getBytes();
        StringBuilder tgt = new StringBuilder();

        for (int i = startIndex; i <= endIndex; i++) {
            tgt.append((char)b[i]);
        }

        return tgt.toString();
    }

    /**
     * 获取free内存
     *
     * @return 可用内存
     */
    public static long getFreeMemory() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory
            .getOperatingSystemMXBean();
        // 总的物理内存
        long freePhysicalMemorySize = osBean.getFreePhysicalMemorySize();
        return freePhysicalMemorySize / 1024 / 1024;
    }

    /**
     * 获取总内存
     *
     * @return 总内存
     */
    public static long getTotalMemory() {
        OperatingSystemMXBean os = (OperatingSystemMXBean)ManagementFactory
            .getOperatingSystemMXBean();
        // 总的物理内存
        long totalPhysicalMemorySize = os.getTotalPhysicalMemorySize();
        return totalPhysicalMemorySize / 1024 / 1024;
    }

    /**
     * 获取总内存
     *
     * @return 总内存
     */
    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }

    public static int getCpuNum() {
        int cpuNum = 0;
        Runtime r = Runtime.getRuntime();
        String command = "cat /proc/stat";
        BufferedReader in1 = null;
        Process pro1 = null;
        try {
            pro1 = r.exec(command);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while ((line = in1.readLine()) != null) {
                if (line.contains("cpu")) {
                    cpuNum++;
                }
            }
            return cpuNum - 1;
        } catch (Exception e) {
            log.error("get load error:", e);
            return cpuNum;
        } finally {
            if (in1 != null) {
                try {
                    in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pro1 != null) {
                pro1.destroy();
            }
        }
    }

    public static LoadInfo getLoad() {
        LoadInfo loadInfo = new LoadInfo();
        Runtime r = Runtime.getRuntime();
        String command = "top -b -n 1";
        BufferedReader in1 = null;
        Process pro1 = null;
        try {
            pro1 = r.exec(command);
            pro1.waitFor(2, TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while ((line = in1.readLine()) != null) {
                if (line.contains("top")) {
                    String temp = line.split("load average:")[1];
                    loadInfo.setLoad_1(temp.split(",")[0]);
                    loadInfo.setLoad_2(temp.split(",")[1]);
                    loadInfo.setLoad_3(temp.split(",")[2]);
                    break;
                }
            }
            return loadInfo;
        } catch (Exception e) {
            log.error("get load error:", e);
            return loadInfo;
        } finally {
            if (in1 != null) {
                try {
                    in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pro1 != null) {
                pro1.destroy();
            }
        }
    }

    public static String getIoWait() {
        //%Cpu(s):  1.5 us,  0.2 sy,  0.0 ni, 98.2 id,  0.1 wa,  0.0 hi,  0.0 si,  0.0 st
        String ioWait = null;
        Runtime r = Runtime.getRuntime();
        String command = "top -b -n 1";
        BufferedReader in1 = null;
        Process pro1 = null;
        try {
            pro1 = r.exec(command);
            pro1.waitFor(2, TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while ((line = in1.readLine()) != null) {
                if (line.contains("Cpu(s)")) {
                    ioWait = line.split(",")[4];
                    break;
                }
            }
        } catch (Exception e) {
            log.error("getIoWait error:", e);
        } finally {
            if (in1 != null) {
                try {
                    in1.close();
                } catch (IOException e) {
                    log.error("getIoWait()发生错误.", e);
                }
            }
            if (pro1 != null) {
                pro1.destroy();
            }
        }
        return ioWait;
    }

    public static List<DiskUsage> getDiskUsage() {
        Runtime r = Runtime.getRuntime();
        String command = "iostat -d -x 1 1";
        BufferedReader in1 = null;
        Process pro1 = null;
        List<DiskUsage> diskUsageList = new ArrayList<>();
        try {
            pro1 = r.exec(command);
            pro1.waitFor(2, TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            while ((line = in1.readLine()) != null) {
                if (line.contains("Device")) {
                    log.info("Device info:" + line);
                } else if (line.contains("Linux")) {
                    log.info("Linux info:" + line);
                } else if (StringUtils.isNotBlank(line)) {
                    DiskUsage diskUsage = new DiskUsage();
                    diskUsage.setDiskName(line.split(" ")[0]);
                    diskUsage.setUtil(line.split(" ")[line.split(" ").length - 1]);
                    diskUsageList.add(diskUsage);
                }
            }
            return diskUsageList;
        } catch (Exception e) {
            log.error("getDiskUsage error:", e);
            return diskUsageList;
        } finally {
            if (in1 != null) {
                try {
                    in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (pro1 != null) {
                pro1.destroy();
            }
        }
    }

    private static List<NetUsageInfo> getNetCard() {
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
            pro1.waitFor(2, TimeUnit.SECONDS);
            in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
            String line;
            NetUsageInfo netUsageInfo = null;
            while ((line = in1.readLine()) != null) {
                if (line.contains("flags") || line.contains("encap:Ethernet")) {
                    if (netUsageInfo != null && netUsageInfo.getIp() != null) {
                        netUsageInfoList.add(netUsageInfo);
                    }
                    netUsageInfo = new NetUsageInfo();
                    netUsageInfo.setName(line.split(" ")[0]);
                } else if (line.contains("inet") && !line.contains("inet6")) {
                    if (netUsageInfo != null && line.contains(agentIp)) {
                        netUsageInfo.setIp(agentIp);
                    }
                }
                log.info(line);
            }
            return netUsageInfoList;
        } catch (Exception e) {
            log.error("getNetUsage error:", e);
            return netUsageInfoList;
        } finally {
            try {
                if (in1 != null) {
                    in1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pro1 != null) {
                pro1.destroy();
            }
        }
    }

    private static void getMaxNetSpeed(List<NetUsageInfo> list) {
        String temp = NET_CARD_MAX_SPEED;
        if (StringUtils.isNotBlank(temp)) {
            for (NetUsageInfo netUsageInfo : list) {
                netUsageInfo.setMaxSpeed(temp);
            }
        } else {
            for (NetUsageInfo netUsageInfo : list) {
                Process pro1 = null;
                Runtime r = Runtime.getRuntime();
                BufferedReader in1 = null;
                try {
                    String command = "ethtool " + netUsageInfo.getName();
                    //第一次采集流量数据
                    pro1 = r.exec(command);
                    pro1.waitFor(2, TimeUnit.SECONDS);
                    in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
                    String line;
                    while ((line = in1.readLine()) != null) {
                        if (line.startsWith("Speed")) {
                            netUsageInfo.setMaxSpeed(line.replace("Speed:", "").replaceAll(" ", ""));
                        }
                    }
                } catch (Exception e) {
                    log.error("getMaxNetSpeed error:", e);
                } finally {
                    try {
                        if (in1 != null) {
                            in1.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (pro1 != null) {
                        pro1.destroy();
                    }
                }
            }
        }
    }

    private static void getNetUsage(List<NetUsageInfo> list) {
        for (NetUsageInfo netUsageInfo : list) {
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
                pro1.waitFor(2, TimeUnit.SECONDS);
                Thread.sleep(1000);
                pro2 = r2.exec(command);
                pro2.waitFor(2, TimeUnit.SECONDS);
                in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
                in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
                String line;
                long receive1 = 0;
                long send1 = 0;
                long receive2 = 0;
                long send2 = 0;
                while ((line = in1.readLine()) != null) {
                    if (line.contains(netUsageInfo.getName())) {
                        String[] temp = line.replace(netUsageInfo.getName(), "").split("\\s+");
                        receive1 = Long.parseLong(temp[2]);
                        send1 = Long.parseLong(temp[9]);
                    }
                }

                while ((line = in2.readLine()) != null) {
                    if (line.contains(netUsageInfo.getName())) {
                        String[] temp = line.replace(netUsageInfo.getName(), "").split("\\s+");
                        receive2 = Long.parseLong(temp[2]);
                        send2 = Long.parseLong(temp[9]);
                    }
                }
                BigDecimal receive = new BigDecimal(receive2 - receive1);
                BigDecimal send = new BigDecimal(send2 - send1);
                netUsageInfo.setReceive(receive.divide(new BigDecimal(1024 * 1024), 6, RoundingMode.HALF_UP).floatValue());
                netUsageInfo.setSend(send.divide(new BigDecimal(1024 * 1024), 6, RoundingMode.HALF_UP).floatValue());
            } catch (Exception e) {
                log.error("getNetUsage error:", e);
            } finally {
                try {
                    if (in1 != null) {
                        in1.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    if (in2 != null) {
                        in2.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (pro1 != null) {
                    pro1.destroy();
                }

                if (pro2 != null) {
                    pro2.destroy();
                }
            }
        }

    }

    public static List<NetUsageInfo> getNetUsageInfo() {
        List<NetUsageInfo> list = getNetCard();
        getMaxNetSpeed(list);
        getNetUsage(list);
        return list;
    }

    public static Map<String, Object> getServerInfo() {
        Map<String, Object> map = new HashMap<>(5);
        try {
            map.put("cpu", getCpuInfo());
        } catch (Exception e) {
            log.error("【cpu】" + e.getMessage(), e);
        }

        try {
            map.put("memory", getUsedMemory());
        } catch (Exception e) {
            log.error("【memory】" + e.getMessage(), e);
        }

        try {
            map.put("load", getLoad());
        } catch (Exception e) {
            log.error("【load】" + e.getMessage(), e);
        }

        try {
            map.put("io", getIoWait());
        } catch (Exception e) {
            log.error("【io】" + e.getMessage(), e);
        }

        try {
            map.put("disk", getDiskUsage());
        } catch (Exception e) {
            log.error("【disk】" + e.getMessage(), e);
        }

        return map;
    }

    /**
     * 获取服务器IP地址
     *
     * @return 服务器IP地址
     */
    public static String getLocalInetAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress address;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.getHostAddress().contains(":")) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Throwable t) {
            log.debug("忽略的异常", t);
        }
        return "127.0.0.1";
    }

    /**
     * 获取系统能承受的最大并发数
     * 计算标准：2C3G的配置，最大并发1500个
     */
    public static int getMaxThreadNum() {
        int cpuNum = Runtime.getRuntime().availableProcessors();
        double memorySize = Runtime.getRuntime().maxMemory() / 1024d / 1024d / 1024d;
        double rate = Math.min(cpuNum / 2d, memorySize / 3d);
        int maxThread = (int)Math.floor(1500 * rate);
        log.info("cpu=" + cpuNum + ", memory=" + memorySize + "G" + ", rate=" + rate + ", maxThread=" + maxThread);
        return maxThread;
    }

    public static void main(String[] args) {
        log.info(getLocalInetAddress());
    }
}


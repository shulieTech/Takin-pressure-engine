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

package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import io.shulie.flpt.pressure.engine.common.Constants;
import io.shulie.flpt.pressure.engine.entity.cloud.EngineStatusEnum;
import io.shulie.flpt.pressure.engine.util.FileUtils;
import io.shulie.flpt.pressure.engine.util.StringWriter;
import io.shulie.flpt.pressure.engine.util.http.HttpNotifyTakinCloudUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author hezhongqi
 */
public class JmeterPluginUtil {

    private static Logger logger = LoggerFactory.getLogger(JmeterPluginUtil.class);
    private static final String LESS_THAN = "&lt;";
    private static final String LESS_THAN_REPLACEMENT = "SHULIE_LESS_THAN_FLAG";
    private static final String GREATER_THAN = "&gt;";
    private static final String GREATER_THAN_REPLACEMENT = "SHULIE_GREATER_THAN_FLAG";
    private static final String AND = "&amp;";
    private static final String AND_REPLACEMENT = "SHULIE_AND_FLAG";
    private static final String APOS = "&apos;";
    private static final String APOS_REPLACEMENT = "SHULIE_APOS_FLAG";
    private static final String QUOTE = "&quot;";
    public static final String QUOTE_REPLACEMENT = "SHULIE_GUOTE_FLAG";

    /**
     * 写入最终文件
     *
     * @param document
     * @param finalJmxFilePathName
     * @return
     */
    public static String writeToFinalFile(Document document, String finalJmxFilePathName) {
        StringWriter stringWriter = null;
        try {
            stringWriter = new StringWriter();
            document.write(stringWriter);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (stringWriter != null) {
                try {
                    stringWriter.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }

        String finalStr = stringWriter.getString();
        finalStr = specialCharRepAfter(finalStr);
        /*
         * 写入最终压测文件
         */
        File file = FileUtils.createFileDE(finalJmxFilePathName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(finalStr);
            writer.flush();
        } catch (Exception e) {
            HttpNotifyTakinCloudUtils.notifyTakinCloud(EngineStatusEnum.START_FAILED, e.getMessage());
            logger.warn(e.getMessage(), e);
            System.exit(-1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return finalStr;
    }

    /**
     * 复制插件到jmeter
     *
     * @param enginePluginFilePath 插件存放文件夹
     */
    public static void copyPluginsToJmeter(String enginePluginFilePath) {
        //暂时处理非kafka和dubbo插件，第三方如mysql.jar, oracle.jar想上传到ext下，
        //会传绝对路径，这个绝对路径是容器中映射的nfs根目录下的文件
        //TODO 之后需要将cloud把kafka和dubbo插件的路径也处理成绝对路径就好了
        String pluginJarPath = enginePluginFilePath;
        //jmeter home目录
        String jmeterHomeFolder = System.getProperty("jmeter.home");
        if (!enginePluginFilePath.startsWith(Constants.ENGINE_NFS_MOUNTED_PATH)) {
            if(enginePluginFilePath.startsWith("/")) {
                pluginJarPath = jmeterHomeFolder + enginePluginFilePath;
            } else {
                pluginJarPath = jmeterHomeFolder + File.separator + enginePluginFilePath;
            }
        }
        //end
        //获取file对象
        File file = FileUtils.getFile(pluginJarPath, ".jar");
        //文件存在
        if (file != null) {
            //获取jmeter lib路径
            String jmeterPluginsFolder = jmeterHomeFolder
                    + File.separator + "lib" + File.separator + "ext";
            try {
                FileUtils.copyFileToDirectory(file, jmeterPluginsFolder);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("插件文件[" + file.getName() + "]不是jar文件，已忽略。");
            }
        }
    }

    public static String specialCharRepBefore(String content) {
        content = content.replaceAll(LESS_THAN, LESS_THAN_REPLACEMENT);
        content = content.replaceAll(GREATER_THAN, GREATER_THAN_REPLACEMENT);
        content = content.replaceAll(AND, AND_REPLACEMENT);
        content = content.replaceAll(APOS, APOS_REPLACEMENT);
        content = content.replaceAll(QUOTE, QUOTE_REPLACEMENT);
        return content;
    }

    public static String specialCharRepAfter(String content) {
        content = content.replaceAll(LESS_THAN_REPLACEMENT, LESS_THAN);
        content = content.replaceAll(GREATER_THAN_REPLACEMENT, GREATER_THAN);
        content = content.replaceAll(AND_REPLACEMENT, AND);
        content = content.replaceAll(APOS_REPLACEMENT, APOS);
        content = content.replaceAll(QUOTE_REPLACEMENT, QUOTE);
        return content;
    }

    public static String availablePortAcquire(int begin) {
        for (int i = begin; i < begin + 1000; i++) {
            if (portAvailable(i)) {
                return i + "";
            }
        }
        return begin + "";
    }

    public static boolean portAvailable(int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void bindPort(String host, int port) throws Exception {
        Socket socket = new Socket(host,port);
        socket.close();
    }

}

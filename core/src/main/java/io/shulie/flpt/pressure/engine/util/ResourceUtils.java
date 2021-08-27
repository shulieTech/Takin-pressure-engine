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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Create by xuyh at 2020/4/20 11:42.
 */
public class ResourceUtils {
    private static Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    public static String download(String url, String targetDir) throws Exception {
        if (!FileUtils.makeDir(new File(targetDir))) {
            throw new RuntimeException("Target dir " + targetDir + " create failed.");
        }
        String fileName = url.contains("/") ? url.substring(url.lastIndexOf("/") + 1) : url;
        String filePathName = targetDir + File.separator + fileName;
        File file = FileUtils.createFileDE(filePathName);
        if (file == null) {
            throw new RuntimeException("File create failed.");
        }
        String replacedFileName = URLEncoder.encode(fileName, "utf-8");
        url = url.replace(fileName, replacedFileName);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            inputStream = connection.getInputStream();
            fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return filePathName;
    }
}

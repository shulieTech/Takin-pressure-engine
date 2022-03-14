package io.shulie.flpt.pressure.engine.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.io.InputStream;
import java.net.URLEncoder;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;

/**
 * @author xuyh
 */
@Slf4j
public class ResourceUtils {

    public static String download(String url, String targetDir) throws Exception {
        if (!FileUtils.makeDir(new File(targetDir))) {
            throw new RuntimeException("Target dir " + targetDir + " create failed.");
        }
        String fileName = url.contains("/") ? url.substring(url.lastIndexOf("/") + 1) : url;
        String filePathName = targetDir + File.separator + fileName;
        File file = FileUtils.createFilePreDelete(filePathName);
        if (file == null) {
            throw new RuntimeException("File create failed.");
        }
        String replacedFileName = URLEncoder.encode(fileName, "utf-8");
        url = url.replace(fileName, replacedFileName);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
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
                    log.warn(e.getMessage(), e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
        return filePathName;
    }
}

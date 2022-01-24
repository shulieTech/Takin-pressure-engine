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

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Create by xuyh at 2020/4/18 16:00.
 */
public class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void main(String[] args) {
        createFileDE("/home/opt/flpt/pressure-task/resources/final/test.jmx");
    }

    public static File createFileDE(String filePathName) {
        File file = new File(filePathName);
        if (file.exists()) {
            if (!file.delete())
                return null;
        }
        if (!makeDir(file.getParentFile()))
            return null;
        try {
            if (!file.createNewFile())
                return null;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return file;
    }

    public static boolean makeDir(File dir) {
        if (!dir.exists()) {
            File parent = dir.getParentFile();
            if (parent != null)
                makeDir(parent);
            return dir.mkdir();
        }
        return true;
    }

    public static List<File> getDirectoryFiles(String dir, String fileEndsWith) {
        List<File> scriptFiles = new ArrayList<>();
        if (dir == null) {
            return null;
        }
        File fileDir = new File(dir);
        if (!fileDir.isDirectory()) {
            logger.warn("Expected a dir, but not: '{}'", fileDir.getPath());
        }
        if (!fileDir.isAbsolute()) {
            logger.warn("Expected a absolute path, bu not: '{}'", fileDir.getPath());
        }
        File[] files = fileDir.listFiles(file -> {
            if (fileEndsWith == null) {
                return true;
            } else {
                return file.getName().endsWith(fileEndsWith);
            }
        });
        if (files == null || files.length == 0) {
            return null;
        }

        scriptFiles.addAll(Arrays.asList(files));
        return scriptFiles;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    if (!deleteDir(new File(child))) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    public static boolean writeTextFile(String content, String filePathName) {
        File file = createFileDE(filePathName);
        if (file == null) {
            return false;
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    public static String readTextFileContent(File file) {
        InputStreamReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            char[] buffer = new char[32];
            int length;
            while ((length = reader.read(buffer)) > 0) {
                stringBuilder.append(buffer, 0, length);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 拷贝文件到目录
     *
     * @param from
     * @param directory
     */
    public static void copyFileToDirectory(File from, String directory) throws IOException {
        Files.copy(from, new File(directory + File.separator + from.getName()));
    }

    /**
     * 获取文件， 可以指定文件后缀，如果不是则返回null
     *
     * @param filePath
     * @param fileEndsWith
     * @return
     */
    public static File getFile(String filePath, String... fileEndsWith) {
        File result = new File(filePath);
        if(!result.exists()) {
            return null;
        }
        //文件名小写
        String fileNameLower = result.getName().toLowerCase();
        for(String endsWith : fileEndsWith) {
            //匹配
            if(fileNameLower.endsWith(endsWith.toLowerCase())) {
                return result;
            }
        }
        return null;
    }


    public static File selectFile(List<File> files, String endsWith) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        File jmxFile = null;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(endsWith)) {
                jmxFile = file;
                break;
            }
        }
        return jmxFile;
    }

    public static List<File> selectFiles(List<File> files, String... endsWith) {
        List<File> selected = new ArrayList<>();
        if (files == null || files.isEmpty() || endsWith == null || endsWith.length == 0) {
            return null;
        }
        for (File file : files) {
            String fileName = file.getName();
            for (String s : endsWith) {
                if (fileName.endsWith(s)) {
                    selected.add(file);
                    break;
                }
            }
        }
        return selected;
    }
}

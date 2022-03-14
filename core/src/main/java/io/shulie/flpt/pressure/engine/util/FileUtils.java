package io.shulie.flpt.pressure.engine.util;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Files;

import java.io.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

/**
 * @author xuyh
 */
@Slf4j
public class FileUtils {
    /**
     * 创建文件 - 预删除
     * <p>如果文件存在，会删除文件后新建一个文件</p>
     *
     * @param filePathName 文件路径
     * @return 空白的文件
     */
    public static File createFilePreDelete(String filePathName) {
        File file = new File(filePathName);
        if (file.exists()) {
            if (!file.delete()) {return null;}
        }
        if (!makeDir(file.getParentFile())) {return null;}
        try {
            if (!file.createNewFile()) {return null;}
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return file;
    }

    /**
     * 创建文件夹
     *
     * @param dir 文件夹路径
     * @return 是否创建成功
     */
    public static boolean makeDir(File dir) {
        if (!dir.exists()) {
            File parent = dir.getParentFile();
            if (parent != null) {makeDir(parent);}
            return dir.mkdir();
        }
        return true;
    }

    /**
     * 获取目录下的文件
     *
     * @param dir          目录地址
     * @param fileEndsWith 文件后缀名
     * @return 文件列表
     */
    public static List<File> getDirectoryFiles(String dir, String fileEndsWith) {
        if (dir == null) {
            return null;
        }
        File fileDir = new File(dir);
        if (!fileDir.isDirectory()) {
            log.warn("应该是文件目录，但不是: '{}'", fileDir.getPath());
        }
        if (!fileDir.isAbsolute()) {
            log.warn("应该是绝对路径，但不是: '{}'", fileDir.getPath());
        }
        File[] files = fileDir.listFiles(file -> fileEndsWith == null || file.getName().endsWith(fileEndsWith));
        /*
         * 如果没有,则返回null
         * ps:不是很明白这个逻辑
         */
        if (files == null || files.length == 0) {return null;}

        return new ArrayList<>(Arrays.asList(files));
    }

    /**
     * 删除目录
     *
     * @param dir 目录路径
     * @return 操作是否成功
     */
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

    /**
     * 写入文本到文件
     * <p>会覆盖旧文件</p>
     *
     * @param content  文本内容
     * @param filePath 文件路径
     * @return 操作是否成功
     */
    public static boolean writeTextFile(String content, String filePath) {
        File file = createFilePreDelete(filePath);
        if (file == null) {
            return false;
        }
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
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
            log.warn(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 拷贝文件到目录
     *
     * @param from      源文件
     * @param directory 目标文件夹
     */
    public static void copyFileToDirectory(File from, String directory) throws IOException {
        Files.copy(from, new File(directory + File.separator + from.getName()));
    }

    /**
     * 获取文件， 可以指定文件后缀，如果不是则返回null
     *
     * @param filePath     文件路径
     * @param fileEndsWith 文件后缀名
     * @return 文件
     */
    public static File getFile(String filePath, String... fileEndsWith) {
        File result = new File(filePath);
        if (!result.exists()) {
            return null;
        }
        //文件名小写
        String fileNameLower = result.getName().toLowerCase();
        for (String endsWith : fileEndsWith) {
            //匹配
            if (fileNameLower.endsWith(endsWith.toLowerCase())) {
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

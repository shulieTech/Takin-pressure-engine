package io.shulie.flpt.pressure.engine.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * @author johnsonmoon
 */
@Slf4j
public class ProcessUtils {
    @SuppressWarnings("AlibabaThreadPoolCreation")
    private static final ExecutorService SERVICE = Executors.newCachedThreadPool();

    public static int run(String command, String directory, Long timeout, final Callback callback, final Comm comm) {
        String[] commands = new String[] {"sh", "-c", command};
        return run(directory, timeout, callback, comm, commands);
    }

    public static int stop(String command, String directory, Long timeout, final Callback callback, final Comm comm) {
        String[] commands = new String[] {"sh", command};
        return run(directory, timeout, callback, comm, commands);
    }

    private static int run(String directory, Long timeout, final Callback callback, final Comm comm, String[] commands) {
        final ProcessBuilder processBuilder = new ProcessBuilder(commands);
        if (directory != null) {
            File workDir = new File(directory);
            if (workDir.exists() && workDir.isDirectory()) {
                processBuilder.directory(workDir);
            }
        }
        processBuilder.redirectErrorStream(true);
        int status = -1;
        try {
            final Process process = processBuilder.start();
            if (callback != null) {
                callback.created(process);
            }
            if (comm != null) {
                SERVICE.submit(() -> {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            comm.onLine(line);
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
                });
            }
            if (timeout == null || timeout <= 0) {
                status = process.waitFor();
            } else {
                if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
                    throw new RuntimeException(String.format("命令运行超时。\n超时时间: %s\n命令: %s", timeout, GsonUtils.obj2Json(commands)));
                } else {
                    status = process.exitValue();
                }
            }
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e) {
            log.error("引擎运行JMeter失败:", e);
        }
        return status;
    }

    public interface Comm {
        /**
         * 回调函数-进程输出
         *
         * @param message 输出的消息
         */
        void onLine(String message);
    }

    public interface Callback {
        /**
         * 回调函数-进程创建完成
         *
         * @param process 进程
         */
        void created(Process process);
    }
}

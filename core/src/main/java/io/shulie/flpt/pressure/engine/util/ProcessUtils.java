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

import cn.hutool.core.io.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Create by johnsonmoon at 2018/10/31 10:38.
 */
public class ProcessUtils {
    private static Logger logger = LoggerFactory.getLogger(ProcessUtils.class);
    private static ExecutorService service = Executors.newCachedThreadPool();

    public static int run(String command, String directory, Long timeout, final Callback callback, final Comm comm) {
        String[] commands = new String[]{"sh", "-c", command};
        return run(directory,timeout,callback,comm,commands);
    }


    public static int stop(String command, String directory, Long timeout, final Callback callback, final Comm comm) {
        String[] commands = new String[]{"sh",command};
        return run(directory,timeout,callback,comm,commands);
    }
    private static int run(String directory, Long timeout, final Callback callback, final Comm comm,String[] commands) {
        final ProcessBuilder processBuilder = new ProcessBuilder(commands);
        if (directory != null) {
            File workDir = FileUtil.file(directory);
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
                service.submit(() -> {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            comm.onLine(line);
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
                });
            }
            if (timeout == null || timeout <= 0) {
                status = process.waitFor();
            } else {
                if (!process.waitFor(timeout, TimeUnit.SECONDS)) {
                    throw new RuntimeException(String.format("Command run timeout, timeout: %s, command: %s", timeout, GsonUtils.obj2Json(commands)));
                } else {
                    status = process.exitValue();
                }
            }
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e.getMessage());
        }
        return status;
    }
    public interface Comm {
        void onLine(String message);
    }

    public interface Callback {
        void created(Process process);
    }
}

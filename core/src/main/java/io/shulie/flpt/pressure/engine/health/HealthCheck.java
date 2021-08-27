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

package io.shulie.flpt.pressure.engine.health;

import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.util.SystemResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.flpt.pressure.engine.health
 * @Date 2020-05-08 16:13
 */
public class HealthCheck {

    private static Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    private HttpServerInfoReport httpServerInfoReport;

    private ThreadFactory threadFactory = r -> {
        Thread thread = new Thread(r);
        thread.setName("health-check");
        return thread;
    };

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2, threadFactory);


    public void register(PressureContext context) {

        try {
            httpServerInfoReport = new HttpServerInfoReport();
            httpServerInfoReport.start(context);

            executorService.scheduleAtFixedRate(() -> {
                try {
                    Map<String, Object> serverInfo = SystemResourceUtil.getServerInfo();
                    httpServerInfoReport.sendHttp(serverInfo);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }, 1, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destroy(){
        executorService.shutdown();
        httpServerInfoReport.destroy();
    }
}

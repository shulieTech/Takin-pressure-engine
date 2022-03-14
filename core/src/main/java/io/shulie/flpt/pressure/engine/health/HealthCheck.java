package io.shulie.flpt.pressure.engine.health;

import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.util.SystemResourceUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 */
@Slf4j
public class HealthCheck {

    private HttpServerInfoReport httpServerInfoReport;

    private final ThreadFactory threadFactory = r -> {
        Thread thread = new Thread(r);
        thread.setName("health-check");
        return thread;
    };

    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2, threadFactory);

    public void register(PressureContext context) {

        try {
            httpServerInfoReport = new HttpServerInfoReport();
            httpServerInfoReport.start(context);

            executorService.scheduleAtFixedRate(() -> {
                try {
                    Map<String, Object> serverInfo = SystemResourceUtil.getServerInfo();
                    httpServerInfoReport.sendHttp(serverInfo);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }, 1, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void destroy() {
        executorService.shutdown();
        httpServerInfoReport.destroy();
    }
}

package io.shulie.flpt.pressure.engine.util;

import io.shulie.flpt.pressure.engine.health.HealthCheck;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

public class HealthCheckTest {

    public static void main(String[] args) {
        PressureContext context = new PressureContext();
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.register(context);
    }
}

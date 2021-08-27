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

import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.health.HealthCheck;

import java.util.HashMap;
import java.util.Map;

public class HealthCheckTest {

    public static void main(String[] args) {
        PressureContext context = new PressureContext();
        Map<String, Object> parmas = new HashMap<>();
        parmas.put("consoleUrl", "http://localhost:10010");
        context.setTaskParams(parmas);
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.register(context);
    }
}

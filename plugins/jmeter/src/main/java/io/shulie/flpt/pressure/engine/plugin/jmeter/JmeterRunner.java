package io.shulie.flpt.pressure.engine.plugin.jmeter;

import org.apache.jmeter.NewDriver;
import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;

/**
 * @author xuyh
 */
public class JmeterRunner {
    /**
     * 运行
     *
     * @param context 压测上下文
     * @param args    参数
     */
    public static void run(PressureContext context, String... args) {
        NewDriver.main(args);
    }
}

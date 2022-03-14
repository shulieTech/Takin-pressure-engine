package io.shulie.flpt.pressure.engine.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xuyh
 */
@Slf4j
public class TryUtils {
    public static <T> T tryOperation(Operation<T> operation) {
        T result = null;
        try {
            result = operation.operate();
        } catch (Exception e) {
            // 忽略异常
        }
        return result;
    }

    public interface Operation<T> {
        /**
         * 要做的操作
         *
         * @return 操作结果
         */
        T operate();
    }

    public static void retry(Runnable runnable, int retryTimes) {
        for (int i = 0; i <= retryTimes; i++) {
            try {
                runnable.run();
                break;
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}

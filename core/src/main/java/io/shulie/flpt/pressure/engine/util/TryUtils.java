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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create by xuyh at 2020/4/20 21:54.
 */
public class TryUtils {
    private static Logger logger = LoggerFactory.getLogger(TryUtils.class);

    public static <T> T tryOperation(Operation<T> operation) {
        T result = null;
        try {
            result = operation.operate();
        } catch (Exception e) {
            //do nothing
        }
        return result;
    }

    public interface Operation<T> {
        T operate();
    }

    public static void retry(Runnable runnable, int retryTimes) {
        for (int i = 0; i <= retryTimes; i++) {
            try {
                runnable.run();
                break;
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }
}

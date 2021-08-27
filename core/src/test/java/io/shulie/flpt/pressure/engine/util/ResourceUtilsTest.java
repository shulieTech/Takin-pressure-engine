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
 * Create by xuyh at 2020/4/21 23:32.
 */
public class ResourceUtilsTest {
    private static Logger logger = LoggerFactory.getLogger(ResourceUtilsTest.class);

    public static void main(String[] args) {
        try {
            ResourceUtils.download(
                    "http://localhost:10010/takin-web/api/file/download?fileName=38/unp.jmx",
                    "/Users/johnson/job-workspace/prada/pradar-splits/pressure-engine"
            );
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
}

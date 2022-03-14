package io.shulie.flpt.pressure.engine.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xuyh
 */
@Slf4j
public class ResourceUtilsTest {
    public static void main(String[] args) {
        try {
            ResourceUtils.download(
                "http://localhost:10010/takin-web/api/file/download?fileName=38/unp.jmx",
                "/Users/johnson/job-workspace/prada/pradar-splits/pressure-engine"
            );
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}

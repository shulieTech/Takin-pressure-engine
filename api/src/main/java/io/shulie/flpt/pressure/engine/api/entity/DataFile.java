package io.shulie.flpt.pressure.engine.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: liyuanba
 * @Date: 2021/10/29 5:17 下午
 */
@Data
public class DataFile extends AbstractEntry {
    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 是否分割文件
     */
    private boolean split;

    /**
     * 是否有序
     */
    private boolean ordered;

    /**
     * refId
     */
    private Long refId;


    /**
     * 是否大文件
     */
    private boolean isBigFile;

    /**
     * 文件分片信息,key-排序，引擎会用到；value-需要读取的分区数据
     */
    Map<Integer, List<StartEndPosition>> startEndPositions;

    @Data
    public static class StartEndPosition implements Serializable {

        /**
         * 分区
         */
        private String partition;

        /**
         * pod读取文件开始位置
         */
        private String start = "-1";

        /**
         * pod读取文件结束位置
         */
        private String end = "-1";
    }
}

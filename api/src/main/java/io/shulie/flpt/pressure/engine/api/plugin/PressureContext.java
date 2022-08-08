package io.shulie.flpt.pressure.engine.api.plugin;

import java.io.File;
import java.util.Map;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.flpt.pressure.engine.api.entity.*;
import io.shulie.flpt.pressure.engine.api.enums.PressureSceneEnum;

/**
 * 压测上下文
 *
 * @author xuyh
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PressureContext extends AbstractEntry {
    /**
     * 场景id
     */
    private String sceneId;
    /**
     * 报告id
     */
    private Long reportId;
    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 脚本路径
     */
    private String scriptPath;
    /**
     * 任务目录
     */
    private String taskDir;
    /**
     * 日志文件目录
     */
    private String logDir;
    /**
     * ptl文件目录
     */
    private String ptlDir;
    /**
     * 资源文件目录
     */
    private String resourcesDir;
    /**
     * 压测内存设置
     */
    private String memSetting;
    /**
     * 是否是新版，新版标识是压测场景有脚本解析的结果
     */
    private boolean oldVersion;
    /**
     * 运行时间
     */
    private Integer duration;
    /**
     * 引擎插件路径
     */
    private List<String> enginePluginsFilePath;
    /**
     * 压测引擎附件
     */
    private List<String> attachmentsFiles;
    /**
     * 期望目标值
     * <p>并发模式下为并发,tps模式为tps</p>
     */
    private Long expectThroughput;
    /**
     * 循环次数
     */
    private Long fix;
    private Long loops;
    /**
     * metric上报数据url
     */
    private String metricCollectorUrl;
    /**
     * 额外参数，目前这里是记录业务活动对应的目标rt
     */
    private Map<String, BusinessActivityConfig> businessMap;
    /**
     * 是否通过xpath的md5进行关联，新老板区分
     */
    private Boolean bindByXpathMd5;
    /**
     * 压测数据信息
     */
    private List<Map<String, Object>> dataFileSets;
    /**
     * pod数量
     */
    private Integer podCount;
    /**
     * pod 的序号
     */
    private String podNumber;
    /**
     * 压测配置信息
     */
    private EnginePressureConfig pressureConfig;
    /**
     * 引擎压测模式
     */
    private Integer enginePressureMode;
    /**
     * 当前压测模式枚举
     */
    private PressureSceneEnum pressureScene;
    /**
     * 启动模式
     * <ul>
     *     <li>single</li>
     *     <li>double</li>
     * </ul>
     */
    private String startMode;
    /**
     * 采样率
     */
    private Integer traceSampling;
    /**
     * cloud回调地址
     */
    private String cloudCallbackUrl;
    /**
     * 动态TPS地址
     */
    private String dynamicTaskTpsUrl;
    /**
     * 动态TPS地址
     */
    private String csvPositionUrl;
    /**
     * jar文件地址
     */
    private List<String> jarFilePathList;
    /**
     * 脚本文件
     */
    private File scriptFile;
    /**
     * 压力引擎后端监听器对列长度
     */
    private String pressureEngineBackendQueueCapacity;
    /**
     * 全局用户参数
     */
    private GlobalUserVariables globalUserVariables;
    /**
     * 请求头参数
     */
    private HttpHeaderVariables httpHeaderVariables;

    /**
     * 秘钥和版本的键值对
     */
    private Map<Integer,String> privateKeyMap;
}
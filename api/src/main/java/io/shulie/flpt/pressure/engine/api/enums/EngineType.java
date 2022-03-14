package io.shulie.flpt.pressure.engine.api.enums;

/**
 * @author xuyh
 */
public enum EngineType {
    /**
     * jmeter
     */
    JMETER("jmeter"),
    /**
     * httpRunner
     */
    HTTP_RUNNER("httpRunner"),
    /**
     * loadRunner
     */
    LOAD_RUNNER("loadRunner"),
    /**
     * apacheAb
     */
    APACHE_AB("apacheAb");

    private final String type;

    EngineType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "EngineType{" +
            "type='" + type + '\'' +
            '}';
    }

    public static EngineType getByType(String type) {
        for (EngineType engineType : EngineType.values()) {
            if (engineType.getType().equals(type)) {
                return engineType;
            }
        }
        return JMETER;
    }
}
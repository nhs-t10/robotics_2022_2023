package dev.autoauto.runtime.values;

public interface AutoautoPropertyBearingObject {
    AutoautoValue getProperty(AutoautoValue prop);
    void setProperty(AutoautoValue prop, AutoautoValue value);
    boolean hasProperty(AutoautoValue prop);
}

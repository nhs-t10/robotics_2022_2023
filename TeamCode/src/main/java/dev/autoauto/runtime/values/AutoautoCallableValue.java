package dev.autoauto.runtime.values;

public interface AutoautoCallableValue {
    String[] getArgNames();
    AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args);
}

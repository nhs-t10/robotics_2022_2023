package dev.autoauto.runtime.values.prototype;

import dev.autoauto.runtime.values.AutoautoCallableValue;
import dev.autoauto.runtime.values.AutoautoValue;

public class PrototypePropertyDescriptor {
    public final AutoautoCallableValue getter;
    public final AutoautoCallableValue setter;

    public final AutoautoValue value;

    public final boolean ownProperty;

    public PrototypePropertyDescriptor(AutoautoValue value, boolean ownProperty) {
        getter = null;
        setter = null;
        this.value = value;

        this.ownProperty = ownProperty;
    }

    public PrototypePropertyDescriptor(AutoautoValue value) {
        getter = null;
        setter = null;
        this.value = value;

        this.ownProperty = false;
    }
    public PrototypePropertyDescriptor(AutoautoCallableValue getter, AutoautoCallableValue setter) {
        this.getter = getter;
        this.setter = setter;
        value = null;

        this.ownProperty = false;
    }
}

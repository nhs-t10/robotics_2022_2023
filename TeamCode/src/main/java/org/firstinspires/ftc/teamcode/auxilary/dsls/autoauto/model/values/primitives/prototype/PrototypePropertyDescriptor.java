package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public class PrototypePropertyDescriptor {
    public final AutoautoCallableValue getter;
    public final AutoautoCallableValue setter;

    public final AutoautoPrimitive value;

    public final boolean ownProperty;

    public PrototypePropertyDescriptor(AutoautoPrimitive value, boolean ownProperty) {
        getter = null;
        setter = null;
        this.value = value;

        this.ownProperty = ownProperty;
    }

    public PrototypePropertyDescriptor(AutoautoPrimitive value) {
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

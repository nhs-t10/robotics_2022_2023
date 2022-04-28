package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface AutoautoPropertyBearingObject {
    AutoautoPrimitive getProperty(AutoautoPrimitive prop);
    void setProperty(AutoautoPrimitive prop, AutoautoPrimitive value);
    boolean hasProperty(AutoautoPrimitive prop);
}

package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoGreaterThanOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opGreaterThan(AutoautoPrimitive other, boolean otherIsLeft);
}

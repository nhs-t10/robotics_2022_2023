package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoLessThanOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opLessThan(AutoautoPrimitive other, boolean otherIsLeft);
}

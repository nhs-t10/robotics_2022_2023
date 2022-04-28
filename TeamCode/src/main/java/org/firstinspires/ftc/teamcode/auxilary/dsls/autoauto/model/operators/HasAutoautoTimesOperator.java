package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoTimesOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opTimes(AutoautoPrimitive other, boolean otherIsLeft);
}

package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoMinusOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opMinus(AutoautoPrimitive other, boolean otherIsLeft);
}

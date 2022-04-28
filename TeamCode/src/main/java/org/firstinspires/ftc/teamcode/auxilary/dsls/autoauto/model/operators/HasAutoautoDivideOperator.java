package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoDivideOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opDivide(AutoautoPrimitive other, boolean otherIsLeft);
}

package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoEqualsOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opEquals(AutoautoPrimitive other, boolean otherIsLeft);
}

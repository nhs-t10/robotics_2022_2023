package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoNequalsOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opNequals(AutoautoPrimitive other, boolean otherIsLeft);
}

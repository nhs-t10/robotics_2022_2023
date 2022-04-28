package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoPlusOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opPlus(AutoautoPrimitive other, boolean otherIsLeft);
}

package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoExpOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opExp(AutoautoPrimitive other, boolean otherIsLeft);
}

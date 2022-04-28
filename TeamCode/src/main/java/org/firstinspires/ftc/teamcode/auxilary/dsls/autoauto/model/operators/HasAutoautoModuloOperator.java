package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoModuloOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opModulo(AutoautoPrimitive other, boolean otherIsLeft);
}

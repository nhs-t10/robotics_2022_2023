package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;

public interface HasAutoautoGrequalsOperator extends HasAutoautoOperatorInterface {
    AutoautoPrimitive opGrequals(AutoautoPrimitive other, boolean otherIsLeft);
}

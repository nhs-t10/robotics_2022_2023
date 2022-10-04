package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoGrequalsOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opGrequals(AutoautoValue other, boolean otherIsLeft);
}

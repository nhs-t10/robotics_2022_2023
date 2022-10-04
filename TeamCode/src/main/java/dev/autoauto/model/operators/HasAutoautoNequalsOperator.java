package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoNequalsOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opNequals(AutoautoValue other, boolean otherIsLeft);
}

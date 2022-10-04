package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoLequalsOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opLequals(AutoautoValue other, boolean otherIsLeft);
}

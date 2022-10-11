package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoLessThanOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opLessThan(AutoautoValue other, boolean otherIsLeft);
}

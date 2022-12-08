package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoGreaterThanOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opGreaterThan(AutoautoValue other, boolean otherIsLeft);
}

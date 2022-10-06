package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoExpOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opExp(AutoautoValue other, boolean otherIsLeft);
}

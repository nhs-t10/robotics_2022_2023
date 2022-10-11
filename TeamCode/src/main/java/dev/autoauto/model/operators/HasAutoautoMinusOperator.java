package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoMinusOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opMinus(AutoautoValue other, boolean otherIsLeft);
}

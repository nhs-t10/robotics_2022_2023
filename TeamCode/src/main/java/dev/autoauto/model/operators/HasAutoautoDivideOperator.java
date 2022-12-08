package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoDivideOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opDivide(AutoautoValue other, boolean otherIsLeft);
}

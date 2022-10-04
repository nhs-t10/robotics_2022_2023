package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoPlusOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opPlus(AutoautoValue other, boolean otherIsLeft);
}

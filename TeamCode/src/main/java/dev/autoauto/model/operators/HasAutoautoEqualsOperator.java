package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoEqualsOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opEquals(AutoautoValue other, boolean otherIsLeft);
}

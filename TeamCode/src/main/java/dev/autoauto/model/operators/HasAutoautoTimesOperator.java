package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoTimesOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opTimes(AutoautoValue other, boolean otherIsLeft);
}

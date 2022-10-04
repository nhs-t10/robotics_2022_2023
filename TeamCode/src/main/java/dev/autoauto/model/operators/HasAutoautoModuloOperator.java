package dev.autoauto.model.operators;

import dev.autoauto.runtime.values.AutoautoValue;

public interface HasAutoautoModuloOperator extends HasAutoautoOperatorInterface {
    AutoautoValue opModulo(AutoautoValue other, boolean otherIsLeft);
}

package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.AutoautoOperation;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoDivideOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoEqualsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoExpOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoGreaterThanOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoGrequalsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoLequalsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoLessThanOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoMinusOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoModuloOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoNequalsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoOperatorInterface;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoPlusOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoTimesOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoArgumentException;
import org.jetbrains.annotations.NotNull;

public class ArithmeticValue extends AutoautoValue {
    private final Class<? extends HasAutoautoOperatorInterface> operatorClass;
    String operator;
    public AutoautoValue right;
    public AutoautoValue left;

    AutoautoPrimitive returnValue;

    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public ArithmeticValue(String operator) {
        this.operator = operator;
        this.operatorClass = getOperatorClass(operator);
    }

    public static ArithmeticValue O(AutoautoValue left, String operator, AutoautoValue right) {
        return new ArithmeticValue(left, operator, right);
    }

    public ArithmeticValue(AutoautoValue left, String operator, AutoautoValue right) {
        this.left = left;
        this.operator = operator;
        this.operatorClass = getOperatorClass(operator);
        this.right = right;
    }

    private Class<? extends HasAutoautoOperatorInterface> getOperatorClass(String operator) {
        switch(operator) {
            case "+": return HasAutoautoPlusOperator.class;
            case "-": return HasAutoautoMinusOperator.class;
            case "*": return HasAutoautoTimesOperator.class;
            case "%": return HasAutoautoModuloOperator.class;
            case "/": return HasAutoautoDivideOperator.class;
            case "^":
            case "**": return HasAutoautoExpOperator.class;
            case "<": return HasAutoautoLessThanOperator.class;
            case "<=": return HasAutoautoLequalsOperator.class;
            case "==": return HasAutoautoEqualsOperator.class;
            case "!=": return HasAutoautoNequalsOperator.class;
            case ">=": return HasAutoautoGrequalsOperator.class;
            case ">": return HasAutoautoGreaterThanOperator.class;
        }
        throw new AutoautoArgumentException("No operator " + operator);
    }

    @Override
    public void init() {
        left.init();
        right.init();
    }

    public void loop() {
        left.loop();
        right.loop();

        AutoautoPrimitive leftRes = left.getResolvedValue();
        AutoautoPrimitive rightRes = right.getResolvedValue();

        this.returnValue = AutoautoOperation.invokeOperation(leftRes, rightRes, operatorClass, operator);
    }

    @NotNull
    @Override
    public String getString() {
        return returnValue.getString();
    }

    @Override
    public ArithmeticValue clone() {
        ArithmeticValue c = new ArithmeticValue(left.clone(), operator, right.clone());
        c.setLocation(location);
        return c;
    }

    @NotNull
    public String toString() {
        return left + " " + operator + " " + right;
    }

    @NotNull
    @Override
    public AutoautoPrimitive getResolvedValue() {
        return this.returnValue;
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        left.setScope(scope);
        right.setScope(scope);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
}

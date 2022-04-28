package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoArgumentException;

public class AutoautoOperation {
    public static AutoautoPrimitive invokeOperation(AutoautoPrimitive left, AutoautoPrimitive right, Class<? extends HasAutoautoOperatorInterface> operatorClass, String operator) {
        //replace nan (not-a-number) with undefined
        return replaceNanWithUndefined(
            unNanSafeInvokeOperation(
                    replaceNanWithUndefined(left),
                    replaceNanWithUndefined(right),
                    operatorClass, operator
            )
        );
    }
    private static AutoautoPrimitive unNanSafeInvokeOperation(AutoautoPrimitive left, AutoautoPrimitive right, Class<? extends HasAutoautoOperatorInterface> operatorClass, String operator) {

        //Choose the widest datatype; if there's a tie, choose left, since we read left-to-right.
        boolean chooseLeft = left.dataWidth() >= right.dataWidth();
        AutoautoPrimitive chosen = chooseLeft ? left : right;

        //let people define their own overloads!
        if(chosen instanceof AutoautoTable &&
                chosen.hasProperty(new AutoautoString(AutoautoSystemVariableNames.OPERATOR_OVERLOADING_PREFIX + operator))) {
            AutoautoPrimitive func = ((AutoautoTable) chosen).getProperty(AutoautoSystemVariableNames.OPERATOR_OVERLOADING_PREFIX + operator);

            AutoautoPrimitive nonChosen = chooseLeft ? right : left;

            if(func instanceof AutoautoCallableValue) {
                return ((AutoautoCallableValue) func).call(chosen,
                        new AutoautoPrimitive[]{
                                nonChosen, new AutoautoBooleanValue(!chooseLeft)
                        });
            } else {
                return func;
            }
        } else if(chooseLeft && operatorClass.isInstance(right)) {
            return dispatchOperator(operator, left, right, false);
        } else if(operatorClass.isInstance(left)) {
            return dispatchOperator(operator, right, left, true);
        } //if neither one offers this operator, try casting it to a string? and then redo with that?
        else if(operatorClass.isAssignableFrom(AutoautoString.class)) {
            return dispatchOperator(operator, right.castToString(), left.castToString(), !chooseLeft);
        } //if it's *still* nothing, maybe number could do it?
        else if(operatorClass.isAssignableFrom(AutoautoNumericValue.class)) {
            return dispatchOperator(operator, right.castToNumber(), left.castToNumber(), !chooseLeft);
        } //give up and do `undefined`
        else {
            return new AutoautoUndefined();
        }

    }
    private static AutoautoPrimitive replaceNanWithUndefined(AutoautoPrimitive value) {
        if(value instanceof AutoautoNumericValue) {
            if(Float.isNaN(((AutoautoNumericValue) value).value)) return new AutoautoUndefined();
        }
        //if we haven't returned undefined,
        return value;
    }

    private static AutoautoPrimitive dispatchOperator(String operator,
                                               AutoautoPrimitive evaluateOn, AutoautoPrimitive other, boolean otherIsLeft) {
        switch(operator) {
            case "+": return ((HasAutoautoPlusOperator)evaluateOn).opPlus(other, otherIsLeft);
            case "-": return ((HasAutoautoMinusOperator)evaluateOn).opMinus(other, otherIsLeft);
            case "*": return ((HasAutoautoTimesOperator)evaluateOn).opTimes(other, otherIsLeft);
            case "%": return ((HasAutoautoModuloOperator)evaluateOn).opModulo(other, otherIsLeft);
            case "/": return ((HasAutoautoDivideOperator)evaluateOn).opDivide(other, otherIsLeft);
            case "^":
            case "**": return ((HasAutoautoExpOperator)evaluateOn).opExp(other, otherIsLeft);
            case "<": return ((HasAutoautoLessThanOperator)evaluateOn).opLessThan(other, otherIsLeft);
            case "<=": return ((HasAutoautoLequalsOperator)evaluateOn).opLequals(other, otherIsLeft);
            case "==": return ((HasAutoautoEqualsOperator)evaluateOn).opEquals(other, otherIsLeft);
            case "!=": return ((HasAutoautoNequalsOperator)evaluateOn).opNequals(other, otherIsLeft);
            case ">=": return ((HasAutoautoGrequalsOperator)evaluateOn).opGrequals(other, otherIsLeft);
            case ">": return ((HasAutoautoGreaterThanOperator)evaluateOn).opGreaterThan(other, otherIsLeft);
        }
        throw new AutoautoArgumentException("No operator " + operator);
    }
}

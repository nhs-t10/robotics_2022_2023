package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
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
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.number.NumericPrototype;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.jetbrains.annotations.NotNull;

public class AutoautoNumericValue extends AutoautoPrimitive implements
        AutoautoCallableValue, HasAutoautoDivideOperator, HasAutoautoEqualsOperator,
        HasAutoautoExpOperator, HasAutoautoGreaterThanOperator, HasAutoautoGrequalsOperator,
        HasAutoautoLequalsOperator, HasAutoautoLessThanOperator, HasAutoautoMinusOperator,
        HasAutoautoModuloOperator, HasAutoautoNequalsOperator, HasAutoautoOperatorInterface,
        HasAutoautoPlusOperator, HasAutoautoTimesOperator {
    public float value;

    private Location location;
    private AutoautoRuntimeVariableScope scope;

    public static AutoautoNumericValue C(float value) {
        return new AutoautoNumericValue(value);
    }
    public static AutoautoNumericValue C(double value) {
        return new AutoautoNumericValue(value);
    }

    public AutoautoNumericValue(float value) {
        setPrototype(NumericPrototype.getMap());
        this.value = value;
    }
    public AutoautoNumericValue(double value) {
        this((float)value);
    }

    public float getFloat() {
        return value;
    }

    public String toString() {
        return getString();
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @NotNull
    @Override
    public String getString() {
        if((int)value == value) return (int)value + "";
        else if(Float.isNaN(value)) return (new AutoautoUndefined()).getString();
        else return value + "";
    }

    @Override
    public AutoautoNumericValue clone() {
        AutoautoNumericValue c = new AutoautoNumericValue(value);
        c.setLocation(location);
        return c;
    }

    @Override
    public int dataWidth() {
        return 2;
    }

    @Override
    public String getJSONString() {
        return value + "";
    }


    /*
    * Numbers are callable ONLY so both Java-esque <code>str.length()</code> and JS-esque <code>str.length</code> work. They just return themself.
    */

    @Override
    public String[] getArgNames() { return new String[0];}
    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisValue, AutoautoPrimitive[] args) { return this; }

    @Override
    public AutoautoPrimitive opDivide(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(other.castToNumber().value / this.value);
        else return new AutoautoNumericValue(this.value / other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opEquals(AutoautoPrimitive other, boolean otherIsLeft) {
        if(Float.isNaN(value) && Float.isNaN(other.castToNumber().value)) return new AutoautoBooleanValue(true);
        else return new AutoautoBooleanValue(other.castToNumber().value == this.value);
    }

    @Override
    public AutoautoPrimitive opExp(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(Math.pow(other.castToNumber().value, this.value));
        else return new AutoautoNumericValue(Math.pow(this.value, other.castToNumber().value));
    }

    @Override
    public AutoautoPrimitive opGreaterThan(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value > this.value);
        else return new AutoautoBooleanValue(this.value > other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opGrequals(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value >= this.value);
        else return new AutoautoBooleanValue(this.value >= other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opLequals(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value <= this.value);
        else return new AutoautoBooleanValue(this.value <= other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opLessThan(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value < this.value);
        else return new AutoautoBooleanValue(this.value < other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opMinus(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(other.castToNumber().value - this.value);
        else return new AutoautoNumericValue(this.value - other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opModulo(AutoautoPrimitive other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(other.castToNumber().value % this.value);
        else return new AutoautoNumericValue(this.value % other.castToNumber().value);
    }

    @Override
    public AutoautoPrimitive opNequals(AutoautoPrimitive other, boolean otherIsLeft) {
        return new AutoautoBooleanValue(other.castToNumber().value != this.value);
    }

    @Override
    public AutoautoPrimitive opPlus(AutoautoPrimitive other, boolean otherIsLeft) {
        return new AutoautoNumericValue(other.castToNumber().value + this.value);
    }

    @Override
    public AutoautoPrimitive opTimes(AutoautoPrimitive other, boolean otherIsLeft) {
        return new AutoautoNumericValue(other.castToNumber().value * this.value);
    }

    public int getInt() {
        return (int)value;
    }
}

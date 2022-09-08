package dev.autoauto.runtime.values;

import dev.autoauto.model.Location;
import dev.autoauto.model.operators.HasAutoautoDivideOperator;
import dev.autoauto.model.operators.HasAutoautoEqualsOperator;
import dev.autoauto.model.operators.HasAutoautoExpOperator;
import dev.autoauto.model.operators.HasAutoautoGreaterThanOperator;
import dev.autoauto.model.operators.HasAutoautoGrequalsOperator;
import dev.autoauto.model.operators.HasAutoautoLequalsOperator;
import dev.autoauto.model.operators.HasAutoautoLessThanOperator;
import dev.autoauto.model.operators.HasAutoautoMinusOperator;
import dev.autoauto.model.operators.HasAutoautoModuloOperator;
import dev.autoauto.model.operators.HasAutoautoNequalsOperator;
import dev.autoauto.model.operators.HasAutoautoOperatorInterface;
import dev.autoauto.model.operators.HasAutoautoPlusOperator;
import dev.autoauto.model.operators.HasAutoautoTimesOperator;
import dev.autoauto.runtime.values.prototype.number.NumericPrototype;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class AutoautoNumericValue extends AutoautoValue implements
        AutoautoCallableValue, HasAutoautoDivideOperator, HasAutoautoEqualsOperator,
        HasAutoautoExpOperator, HasAutoautoGreaterThanOperator, HasAutoautoGrequalsOperator,
        HasAutoautoLequalsOperator, HasAutoautoLessThanOperator, HasAutoautoMinusOperator,
        HasAutoautoModuloOperator, HasAutoautoNequalsOperator, HasAutoautoOperatorInterface,
        HasAutoautoPlusOperator, HasAutoautoTimesOperator {
    public final double value;

    private Location location;
    private AutoautoRuntimeVariableScope scope;

    public static AutoautoNumericValue C(float value) {
        return new AutoautoNumericValue(value);
    }
    public static AutoautoNumericValue C(double value) {
        return new AutoautoNumericValue(value);
    }

    public AutoautoNumericValue(double value) {
        setPrototype(NumericPrototype.getMap());
        this.value = value;
    }
    /*public float getFloat() {
        return (float)value;
    }*/

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
        else if(Double.isNaN(value)) return (new AutoautoUndefined()).getString();
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
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) { return this; }

    @Override
    public AutoautoValue opDivide(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(other.castToNumber().value / this.value);
        else return new AutoautoNumericValue(this.value / other.castToNumber().value);
    }

    @Override
    public AutoautoValue opEquals(AutoautoValue other, boolean otherIsLeft) {
        if(Double.isNaN(value) && Double.isNaN(other.castToNumber().value)) return new AutoautoBooleanValue(true);
        else return new AutoautoBooleanValue(other.castToNumber().value == this.value);
    }

    @Override
    public AutoautoValue opExp(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(Math.pow(other.castToNumber().value, this.value));
        else return new AutoautoNumericValue(Math.pow(this.value, other.castToNumber().value));
    }

    @Override
    public AutoautoValue opGreaterThan(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value > this.value);
        else return new AutoautoBooleanValue(this.value > other.castToNumber().value);
    }

    @Override
    public AutoautoValue opGrequals(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value >= this.value);
        else return new AutoautoBooleanValue(this.value >= other.castToNumber().value);
    }

    @Override
    public AutoautoValue opLequals(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value <= this.value);
        else return new AutoautoBooleanValue(this.value <= other.castToNumber().value);
    }

    @Override
    public AutoautoValue opLessThan(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoBooleanValue(other.castToNumber().value < this.value);
        else return new AutoautoBooleanValue(this.value < other.castToNumber().value);
    }

    @Override
    public AutoautoValue opMinus(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(other.castToNumber().value - this.value);
        else return new AutoautoNumericValue(this.value - other.castToNumber().value);
    }

    @Override
    public AutoautoValue opModulo(AutoautoValue other, boolean otherIsLeft) {
        if(otherIsLeft) return new AutoautoNumericValue(other.castToNumber().value % this.value);
        else return new AutoautoNumericValue(this.value % other.castToNumber().value);
    }

    @Override
    public AutoautoValue opNequals(AutoautoValue other, boolean otherIsLeft) {
        return new AutoautoBooleanValue(other.castToNumber().value != this.value);
    }

    @Override
    public AutoautoValue opPlus(AutoautoValue other, boolean otherIsLeft) {
        return new AutoautoNumericValue(other.castToNumber().value + this.value);
    }

    @Override
    public AutoautoValue opTimes(AutoautoValue other, boolean otherIsLeft) {
        return new AutoautoNumericValue(other.castToNumber().value * this.value);
    }

    public int getInt() {
        return (int)value;
    }

    public double getDouble() {
        return value;
    }

    public float getFloat() {
        return (float)value;
    }
}

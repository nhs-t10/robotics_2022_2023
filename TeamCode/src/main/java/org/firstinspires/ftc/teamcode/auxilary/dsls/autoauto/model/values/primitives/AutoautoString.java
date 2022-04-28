package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoEqualsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoGreaterThanOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoGrequalsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoLequalsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoLessThanOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoNequalsOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoPlusOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.operators.HasAutoautoTimesOperator;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.string.StringPrototype;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class AutoautoString extends AutoautoPrimitive implements
        HasAutoautoPlusOperator, HasAutoautoEqualsOperator, HasAutoautoNequalsOperator, HasAutoautoTimesOperator,
        HasAutoautoGrequalsOperator, HasAutoautoGreaterThanOperator, HasAutoautoLequalsOperator, HasAutoautoLessThanOperator {
    public String value;
    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public static AutoautoString U (String value) {
        return new AutoautoString(value);
    }

    public AutoautoString(String value) {
        setPrototype(StringPrototype.getMap());
        this.value = value;
    }

    @NotNull
    public String toString() {
        return PaulMath.JSONify(value);
    }

    @NotNull
    public String getString() {
        return value;
    }

    @Override
    public AutoautoString clone() {
        AutoautoString c = new AutoautoString(value);
        c.setLocation(location);
        return c;
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

    @Override
    public String getJSONString() {
        return PaulMath.JSONify(value);
    }

    @Override
    public int dataWidth() {
        return 6;
    }


    public static AutoautoString fromJSON(String str) {
        String trim = str.trim();
        if(trim.startsWith("\"")) trim = trim.substring(1, trim.length() - 1);

        return new AutoautoString(PaulMath.unescapeString(trim));
    }

    @Override
    public AutoautoPrimitive opEquals(AutoautoPrimitive other, boolean otherIsLeft) {
        return new AutoautoBooleanValue(  value.equals(other.getString())  );
    }

    @Override
    public AutoautoPrimitive opGreaterThan(AutoautoPrimitive other, boolean otherIsLeft) {
        String o = other.getString();

        if(otherIsLeft) return new AutoautoBooleanValue(o.compareTo(value) > 0);
        else return new AutoautoBooleanValue(value.compareTo(o) > 0);
    }

    @Override
    public AutoautoPrimitive opGrequals(AutoautoPrimitive other, boolean otherIsLeft) {
        String o = other.getString();

        if(otherIsLeft) return new AutoautoBooleanValue(o.compareTo(value) >= 0);
        else return new AutoautoBooleanValue(value.compareTo(o) >= 0);
    }

    @Override
    public AutoautoPrimitive opLequals(AutoautoPrimitive other, boolean otherIsLeft) {
        String o = other.getString();

        if(otherIsLeft) return new AutoautoBooleanValue(o.compareTo(value) <= 0);
        else return new AutoautoBooleanValue(value.compareTo(o) <= 0);
    }

    @Override
    public AutoautoPrimitive opLessThan(AutoautoPrimitive other, boolean otherIsLeft) {
        String o = other.getString();

        if(otherIsLeft) return new AutoautoBooleanValue(o.compareTo(value) < 0);
        else return new AutoautoBooleanValue(value.compareTo(o) < 0);
    }

    @Override
    public AutoautoPrimitive opNequals(AutoautoPrimitive other, boolean otherIsLeft) {
        return new AutoautoBooleanValue(  other.getString().equals(value) == false  );
    }

    @Override
    public AutoautoPrimitive opPlus(AutoautoPrimitive other, boolean otherIsLeft) {
        String o = other.getString();

        if(otherIsLeft) return new AutoautoString(o + value);
        else return new AutoautoString(value + o);
    }

    @Override
    public AutoautoPrimitive opTimes(AutoautoPrimitive other, boolean otherIsLeft) {
        if(other instanceof AutoautoNumericValue) {
            String repeated = PaulMath.repeat(value, (int) (((AutoautoNumericValue) other).value));
            return new AutoautoString(repeated);
        } else {
            return this;
        }
    }
}

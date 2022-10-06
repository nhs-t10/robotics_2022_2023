package dev.autoauto.runtime.values;

import dev.autoauto.model.Location;
import dev.autoauto.runtime.values.prototype.bool.BooleanPrototype;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class AutoautoBooleanValue extends AutoautoValue {
    public boolean value;

    private Location location;
    private AutoautoRuntimeVariableScope scope;

    public static AutoautoBooleanValue B (boolean value) {
        return new AutoautoBooleanValue(value);
    }

    public AutoautoBooleanValue(boolean value) {
        setPrototype(BooleanPrototype.getMap());
        this.value = value;
    }

    public static boolean isTruthy(AutoautoValue v) {
        return !isFalsy(v);
    }

    private static boolean isFalsy(AutoautoValue v) {
        if(v instanceof AutoautoBooleanValue) return !((AutoautoBooleanValue) v).value;
        else if (v instanceof AutoautoString) return ((AutoautoString) v).value.length() == 0;
        else if(v instanceof AutoautoNumericValue) return ((AutoautoNumericValue) v).getDouble() == 0.0;
        else if(v instanceof AutoautoTable) return !((AutoautoTable) v).isEmpty();
        else if(v instanceof AutoautoUndefined) return true;
        else return true;
    }

    public boolean getBoolean() {
        return value;
    }

    public String toString() {
        return this.value + "";
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
        return value + "";
    }

    @Override
    public AutoautoBooleanValue clone() {
        AutoautoBooleanValue c = new AutoautoBooleanValue(value);
        c.setLocation(location);
        return c;
    }

    @Override
    public int dataWidth() {
        return 1;
    }

    @Override
    public String getJSONString() {
        return value + "";
    }
}

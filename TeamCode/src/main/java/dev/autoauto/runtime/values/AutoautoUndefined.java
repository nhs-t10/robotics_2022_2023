package dev.autoauto.runtime.values;

import dev.autoauto.model.Location;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import dev.autoauto.runtime.AutoautoSystemVariableNames;
import dev.autoauto.runtime.errors.AutoautoNameException;
import org.jetbrains.annotations.NotNull;

public class AutoautoUndefined extends AutoautoValue {
    private boolean complainAboutUndefinedUsage = false;

    public static final int USER_CODE_OR_MATHEMATICAL_USAGE = 0;
    public static final int NONEXISTENT_VARIABLE = 1;

    private Location location;

    public int source;
    private AutoautoRuntimeVariableScope scope;

    public AutoautoUndefined(int source) {
        if(source != USER_CODE_OR_MATHEMATICAL_USAGE) {
            if (complainAboutUndefinedUsage) throw new AutoautoNameException("Undefined Usage Error");
        }
        this.source = source;
    }

    public AutoautoUndefined() { this(USER_CODE_OR_MATHEMATICAL_USAGE); }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;

        complainAboutUndefinedUsage = (scope.get(AutoautoSystemVariableNames.FLAG_UNDEFINED_CAUSES_ERRORS) != null);
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
        return "undefined";
    }

    @Override
    public String getJSONString() {
        return "null";
    }

    @Override
    public AutoautoUndefined clone() {
        AutoautoUndefined c = new AutoautoUndefined();
        c.setLocation(location);
        return c;
    }
    public String toString() {
        return "undefined";
    }

    //make sure people don't try to set properties on undefined
    public AutoautoValue getProperty(AutoautoValue key) {
        if (complainAboutUndefinedUsage) throw new AutoautoNameException("Undefined Usage Error");
        return this;
    }
    public boolean hasProperty(AutoautoValue key) {
        if (complainAboutUndefinedUsage) throw new AutoautoNameException("Undefined Usage Error");
        return false;
    }

    public void setProperty(AutoautoValue key, AutoautoValue value) {
        if (complainAboutUndefinedUsage) throw new AutoautoNameException("Undefined Usage Error");
    }

    public void deleteProperty(AutoautoValue key) {
        if (complainAboutUndefinedUsage) throw new AutoautoNameException("Undefined Usage Error");
    }

    @Override
    public int dataWidth() {
        return 0;
    }
}

package dev.autoauto.runtime.values;

import dev.autoauto.model.Location;
import dev.autoauto.runtime.values.prototype.relation.RelationPrototype;
import dev.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

public class AutoautoRelation extends AutoautoValue {
    public AutoautoValue title;
    public AutoautoValue value;
    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public AutoautoRelation(AutoautoValue title, AutoautoValue value) {
        setPrototype(RelationPrototype.getMap());
        this.title = title;
        this.value = value;
    }

    @Override
    public String getJSONString() {
        return "{\"title\":" + title.getJSONString() + ",\"value:\"" + value.getJSONString() + "}";
    }

    @NotNull
    @Override
    public String getString() {
        return value.getString();
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        title.setScope(scope);
        value.setScope(scope);
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
    public AutoautoRelation clone() {
        return new AutoautoRelation(title.clone(), value.clone());
    }

    public String toString() {
        return title + " = " + value;
    }

    @Override
    public int dataWidth() {
        return 3;
    }
}

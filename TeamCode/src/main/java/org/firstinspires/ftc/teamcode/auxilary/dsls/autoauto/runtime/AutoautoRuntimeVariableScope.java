package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime;

import androidx.annotation.Nullable;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoBooleanValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.encapsulation.AutoautoModule;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.AutoautoMathMethodsTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.DelegateFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.TypeofFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.ProvideFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.LogFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.ReturnFunction;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Set;

public class AutoautoRuntimeVariableScope {
    public HashMap<String, StoredAutoautoVariable> variables;

    @Nullable
    public AutoautoRuntimeVariableScope parentScope;

    @Nullable
    private AutoautoRuntimeVariableScope rootScope;

    public AutoautoRuntimeVariableScope() {
        this.variables = new HashMap<String, StoredAutoautoVariable>();
    }
    public AutoautoRuntimeVariableScope(@NotNull AutoautoRuntimeVariableScope parentScope) {
        this();
        this.parentScope = parentScope;
        this.rootScope = parentScope.getRoot();
    }

    public Set<String> getKeys() {
        return variables.keySet();
    }

    public void put(String s, AutoautoPrimitive v) {
        StoredAutoautoVariable value = getStored(s);

        if(value == null) {
            this.variables.put(s, new StoredAutoautoVariable(v));
        } else {
            if(value.systemManaged) {
                throw new IllegalArgumentException("Variable " + s + " is a system variable");
            } else if(value.readOnly) {
                throw new IllegalArgumentException("Variable " + s + " is a read-only variable");
            };
            value.value = v;
        }
    }

    @Nullable
    public AutoautoPrimitive get(String s) {
        if(this.variables.containsKey(s)) return this.variables.get(s).value;
        else if(parentScope != null) return parentScope.get(s);
        else return null;
    }

    public StoredAutoautoVariable getConsistentVariableHandle(String s) {
        return getStored(s);
    }

    private StoredAutoautoVariable getStored(String s) {
        if(this.variables.containsKey(s)) return this.variables.get(s);
        else if(parentScope != null) return parentScope.getStored(s);
        else return null;
    }

    public void systemRemove(String s) {
        if(this.variables.containsKey(s)) this.variables.remove(s);
        else if(parentScope != null) parentScope.systemRemove(s);
    }

    public void systemSet(String s, AutoautoPrimitive v) {
        StoredAutoautoVariable value = this.variables.get(s);

        if(value == null) {
            value = new StoredAutoautoVariable(v);
            value.systemManaged = true;
            this.variables.put(s, value);
        } else {
            if(!value.systemManaged) {
                throw new IllegalArgumentException("Variable " + s + " is not a system variable; it cannot be systemSet.");
            }
            if(value.readOnly) {
                throw new IllegalArgumentException("Variable " + s + " is a read-only variable");
            }
            value.value = v;
        }
    }

    /**
     * Init "syntactical sugar" variables, such as `pi`, that make it easier for coders to do their job
     */
    public void initSugarVariables() {
        this.systemSet("pi", new AutoautoNumericValue((float) Math.PI));
        this.systemSet("e", new AutoautoNumericValue((float) Math.E));

        this.systemSet("True", new AutoautoBooleanValue(true));
        this.systemSet("true", new AutoautoBooleanValue(true));
        this.systemSet("TRUE", new AutoautoBooleanValue(true));
        this.systemSet("False", new AutoautoBooleanValue(false));
        this.systemSet("false", new AutoautoBooleanValue(false));
        this.systemSet("FALSE", new AutoautoBooleanValue(false));

        this.systemSet("undefined", new AutoautoUndefined());
        this.systemSet("null", new AutoautoUndefined());
        this.systemSet("delete", new AutoautoUndefined());

        this.systemSet("Math", new AutoautoMathMethodsTable());
    }

    public void initBuiltinFunctions(AutoautoModule module, RobotFunctionLoader robotFunctions) {
        this.systemSet("log", new LogFunction());

        this.systemSet("return", new ReturnFunction());

        this.systemSet("typeof", new TypeofFunction());

        this.systemSet("delegate", new DelegateFunction(module.address, robotFunctions));
        this.systemSet("provide", new ProvideFunction());

        robotFunctions.loadFunctions(this);
    }

    public boolean isTopLevel() {
        return this.parentScope == null;
    }

    public AutoautoRuntimeVariableScope getRoot() {
        if(rootScope == null) return this;
        else return rootScope;
    }

    public void remove(String s) {
        if(this.variables.containsKey(s)) this.variables.remove(s);
        else if(parentScope != null) parentScope.remove(s);
    }
}

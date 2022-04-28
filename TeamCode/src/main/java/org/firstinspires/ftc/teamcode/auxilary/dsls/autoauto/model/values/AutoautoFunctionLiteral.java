package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.State;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AutoautoFunctionLiteral extends AutoautoValue {
    private final AutoautoValue[] argNames;
    private final State body;

    private AutoautoFunction resolvedValue;

    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public static AutoautoFunctionLiteral Q (AutoautoValue[] argNames, State val) {
        return new AutoautoFunctionLiteral(argNames, val);
    }

    public AutoautoFunctionLiteral(AutoautoValue[] argNames, State body) {
        this.argNames = argNames;
        this.body = body;

        for(int i = 0; i < argNames.length; i++) {
            if(argNames[i] instanceof VariableReference) argNames[i] = new AutoautoString(((VariableReference)argNames[i]).getName());
        }
    }

    @NotNull
    @Override
    public AutoautoPrimitive getResolvedValue() {
        return resolvedValue;
    }

    public void init() {
        for(AutoautoValue v : argNames) v.init();
        body.init();
    }

    @Override
    public AutoautoFunctionLiteral clone() {
        AutoautoValue[] clonedArgs = new AutoautoValue[argNames.length];
        for(int i = 0; i < clonedArgs.length; i++) clonedArgs[i] = argNames[i].clone();

        AutoautoFunctionLiteral c = new AutoautoFunctionLiteral(clonedArgs, body.clone());
        c.setLocation(location);
        return c;
    }

    public void loop() {
        body.stepInit();

        for(AutoautoValue v : argNames) v.loop();

        this.resolvedValue = makeFunction();
    }

    @NotNull
    @Override
    public String getString() {
        return Arrays.toString(argNames);
    }

    private AutoautoFunction makeFunction() {
        String[] a = new String[argNames.length];
        AutoautoPrimitive[] defaultValues = new AutoautoPrimitive[argNames.length];
        for(int i = 0; i < argNames.length; i++) {
            if(argNames[i] instanceof VariableReference) a[i] = ((VariableReference) argNames[i]).getName();
            else if(argNames[i] instanceof AutoautoPrimitive) a[i] = argNames[i].getString();
            else if(argNames[i] instanceof TitledArgument) a[i] = ((TitledArgument) argNames[i]).getResolvedValue().title.getString();
            else a[i] = "";

            if(argNames[i] instanceof TitledArgument) defaultValues[i] = ((TitledArgument) argNames[i]).getResolvedValue().value;
        }

        return new AutoautoFunction(body, a, defaultValues);
    }

    public String toString() {
        return "func (" + Arrays.toString(argNames) + ")";
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        for(AutoautoValue v : argNames) v.setScope(scope);
        this.body.setScope(scope);
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

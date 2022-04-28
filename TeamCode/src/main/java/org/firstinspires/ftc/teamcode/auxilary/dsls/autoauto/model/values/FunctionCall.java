package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoNameException;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class FunctionCall extends AutoautoValue {
    public AutoautoValue[] args;
    public AutoautoValue functionName;


    public AutoautoPrimitive returnValue;

    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public FunctionCall(AutoautoValue n, AutoautoValue[] a) {
        this.functionName = n;
        this.args = a;
    }

    public static FunctionCall M(AutoautoValue n, AutoautoValue[] a) {
        return new FunctionCall(n, a);
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        this.functionName.setScope(scope);
        for(int i = args.length - 1; i >= 0; i--) this.args[i].setScope(scope);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    public void init() {
        for(int i = args.length - 1; i >= 0; i--) {
            this.args[i].init();
        }
    }

    public void loop() throws AutoautoNameException {
        functionName.loop();

        AutoautoPrimitive val = functionName.getResolvedValue();

        if(!(val instanceof AutoautoCallableValue)) throw new AutoautoNameException("`" + val.toString() + "` is not a function" + formatStack());

        AutoautoCallableValue fn = (AutoautoCallableValue)val;

        AutoautoPrimitive context = new AutoautoUndefined();
        //if this function is being called *on* an object (e.g. `foo.bar()`), then that object will be the context!
        if(functionName instanceof AutoautoTailedValue) {
            context = ((AutoautoTailedValue)functionName).head.getResolvedValue();
        }

        //set scope! this makes nested functions work
        val.setScope(scope);

        //set location for error reporting purposes
        val.setLocation(location);

        for(AutoautoValue v : args) v.loop();

        String[] argNames = fn.getArgNames();
        AutoautoPrimitive[] argsResolved = new AutoautoPrimitive[Math.max(args.length, argNames.length)];

        for(int i = 0; i < argsResolved.length; i++) {
            if(i < args.length) {
                argsResolved[i] = args[i].getResolvedValue();
            } else {
                argsResolved[i] = new AutoautoUndefined();
            }
            //set the location it's being called from, for error reporting purposes
            if(argsResolved[i].getLocation() == null) argsResolved[i].setLocation(this.location);
        }
        //copy in titledarguments to their positions
        List<String> argNameList = Arrays.asList(argNames);
        for(int i = 0; i < args.length; i++) {
            if(args[i] instanceof TitledArgument) {
                String argTitle = ((TitledArgument) args[i]).getResolvedValue().title.getString();
                int index = argNameList.indexOf(argTitle);
                AutoautoPrimitive resolvedArg = ((TitledArgument) this.args[i]).getResolvedValue().value;

                if (index != -1) argsResolved[index] = resolvedArg;
            }
        }

        this.returnValue = fn.call(context, argsResolved);
    }

    @NotNull
    @Override
    public String getString() {
        if(returnValue == null) return "null";
        else return returnValue.getString();
    }

    @Override
    public FunctionCall clone() {
        AutoautoValue[] argsCloned = new AutoautoValue[args.length];
        for(int i = 0; i < argsCloned.length; i++) argsCloned[i] = args[i].clone();

        FunctionCall c = new FunctionCall(functionName.clone(), argsCloned);
        c.setLocation(location);
        return c;
    }

    @NotNull
    public AutoautoPrimitive getResolvedValue() {
        return this.returnValue;
    }

    @NotNull
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(this.functionName.toString() + "(");
        for(AutoautoValue arg : args) {
            if(arg == null) str.append("<null>");
            else str.append(arg.toString() + ", ");
        }
        String string = str.toString();

        return string.substring(0, string.length() - 2) + ")";
    }
}

package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.Location;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.State;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoString;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.TitledArgument;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.VariableReference;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;

public class FunctionDefStatement extends Statement {
    private AutoautoValue[] args;
    private State body;
    public AutoautoValue fName;

    private AutoautoRuntimeVariableScope scope;
    private Location location;

    public static FunctionDefStatement J (AutoautoValue functionName, AutoautoValue[] args, State val) {
        return new FunctionDefStatement(functionName, args, val);
    }
    public static FunctionDefStatement J (String functionName, AutoautoValue[] args, State val) {
        return new FunctionDefStatement(functionName, args, val);
    }
    public static FunctionDefStatement J (AutoautoValue functionName, String[] args, State val) {
        AutoautoValue[] aStr = new AutoautoValue[args.length];
        for(int i = 0; i < aStr.length; i++) aStr[i] = new AutoautoString(args[i]);

        return new FunctionDefStatement(functionName, aStr, val);
    }
    public static FunctionDefStatement J (String functionName, String[] args, State val) {
        AutoautoValue[] aStr = new AutoautoValue[args.length];
        for(int i = 0; i < aStr.length; i++) aStr[i] = new AutoautoString(args[i]);

        return new FunctionDefStatement(functionName, aStr, val);
    }
    public FunctionDefStatement(AutoautoValue fName, AutoautoValue[] args, State body) {
        this.fName = fName;
        this.args = args;
        this.body = body;

        for(int i = 0; i < args.length; i++) {
            if(args[i] instanceof VariableReference) args[i] = new AutoautoString(((VariableReference)args[i]).getName());
        }
    }

    public FunctionDefStatement(String fName, AutoautoValue[] args, State body) {
        this(new AutoautoString(fName), args, body);
    }

    public void init() {
        this.fName.init();
        for(AutoautoValue v : args) v.init();
        body.init();
    }

    @Override
    public FunctionDefStatement clone() {
        AutoautoValue[] clonedArgs = new AutoautoValue[args.length];
        for(int i = 0; i < clonedArgs.length; i++) clonedArgs[i] = args[i].clone();

        FunctionDefStatement c = new FunctionDefStatement(fName.clone(), clonedArgs, body.clone());
        c.setLocation(location);
        return c;
    }

    public void loop() {
        body.stepInit();

        fName.loop();
        for(AutoautoValue v : args) v.loop();

        AutoautoFunction func = makeFunction();
        this.scope.put(this.fName.getResolvedValue().getString(), func);
    }

    private AutoautoFunction makeFunction() {
        String[] a = new String[args.length];
        AutoautoPrimitive[] defaultValues = new AutoautoPrimitive[args.length];
        for(int i = 0; i < args.length; i++) {
            if(args[i] instanceof VariableReference) a[i] = ((VariableReference)args[i]).getName();
            else if(args[i] instanceof AutoautoPrimitive) a[i] = args[i].getString();
            else if(args[i] instanceof TitledArgument) a[i] = ((TitledArgument)args[i]).getResolvedValue().title.getString();
            else a[i] = "";

            if(args[i] instanceof TitledArgument) defaultValues[i] = ((TitledArgument)args[i]).getResolvedValue().value;
        }

        return new AutoautoFunction(body, a, defaultValues);
    }

    public String toString() {
        return "function " + this.fName.toString();
    }

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    @Override
    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        this.fName.setScope(scope);
        for(AutoautoValue v : args) v.setScope(scope);
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

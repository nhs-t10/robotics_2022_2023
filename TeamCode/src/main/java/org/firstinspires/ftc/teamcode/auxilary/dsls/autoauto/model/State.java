package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.statements.Statement;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.AutoautoCallableValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoRuntimeVariableScope;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.AutoautoSystemVariableNames;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.StoredAutoautoVariable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.AutoautoNameException;
import org.jetbrains.annotations.NotNull;

public class State implements AutoautoProgramElement {
    public Statement[] statements;
    public AutoautoRuntimeVariableScope scope;
    public Location location;
    private StoredAutoautoVariable storedStatepathVariable;
    private StoredAutoautoVariable storedStateNumberVariable;

    @Override
    public AutoautoRuntimeVariableScope getScope() {
        return scope;
    }

    public void setScope(AutoautoRuntimeVariableScope scope) {
        this.scope = scope;
        for(Statement p : this.statements) p.setScope(scope);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public static State A(Statement[] statements) {
        return new State(statements);
    }

    public State(Statement[] statements) {
        this.statements = statements;
    }

    public State clone() {
        Statement[] cloneStatements = new Statement[this.statements.length];
        for(int i = 0; i < cloneStatements.length; i++) cloneStatements[i] = this.statements[i].clone();
        State clone = new State(cloneStatements);

        clone.setLocation(location.clone());

        return clone;
    }

    @NotNull
    public String toString() {
        StringBuilder statementsStr = new StringBuilder();
        for(Statement statement : statements) {
            statementsStr.append(statement.toString() + ", ");
        }
        return statementsStr.toString();
    }

    public void stepInit() {
        for(Statement s : statements) {
            if(s != null) s.stepInit();
        }
        //ONLY when in the top-level scope, stop when new steps start
        if(scope.isTopLevel()) {
            AutoautoCallableValue stopDriveFunction = ((AutoautoCallableValue)scope.get("stopDrive"));
            if(stopDriveFunction == null) throw new AutoautoNameException("Missing essential function `stopDrive`");
            stopDriveFunction.call(new AutoautoUndefined(), new AutoautoPrimitive[0]);
        }
    }

    public void init() {
        this.storedStatepathVariable = scope.getConsistentVariableHandle(AutoautoSystemVariableNames.STATEPATH_NAME);
        this.storedStateNumberVariable = scope.getConsistentVariableHandle(AutoautoSystemVariableNames.STATE_NUMBER);

        for(Statement s : statements) {
            if(s != null) s.init();
        }
    }

    public void loop() {
        this.returnValue = new AutoautoUndefined();
        for(Statement s : statements) {
            if(!storedStatepathVariable.value.getString().equals(this.location.statepath)) break;
            if(((AutoautoNumericValue)storedStateNumberVariable.value).getFloat() != this.location.stateNumber) break;
            if(s != null) s.loop();

            //break if there's a return statement
            this.returnValue = this.scope.get(AutoautoSystemVariableNames.RETURNED_VALUE);
            if(this.returnValue != null) {
                this.returned = true;
                break;
            }
        }
    }

    //for if it's being used as a function body
    private AutoautoPrimitive returnValue;
    private boolean returned;
    public AutoautoPrimitive getReturnValue() {
        return returnValue == null ? new AutoautoUndefined() : returnValue;
    }
    public void setReturnValue(AutoautoPrimitive returnValue) {
        this.returnValue = returnValue;
        this.returned = true;
    }
}

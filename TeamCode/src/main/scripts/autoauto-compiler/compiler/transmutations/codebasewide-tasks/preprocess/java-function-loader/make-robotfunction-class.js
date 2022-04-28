module.exports = function(callMethodSource, definingClass, classname, argNames) {
    if(!argNames) argNames = "[]";
    else argNames = JSON.stringify(argNames);
    argNames = argNames.substring(1, argNames.length - 1);

    return `package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.robotfunctions;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.*;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.*;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeRobotFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.errors.ManagerSetupException;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

public class ${classname} extends NativeRobotFunction {
    private ${definingClass} manager;

    public ${classname}(FeatureManager manager) {
        this.manager = (${definingClass})manager;
    }

    private String[] argNames = new String[] { ${ argNames } };
    @Override
    public String[] getArgNames() {
        return argNames;
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisValue, AutoautoPrimitive[] args) {
        if(manager == null) throw new ManagerSetupException("No ${definingClass.substring(definingClass.lastIndexOf(".") + 1)}; please define one in template.notjava");
        ${callMethodSource}
    }
}`;
}
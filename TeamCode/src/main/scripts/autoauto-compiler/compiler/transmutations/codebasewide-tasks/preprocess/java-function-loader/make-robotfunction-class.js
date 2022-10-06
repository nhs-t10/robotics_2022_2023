"use strict";

module.exports = function(callMethodSource, definingClass, classname, argNames) {
    if(!argNames) argNames = "[]";
    else argNames = JSON.stringify(argNames);
    argNames = argNames.substring(1, argNames.length - 1);

    return `package dev.autoauto.runtime.robotfunctions;

import dev.autoauto.runtime.values.*;

public class ${classname} extends dev.autoauto.runtime.NativeRobotFunction {
    private ${definingClass} manager;

    public ${classname}(Object manager) {
        this.manager = (${definingClass})manager;
    }

    private String[] argNames = new String[] { ${ argNames } };
    @Override
    public String[] getArgNames() {
        return argNames;
    }

    @Override
    public AutoautoValue call(AutoautoValue thisValue, AutoautoValue[] args) {
        if(manager == null) throw new RuntimeException("No ${definingClass.substring(definingClass.lastIndexOf(".") + 1)}; please define one in template.notjava");
        ${callMethodSource}
    }
}`;
}
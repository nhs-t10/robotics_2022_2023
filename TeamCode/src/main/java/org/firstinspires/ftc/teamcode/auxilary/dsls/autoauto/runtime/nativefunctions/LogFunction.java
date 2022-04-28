package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoUndefined;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.NativeFunction;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

public class LogFunction extends NativeFunction {
    public String name = "log";
    public int argCount = 1;

    public LogFunction() {

    }

    @Override
    public String[] getArgNames() {
        return new String[0];
    }

    @Override
    public AutoautoPrimitive call(AutoautoPrimitive thisArg, AutoautoPrimitive[] args) {
        StringBuilder sb = new StringBuilder();

        for(AutoautoPrimitive a : args) {
            sb.append((a == null ? new AutoautoUndefined() : a).getString()).append('\t');
        }

        FeatureManager.logger.log(sb.toString());

        return new AutoautoUndefined();
    }
}
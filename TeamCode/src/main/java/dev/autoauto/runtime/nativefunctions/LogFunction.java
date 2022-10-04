package dev.autoauto.runtime.nativefunctions;

import dev.autoauto.runtime.values.AutoautoValue;
import dev.autoauto.runtime.values.AutoautoUndefined;
import dev.autoauto.runtime.NativeFunction;
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
    public AutoautoValue call(AutoautoValue thisArg, AutoautoValue[] args) {
        StringBuilder sb = new StringBuilder();

        for(AutoautoValue a : args) {
            sb.append((a == null ? new AutoautoUndefined() : a).getString()).append('\t');
        }

        FeatureManager.logger.log(sb.toString());

        return new AutoautoUndefined();
    }
}
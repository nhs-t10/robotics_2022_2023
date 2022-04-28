package org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions;

import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoNumericValue;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoPrimitive;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.AutoautoTable;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.model.values.primitives.prototype.PrototypePropertyDescriptor;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.absNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.acosNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.acoshNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.asinNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.asinhNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.atan2NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.atanNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.atanhNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.cbrtNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.ceilNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.clz32NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.cosNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.coshNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.expNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.expm1NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.floorNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.froundNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.hypotNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.imulNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.log10NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.log1pNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.log2NativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.logNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.maxNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.minNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.powNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.randomNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.roundNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.signNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.sinNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.sinhNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.sqrtNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.tanNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.tanhNativeFunction;
import org.firstinspires.ftc.teamcode.auxilary.dsls.autoauto.runtime.nativefunctions.math.truncNativeFunction;

import java.util.HashMap;

public class AutoautoMathMethodsTable extends AutoautoTable {
    public AutoautoMathMethodsTable() {
        setPrototype(makeMethodsMap());
    }

    private static HashMap<String, PrototypePropertyDescriptor> makeMethodsMap() {
        HashMap<String, PrototypePropertyDescriptor> methods = new HashMap<>();

        methods.put("E", new PrototypePropertyDescriptor(new AutoautoNumericValue(Math.E)));
        methods.put("LN2", new PrototypePropertyDescriptor(new AutoautoNumericValue(0.6931471805599453)));
        methods.put("LN10", new PrototypePropertyDescriptor(new AutoautoNumericValue(2.302585092994046)));
        methods.put("LOG2E", new PrototypePropertyDescriptor(new AutoautoNumericValue(1.4426950408889634)));
        methods.put("LOG10E", new PrototypePropertyDescriptor(new AutoautoNumericValue(0.4342944819032518)));
        methods.put("PI", new PrototypePropertyDescriptor(new AutoautoNumericValue(Math.PI)));
        methods.put("SQRT1_2", new PrototypePropertyDescriptor(new AutoautoNumericValue(Math.sqrt(0.5))));
        methods.put("SQRT2", new PrototypePropertyDescriptor(new AutoautoNumericValue(Math.sqrt(2))));

        methods.put("abs", new PrototypePropertyDescriptor(new absNativeFunction()));
        methods.put("acos", new PrototypePropertyDescriptor(new acosNativeFunction()));
        methods.put("acosh", new PrototypePropertyDescriptor(new acoshNativeFunction()));
        methods.put("asin", new PrototypePropertyDescriptor(new asinNativeFunction()));
        methods.put("asinh", new PrototypePropertyDescriptor(new asinhNativeFunction()));
        methods.put("atan", new PrototypePropertyDescriptor(new atanNativeFunction()));
        methods.put("atanh", new PrototypePropertyDescriptor(new atanhNativeFunction()));
        methods.put("atan2", new PrototypePropertyDescriptor(new atan2NativeFunction()));
        methods.put("cbrt", new PrototypePropertyDescriptor(new cbrtNativeFunction()));
        methods.put("ceil", new PrototypePropertyDescriptor(new ceilNativeFunction()));
        methods.put("clz32", new PrototypePropertyDescriptor(new clz32NativeFunction()));
        methods.put("cos", new PrototypePropertyDescriptor(new cosNativeFunction()));
        methods.put("cosh", new PrototypePropertyDescriptor(new coshNativeFunction()));
        methods.put("exp", new PrototypePropertyDescriptor(new expNativeFunction()));
        methods.put("expm1", new PrototypePropertyDescriptor(new expm1NativeFunction()));
        methods.put("floor", new PrototypePropertyDescriptor(new floorNativeFunction()));
        methods.put("fround", new PrototypePropertyDescriptor(new froundNativeFunction()));
        methods.put("hypot", new PrototypePropertyDescriptor(new hypotNativeFunction()));
        methods.put("imul", new PrototypePropertyDescriptor(new imulNativeFunction()));
        methods.put("log", new PrototypePropertyDescriptor(new logNativeFunction()));
        methods.put("log1p", new PrototypePropertyDescriptor(new log1pNativeFunction()));
        methods.put("log10", new PrototypePropertyDescriptor(new log10NativeFunction()));
        methods.put("log2", new PrototypePropertyDescriptor(new log2NativeFunction()));
        methods.put("max", new PrototypePropertyDescriptor(new maxNativeFunction()));
        methods.put("min", new PrototypePropertyDescriptor(new minNativeFunction()));
        methods.put("pow", new PrototypePropertyDescriptor(new powNativeFunction()));
        methods.put("random", new PrototypePropertyDescriptor(new randomNativeFunction()));
        methods.put("round", new PrototypePropertyDescriptor(new roundNativeFunction()));
        methods.put("sign", new PrototypePropertyDescriptor(new signNativeFunction()));
        methods.put("sin", new PrototypePropertyDescriptor(new sinNativeFunction()));
        methods.put("sinh", new PrototypePropertyDescriptor(new sinhNativeFunction()));
        methods.put("sqrt", new PrototypePropertyDescriptor(new sqrtNativeFunction()));
        methods.put("tan", new PrototypePropertyDescriptor(new tanNativeFunction()));
        methods.put("tanh", new PrototypePropertyDescriptor(new tanhNativeFunction()));
        methods.put("trunc", new PrototypePropertyDescriptor(new truncNativeFunction()));

        return methods;
    }
}

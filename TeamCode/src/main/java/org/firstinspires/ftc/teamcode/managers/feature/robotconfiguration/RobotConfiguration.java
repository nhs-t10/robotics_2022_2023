package org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration;


import org.firstinspires.ftc.teamcode.auxilary.BasicMapEntry;
import org.firstinspires.ftc.teamcode.auxilary.UpdatableWeakReference;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.feature.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RobotConfiguration {
    public static final ArrayList<RobotConfiguration> configurations = new ArrayList<>();
    public final static String fileName = "configuration";

    public final String name;
    public WheelCoefficients motorCoefficients;
    public final WheelCoefficients autoMotorCoefficients;
    public final WheelCoefficients teleOpMotorCoefficients;

    public final OmniCalcComponents omniComponents;



    /**
     * How many "ticks" quantify a rotation of the motor.
     */
    public final double encoderTicksPerRotation;
    /**
     * The gear ratio of the main drive motors.
     */
    public final double gearRatio;
    /**
     * The diameter of the main drive wheels, in centimeters
     */
    public final double wheelDiameterCm;
    /**
     * A coefficient indicating how much sliding we can expect of the wheels. 1 is perfect traction; 0 is no traction at all.
     */
    public final double slip;
    /**
     * The circumference of the main drive wheels, in centimeters
     */
    public final double wheelCircumference;


    public final float exponentialScalar;
    public final HashMap<String, float[]> pidCoefs;


    public RobotConfiguration(String name, WheelCoefficients teleOpMotorCoefficients, WheelCoefficients autoMotorCoefficients, OmniCalcComponents omniComponents,
                              float pidPCoefficient, double encoderTicksPerRotation, double gearRatio, double wheelDiameterCm,
                              double slip, float exponentialScalar, HashMap<String, float[]> pidCoefs) {
        this.name = name;
        this.motorCoefficients = teleOpMotorCoefficients;
        this.teleOpMotorCoefficients = teleOpMotorCoefficients;
        this.autoMotorCoefficients = autoMotorCoefficients;
        this.omniComponents = omniComponents;
        this.encoderTicksPerRotation = encoderTicksPerRotation;
        this.gearRatio = gearRatio;
        this.wheelDiameterCm = wheelDiameterCm;
        this.slip = slip;
        this.wheelCircumference = Math.PI * wheelDiameterCm;
        this.exponentialScalar = exponentialScalar;
        this.pidCoefs = pidCoefs;

        RobotConfiguration.configurations.add(this);
    }

    public static Map.Entry<String, float[]> PIDC(String n, float... v) {
        return new BasicMapEntry<String, float[]>(n, v);
    }
    public static HashMap<String, float[]> PIDMAP(Map.Entry<String, float[]>... m) {
        HashMap<String, float[]> f = new HashMap<>();
        for(Map.Entry<String, float[]> k : m) f.put(k.getKey(), k.getValue());
        return f;
    }
}
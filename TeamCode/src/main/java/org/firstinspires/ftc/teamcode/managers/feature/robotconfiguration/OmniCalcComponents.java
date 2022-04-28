package org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration;

import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;

import java.util.Map;

public class OmniCalcComponents {
    public WheelCoefficients hor, ver, rot;

    public OmniCalcComponents(WheelCoefficients h, WheelCoefficients v, WheelCoefficients r) {
        //assemble into an array for directive-checking
        WheelCoefficients[] coefs = new WheelCoefficients[] {h, v, r};

        //fail-safe: if non-directive'd coefs are given, then it'll use VHR to enter them by order.
        this.ver = coefs[0];
        this.hor = coefs[1];
        this.rot = coefs[2];

        for(WheelCoefficients c : coefs) {
            if(c.directive == WheelCoefficients.WheelDirective.OMNI_HOR) hor = c;
            if(c.directive == WheelCoefficients.WheelDirective.OMNI_VER) ver = c;
            if(c.directive == WheelCoefficients.WheelDirective.OMNI_ROT) rot = c;
        }
    }
    @SafeVarargs
    public OmniCalcComponents(Map.Entry<WheelCoefficients.WheelDirective, WheelCoefficients>... coefSets) {
        for(Map.Entry<WheelCoefficients.WheelDirective, WheelCoefficients> c : coefSets) {
            if(c.getKey() == WheelCoefficients.WheelDirective.OMNI_HOR) hor = c.getValue();
            if(c.getKey() == WheelCoefficients.WheelDirective.OMNI_VER) ver = c.getValue();
            if(c.getKey() == WheelCoefficients.WheelDirective.OMNI_ROT) rot = c.getValue();
        }
    }
}

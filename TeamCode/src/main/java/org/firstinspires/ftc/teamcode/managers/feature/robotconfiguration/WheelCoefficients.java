package org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration;


import org.firstinspires.ftc.teamcode.auxilary.BasicMapEntry;

public class WheelCoefficients {
    public enum WheelDirective { OMNI_HOR, OMNI_VER, OMNI_ROT, MOTORS };

    public static WheelCoefficients W(float fl, float fr, float bl, float br) {
        return new WheelCoefficients(fl, fr, bl, br);
    }
    public static BasicMapEntry<WheelDirective, WheelCoefficients> horizontal(float fl, float fr, float bl, float br) {
        return new BasicMapEntry<>(WheelDirective.OMNI_HOR, new WheelCoefficients(fl, fr, bl, br));
    }
    public static BasicMapEntry<WheelDirective, WheelCoefficients> rotational(float fl, float fr, float bl, float br) {
        return new BasicMapEntry<>(WheelDirective.OMNI_ROT, new WheelCoefficients(fl, fr, bl, br));
    }
    public static BasicMapEntry<WheelDirective, WheelCoefficients> vertical(float fl, float fr, float bl, float br) {
        return new BasicMapEntry<>(WheelDirective.OMNI_VER, new WheelCoefficients(fl, fr, bl, br));
    }

    public float fl, fr, bl, br;
    public WheelDirective directive;

    public WheelCoefficients(float fl, float fr, float bl, float br) {
        this.fl = fl;
        this.fr = fr;
        this.bl = bl;
        this.br = br;
    }
}
package org.firstinspires.ftc.teamcode.managers.feature;


import org.firstinspires.ftc.teamcode.auxilary.FileSaver;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.OmniCalcComponents;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration;
import org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.WheelCoefficients;

import java.util.ArrayList;

import static org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration.PIDC;
import static org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.RobotConfiguration.PIDMAP;
import static org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.WheelCoefficients.W;
import static org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.WheelCoefficients.horizontal;
import static org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.WheelCoefficients.rotational;
import static org.firstinspires.ftc.teamcode.managers.feature.robotconfiguration.WheelCoefficients.vertical;

public class FeatureManager {
    public static final Logger logger = new Logger();

    public static final int DOUBLE_CLICK_TIME_MS = 300;

    public static boolean isOpModeRunning = false;

    public static void setIsOpModeRunning(boolean b) {
        isOpModeRunning = b;
    }

    public static final RobotConfiguration tankBoyConfiguration = new RobotConfiguration(
            W(1,1,1,1), W(1,1,1,1),
            new OmniCalcComponents(
                    vertical      (-1f,1f,-1f,-1f),
                    horizontal    (0f, 0f, 0f, 0f),
                    rotational    (1f,-1f,-1f,-1f)
            ),
            0.03f, 1680, 1, 8.9, 1, 3f,
            PIDMAP()
    );

    public static final RobotConfiguration speedyBoyConfiguration = new RobotConfiguration(
            W(1,1,0,0), W(1,1,0,0),
            new OmniCalcComponents(
                    vertical      (1f,-1f,1f,1f),
                    horizontal    (-0.5f, -0.5f, -1f, -1f),
                    rotational    (0f,0f,0f,0f)
            ),
            0.03f, 1680, 1, 8.9, 1, 3f,
            PIDMAP()
    );

    public static final RobotConfiguration bigBoyConfiguration = new RobotConfiguration(
            W(1,1,-1,1), W(0.83f,0.83f,-1,1),
            new OmniCalcComponents(
                vertical      (-1f,-1f,-1f,-1f),
                horizontal    (1f, -1f, 1f, -1f),
                rotational    (1f,-1f,-1f,1f)
            ),
            0.03f, 1680, 1, 8.9, 1, 3f,
            PIDMAP(
                PIDC("ClawMotor", 0.05f, 0f, 0f)
            )
        );

    public static final RobotConfiguration giraffeBoyConfiguration = new RobotConfiguration(
            W(1,1,-1,1), W(1,1,-1,1),
            new OmniCalcComponents(
                vertical      (-1f,-1f,-1f,-1f),
                horizontal    (1f, -1f, 1f, -1f),
                rotational    (1f,-1f,-1f,1f)
            ),
            0.03f, 1680, 1, 8.9, 1, 3f,
            PIDMAP(
                    PIDC("NeckMotor", 0.050928f, 1.003e-9f  , 0.05f),
                    PIDC("ClawMotor", 0.057f, 1.243e-9f, 0f)
               //    PIDC("ClawMotor", 0.008718f, 1.6917e-7f, 0.0001f)

            )
    );

    public static void reconfigureForTeleop() {
        FeatureManager.logger.log("I am teleop");
        getRobotConfiguration().motorCoefficients = getRobotConfiguration().teleOpMotorCoefficients;
    }
    public static void reconfigureForAuto() {
        FeatureManager.logger.log("I am auto");
        getRobotConfiguration().motorCoefficients = getRobotConfiguration().autoMotorCoefficients;
    }
    public static final RobotConfiguration littleBoyConfiguration = new RobotConfiguration(
            W(1, -1, 1, -1), W(1,-1,1,-1),
            new OmniCalcComponents(
                vertical      (-1f,-1f,-1f,-1f),
                horizontal    (-1f,1f,1f,-1f),
                rotational    (-1f, 1f, -1f, 1f)
            ),
            0.03f, 1680, 1, 4, 0.7, 3f,
            PIDMAP()
        );

    public static final RobotConfiguration defaultConfiguration = bigBoyConfiguration;


    private static RobotConfiguration cachedConfiguration;

    public static String getRobotName() {
        ArrayList<String> lines = (new FileSaver(RobotConfiguration.fileName)).readLines();

        //if the file doesn't exist, return the default. This doesn't adjust the cache, so if there's a later edit, it'll be loaded.
        if(lines.size() == 0) return "nonexistentBoy";

        String fileContent = lines.get(0);

        switch (fileContent) {
            case RobotConfiguration.bigBoyFileContent:
                return "bigBoy";
            case RobotConfiguration.littleBoyFileContent:
                return "smallBoy";
            case RobotConfiguration.giraffeBoyFileContent:
                return "giraffeBoy";
            case RobotConfiguration.tankBoyFileContent:
                return "tankBoy";
            case RobotConfiguration.SpeedyBoyFileContent:
                return "Speedy";
            default:
                return "mysteryBoy";
        }
    }

    public static RobotConfiguration getRobotConfiguration() {
        //if it's been cached, don't bother re-loading everything. Just return the cache.
        if(cachedConfiguration != null) return cachedConfiguration;

        ArrayList<String> lines = (new FileSaver(RobotConfiguration.fileName)).readLines();

        //if the file doesn't exist, return the default. This doesn't adjust the cache, so if there's a later edit, it'll be loaded.
        if(lines.size() == 0) return cachedConfiguration = defaultConfiguration;

        String fileContent = lines.get(0);

        switch (fileContent) {
            case RobotConfiguration.bigBoyFileContent:
                return cachedConfiguration = bigBoyConfiguration;
            case RobotConfiguration.littleBoyFileContent:
                return cachedConfiguration = littleBoyConfiguration;
            case RobotConfiguration.giraffeBoyFileContent:
                return cachedConfiguration = giraffeBoyConfiguration;
            case RobotConfiguration.SpeedyBoyFileContent:
                return cachedConfiguration = speedyBoyConfiguration;
            default:
                return cachedConfiguration = defaultConfiguration;
        }
    }

    /**
     * Show a warning about an emergency stop to the user. This does <u>not</u> perform any stopping by itself.
     * @param description an unstructured description of what caused the stop. This can be anything, including `null`.
     */
    public static void reportEmergencyStop(String description) {
        FeatureManager.logger.log("EMERGENCY STOP: " + description);
    }
}

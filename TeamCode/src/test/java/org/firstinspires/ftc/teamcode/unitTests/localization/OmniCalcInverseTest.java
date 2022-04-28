package org.firstinspires.ftc.teamcode.unitTests.localization;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.firstinspires.ftc.teamcode.auxilary.PaulMath;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.junit.Test;

public class OmniCalcInverseTest {
    @Test
    public void test() {
        float[][] testcases = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1},

                {1, 0, 1},
                {0, 1, 1},
                {1, 1, 0},

                {-1, 0, 0},
                {0, -1, 0},
                {0, 0, -1},
        };

        for(float[] t : testcases) fliptest(t);

        for(int i = 0; i < 100; i++) {
            //random test
            fliptest(new float[]{(float) (Math.random() - 0.5), (float) (Math.random() - 0.5), (float) (Math.random() - 0.5)});
        }
    }
    public void fliptest(float[] vhr) {
        FeatureManager.logger.log("=======");
        FeatureManager.logger.log(vhr);
        float[] motorPowers = PaulMath.omniCalc(vhr[0], vhr[1], vhr[2]);
        FeatureManager.logger.log(motorPowers);
        float[] inversed = PaulMath.omniCalcInverse(motorPowers[0], motorPowers[1], motorPowers[2], motorPowers[3]);
        FeatureManager.logger.log(inversed);

        assertArrayEquals(PaulMath.normalizeArray(vhr), PaulMath.normalizeArray(inversed), 0.01f);
    }
}

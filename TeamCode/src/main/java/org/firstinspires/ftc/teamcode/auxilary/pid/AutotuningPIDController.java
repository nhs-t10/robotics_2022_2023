package org.firstinspires.ftc.teamcode.auxilary.pid;

import org.firstinspires.ftc.teamcode.auxilary.RobotTime;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.firstinspires.ftc.teamcode.managers.input.InputManager;

public class AutotuningPIDController extends NormalizedPIDController {
    private final float minOutput;
    private final float maxOutput;

    private boolean tuning;

    private float tuningTarget;

    private float lastError = 0;
    private int period = -1;

    private float maxAmp = 0;
    private float minAmp = 0;

    private static int TUNING_PERIODS = 3;
    private long[] periodStartTimes;

    private int lastPeriodStarted = -1;
    private long periodEndFinal;

    public AutotuningPIDController(double minOutput, double maxOutput, float stability) {
        super(0,0,0, stability);
        this.tuning = true;
        this.minOutput = (float)minOutput;
        this.maxOutput = (float)maxOutput;

        periodStartTimes = new long[TUNING_PERIODS];
    }

    public AutotuningPIDController(float stability) {
        this(-1,1, stability);
    }

    @Override
    public float getControl(float current) {
        if(tuning) return autotuneOnCurrentValue(current);
        else return super.getControl(current);
    }

    @Override
    public boolean isStable(float current) {
        if(tuning) return false;
        else return super.isStable(current);
    }

    private float autotuneOnCurrentValue(float current) {
        float error = super.getError(current);

        invalidateTuningIfTargetChanged();

        boolean isStartPeriod = lastError < 0 && error > 0 && needsToBeStarted(period);
        if(isStartPeriod) {
            period++;
            FeatureManager.logger.log(System.currentTimeMillis() + "\t\t\t" + period);
        }

        //only do things if we've gotten to a symmetrical measurement point
        if (period != -1) {
            InputManager.vibrategp2();
            if(period >= TUNING_PERIODS) {
                periodEndFinal = RobotTime.nanoTime();
                tuning = false;
                completeTuningAndPropagateValues();
            } else {
                if(isStartPeriod) periodStartTimes[period] = RobotTime.nanoTime();

                maxAmp = Math.max(maxAmp, error);
                minAmp = Math.min(minAmp, error);
            }
        }

        lastError = error;

        if(error > 0) return minOutput;
        else return maxOutput;
    }

    private void invalidateTuningIfTargetChanged() {
        float target = super.target;
        if(target != tuningTarget) {
            tuningTarget = target;
            period = -1;
        }
    }

    private boolean needsToBeStarted(int period) {
        if(period == -1) return true;

        if(lastPeriodStarted < period) {
            lastPeriodStarted = period;
            return true;
        } else {
            return false;
        }
    }

    private void completeTuningAndPropagateValues() {
        float tu = computeAveragePeriodLength();
        float ku = calculateUltimateGain();

        setZieglerNichols(tu,ku);
    }

    private void setZieglerNichols(float tu, float ku) {
        FeatureManager.logger.log("finished tuning!");
        float kp = 0.3f * ku;
        float ki = 0.50f * ku / tu;
        float kd = 0.11f * ku * tu;

        FeatureManager.logger.log("ku: " + ku);
        FeatureManager.logger.log("tu: " + tu);

        FeatureManager.logger.log("kp: " + kp);
        FeatureManager.logger.log("ki: " + ki);
        FeatureManager.logger.log("kd: " + kd);

        super.kp = kp;
        super.ki = ki;
    }

    private float calculateUltimateGain() {
        float amp = (Math.abs(maxAmp) + Math.abs(minAmp)) / 2;
        float ampCc = (Math.abs(maxOutput) + Math.abs(minOutput)) / 2;
        float fpi = (float) Math.PI;
        return (4 * ampCc) / (fpi * amp);
    }

    private long computeAveragePeriodLength() {
        int m = 0;
        for(int i = 0; i < periodStartTimes.length; i++) {
             if(i + 1 < periodStartTimes.length) m += (periodStartTimes[i + 1] - periodStartTimes[i]);
             else m += periodEndFinal - periodStartTimes[i];
        }
        return m / periodStartTimes.length;
    }
}

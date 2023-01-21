package org.firstinspires.ftc.teamcode.managers.CV;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public abstract class PipelineThatExposesSomeSecondaryAnalysis extends OpenCvPipeline {
    public abstract Mat processFrame(Mat input);

    abstract int getAnalysis();
    abstract double getAnalysisPrecise();
}

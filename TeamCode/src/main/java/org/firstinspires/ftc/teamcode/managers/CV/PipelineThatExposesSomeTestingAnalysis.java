package org.firstinspires.ftc.teamcode.managers.CV;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

public abstract class PipelineThatExposesSomeTestingAnalysis extends OpenCvPipeline {
    public abstract Mat processFrame(Mat input);

    abstract int getAnalysis();
    double getAnalysisPrecise() { return 0; }
}

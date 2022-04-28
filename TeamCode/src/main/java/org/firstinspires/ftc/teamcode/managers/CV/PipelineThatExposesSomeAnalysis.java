package org.firstinspires.ftc.teamcode.managers.CV;

import org.openftc.easyopencv.OpenCvPipeline;

public abstract class PipelineThatExposesSomeAnalysis extends OpenCvPipeline {
    abstract int getAnalysis();
    double getAnalysisPrecise() { return 0; }
}

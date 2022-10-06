package org.firstinspires.ftc.teamcode.managers.CV;

import static org.firstinspires.ftc.teamcode.managers.CV.RegionBasedAveragesPipeline.BLUE;

import org.firstinspires.ftc.teamcode.auxilary.clocktower.Clocktower;
import org.firstinspires.ftc.teamcode.auxilary.clocktower.ClocktowerCodes;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class ColorSensor123 extends PipelineThatExposesSomeAnalysis {



    // Working variables. Because of memory concerns, we're not allowed to make ANY non-primitive variables within the `processFrame` method.

    //Mat is what you see
    Mat YCrCb = new Mat(), greenPixels = new Mat(), hierarchy = new Mat(), Region_Cr = new Mat(), Region_Cb = new Mat();
    Mat Cr = new Mat();
    Mat Cb = new Mat();
    int avg_Cr, avg_Cb;

    void inputToCr(Mat input)
    {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cr, 1);
    }
    void inputToCb(Mat input)
    {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cb, 2);
    }

    static final Scalar color1_min = new Scalar(0, 0, 0); //todo: fix colors
    static final Scalar color1_max = new Scalar(0, 0, 0); //todo: fix colors
    static final Scalar color2_min = new Scalar(0, 0, 0); //todo: fix colors
    static final Scalar color2_max = new Scalar(0, 0, 0); //todo: fix colors
    static final Scalar color3_min = new Scalar(0, 0, 0); //todo: fix colors
    static final Scalar color3_max = new Scalar(0, 0, 0); //todo: fix colors


    static final Point TopLeftAnchorPoint = new Point(109,98); //todo: fix numbers on rectangle
    static final int REGION_WIDTH = 20; //todo: fix numbers on rectangle
    static final int REGION_HEIGHT = 20; //todo: fix numbers on rectangle
    static final Point BottomRightAnchorPoint = new Point(TopLeftAnchorPoint.x + REGION_WIDTH,TopLeftAnchorPoint.y + REGION_HEIGHT);



    @Override
    public void init(Mat firstFrame) {
        inputToCr(firstFrame);
        Region_Cr = Cr.submat(new Rect(TopLeftAnchorPoint, BottomRightAnchorPoint));
        inputToCb(firstFrame);
        Region_Cb = Cb.submat(new Rect(TopLeftAnchorPoint, BottomRightAnchorPoint));
    }

    @Override
    public Mat processFrame(Mat input)
    {
        inputToCr(input);
        inputToCb(input);
        avg_Cr = (int) Core.mean(Region_Cr).val[0];
        avg_Cb = (int) Core.mean(Region_Cb).val[0];

        Imgproc.rectangle(
                input, // Buffer to draw on
                TopLeftAnchorPoint, // First point which defines the rectangle
                BottomRightAnchorPoint, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines


        return input;
    }


    @Override
    int getAnalysis() {
        return 0;
    }
}

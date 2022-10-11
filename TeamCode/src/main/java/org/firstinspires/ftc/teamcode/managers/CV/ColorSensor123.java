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

    static final Scalar color1_min = new Scalar(107, 179, 199); //purple min
    static final Scalar color1_max = new Scalar(94, 144, 231); //purple max
    static final Scalar color2_min = new Scalar(165, 96, 74); //green min
    static final Scalar color2_max = new Scalar(164, 142, 20); //green max
    static final Scalar color3_min = new Scalar(142, 146, 58); //teal min
    static final Scalar color3_max = new Scalar(99, 206, 68); //teal max
    static final Scalar color4_min = new Scalar(155, 62, 168); //orange min (Base color is RGB #E89910)
    static final Scalar color4_max = new Scalar(183, 40, 151); //orange max (Base color is RGB #E89910)


    static final Point TopLeftAnchorPoint = new Point(500,388); //Base Picture is 1280 x 720 when taken on my computer. Should be adjusted for the robot if needed, as current numbers are for that measurement.
    static final int REGION_WIDTH = 55; //1cm
    static final int REGION_HEIGHT = 55; //1cm
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

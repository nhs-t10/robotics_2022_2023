package org.firstinspires.ftc.teamcode.managers.CV;

import static org.firstinspires.ftc.teamcode.managers.CV.RegionBasedAveragesPipeline.BLUE;

import org.firstinspires.ftc.teamcode.managers.imu.ImuManager;
import org.firstinspires.ftc.teamcode.managers.telemetry.TelemetryManager;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ColorSensor123_TEST extends PipelineThatExposesSomeTestingAnalysis {


    // Working variables. Because of memory concerns, we're not allowed to make ANY non-primitive variables within the `processFrame` method.

    //Mat is what you see
    Mat YCrCb = new Mat(), Region_Cr = new Mat(), Region_Cb = new Mat();
    Mat Cr = new Mat();
    Mat Cb = new Mat();
    private int avg_Cr, avg_Cb, color;
    boolean went_through_init = true;
    boolean went_through_process = false;
    public TelemetryManager telemetry;

    void inputToCr(Mat input) {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cr, 1);
    }

    void inputToCb(Mat input) {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cb, 2);
    }

    boolean check_run()
    {
        if(!went_through_process)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    int getCr() {

        return avg_Cr;
    }

    double getCb() {
        double cb_amount = avg_Cb;
        return cb_amount;
    }


    static final Point TopLeftAnchorPoint = new Point(300,318); //Base Picture is 600 x 480 when taken on the robot.
    static final int REGION_WIDTH = 20; //1cm
    static final int REGION_HEIGHT = 20; //1cm
    static final Point BottomRightAnchorPoint = new Point(TopLeftAnchorPoint.x + REGION_WIDTH, TopLeftAnchorPoint.y + REGION_HEIGHT);


    @Override
    public void init(Mat firstFrame) {
        inputToCr(firstFrame);
        Region_Cr = Cr.submat(new Rect(TopLeftAnchorPoint, BottomRightAnchorPoint));
        inputToCb(firstFrame);
        Region_Cb = Cb.submat(new Rect(TopLeftAnchorPoint, BottomRightAnchorPoint));
        went_through_init = true;
    }

    @Override
    public Mat processFrame(Mat input) {
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

        went_through_process = true;
        return input;
    }


    public int getAnalysis() {
        return getCr();
    }

    public double getAnalysisPrecise() {
        return getCb();
    }
}
package org.firstinspires.ftc.teamcode.managers.CV;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;
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
    Mat YCrCb = new Mat(), Region_Cr = new Mat(), Region_Cb = new Mat();
    Mat Cr = new Mat();
    Mat Cb = new Mat();
    int avg_Cr, avg_Cb, color;

    void inputToCr(Mat input) {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cr, 1);
    }

    void inputToCb(Mat input) {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cb, 2);
    }

    void gridDraw(int width, int height, Mat input) {
        final Scalar GREEN = new Scalar(0, 255, 0);
        final Scalar RED = new Scalar(255, 0, 0);
        Scalar COLOR = GREEN;
        Point TopLeftThing = new Point(0, 0);
        Point BottomRightThing = new Point(width, 0);
        for (int currentHeight = 0; currentHeight < height; currentHeight += 20) {
            TopLeftThing.y = currentHeight;
            BottomRightThing.y = currentHeight;
            if (currentHeight % 100 == 0) {
                COLOR = RED;
            } else {
                COLOR = GREEN;
            }

            Imgproc.rectangle(
                    input, // Buffer to draw on
                    TopLeftThing, // First point which defines the rectangle
                    BottomRightThing, // Second point which defines the rectangle
                    COLOR, // The color the rectangle is drawn in
                    1); // Thickness of the rectangle lines

        }
        TopLeftThing.x = 0;
        TopLeftThing.y = 0;
        BottomRightThing.x = 0;
        BottomRightThing.y = height;
        for (int currentWidth = 0; currentWidth < width; currentWidth += 20) {
            TopLeftThing.x = currentWidth;
            BottomRightThing.x = currentWidth;

            if (currentWidth % 100 == 0) {
                COLOR = RED;
            } else {
                COLOR = GREEN;
            }

            Imgproc.rectangle(
                    input, // Buffer to draw on
                    TopLeftThing, // First point which defines the rectangle
                    BottomRightThing, // Second point which defines the rectangle
                    COLOR, // The color the rectangle is drawn in
                    1); // Thickness of the rectangle lines

        }
    }


    //static final Scalar color1_min = new Scalar(107, 179, 199); //purple min
    static final int color1_min_Cr = 170; //pink min cr
    static final int color1_max_Cr = 210; //pink max cr

    static final int color1_min_Cb = 110; //pink min cb
    static final int color1_max_Cb = 150; //pink max cb


    static final int color2_min_Cr = 95; //green min cr
    static final int color2_max_Cr = 125; //green max cr

    static final int color2_min_Cb = 110; //green min cb
    static final int color2_max_Cb = 140; //green max cb


    static final int color3_min_Cr = 70; //teal min cr
    static final int color3_max_Cr = 110; //teal max cr

    static final int color3_min_Cb = 140; //teal min cb
    static final int color3_max_Cb = 180; //teal max cb


    static final Point TopLeftAnchorPoint = new Point(300, 318); //Base Picture is 600 x 480 when taken on the robot.
    static final int REGION_WIDTH = 20; //max width: 600
    static final int REGION_HEIGHT = 20; //max height: 240
    static final Point BottomRightAnchorPoint = new Point(TopLeftAnchorPoint.x + REGION_WIDTH, TopLeftAnchorPoint.y + REGION_HEIGHT);


    @Override
    public void init(Mat firstFrame) {
        inputToCr(firstFrame);
        Region_Cr = Cr.submat(new Rect(TopLeftAnchorPoint, BottomRightAnchorPoint));
        inputToCb(firstFrame);
        Region_Cb = Cb.submat(new Rect(TopLeftAnchorPoint, BottomRightAnchorPoint));
    }

    @Override
    public Mat processFrame(Mat input) {
        inputToCr(input);
        inputToCb(input);
        avg_Cr = (int) Core.mean(Region_Cr).val[0];
        avg_Cb = (int) Core.mean(Region_Cb).val[0];


        gridDraw(1280, 720, input);
        Imgproc.rectangle(
                input, // Buffer to draw on
                TopLeftAnchorPoint, // First point which defines the rectangle
                BottomRightAnchorPoint, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines


        if (color1_min_Cb <= avg_Cb && avg_Cb <= color1_max_Cb && color1_min_Cr <= avg_Cr && avg_Cr <= color1_max_Cr) {
            color = 1;
        } else if (color2_min_Cb <= avg_Cb && avg_Cb <= color2_max_Cb && color2_min_Cr <= avg_Cr && avg_Cr <= color2_max_Cr) {
            color = 2;
        } else if (color3_min_Cb <= avg_Cb && avg_Cb <= color3_max_Cb && color3_min_Cr <= avg_Cr && avg_Cr <= color3_max_Cr) {
            color = 3;
        } else {
            color = 0;
        }


        return input;
    }


    @Override
    int getAnalysis() {
        return color;
    }

    double getAnalysisPrecise() {
        return color;
    }
}
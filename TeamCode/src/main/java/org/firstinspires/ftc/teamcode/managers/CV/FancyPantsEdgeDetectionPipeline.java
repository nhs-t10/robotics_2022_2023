package org.firstinspires.ftc.teamcode.managers.CV;

import org.firstinspires.ftc.teamcode.auxilary.clocktower.Clocktower;
import org.firstinspires.ftc.teamcode.auxilary.clocktower.ClocktowerCodes;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
public class FancyPantsEdgeDetectionPipeline extends PipelineThatExposesSomeAnalysis {


    /*r
     * An enum to define the skystone position
     */
    public enum SkystonePosition
    {
        LEFT,
        CENTER,
        RIGHT
    }

    /*
     * Some color constants
     */
    static final Scalar YCRCB_MIN = new Scalar(0, 95, 190);
    static final Scalar YCRCB_MAX = new Scalar(125, 105, 250);


    // Working variables. Because of memory concerns, we're not allowed to make ANY non-primitive variables within the `processFrame` method.

    //Mat is what you see
    Mat YCrCb = new Mat(), redPixels = new Mat(), hierarchy = new Mat();
    MatOfPoint biggestContour;

    ArrayList<MatOfPoint> contours;

    Rect biggestContourBoundingRect;

    int largeBlobCenterX, inputWidth;

    // Volatile since accessed by OpMode thread w/o synchronization
    private volatile SkystonePosition position = SkystonePosition.LEFT;

    @Override
    public void init(Mat firstFrame) {

    }

    @Override
    public Mat processFrame(Mat input)
    {
        //report how long CV processing is taking
        Clocktower.time(ClocktowerCodes.CV_UPDATE_LOOP);
        //convert the input to YCrCb, which is better for analysis than the default bgr.
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_BGR2YCrCb);

        //filter the image to ONLY redish pixels
        Core.inRange(YCrCb, YCRCB_MIN, YCRCB_MAX, redPixels);

        //Find the biggest blob of reddish pixels.
        //It likes using a list of Matrices of points instead of something more simple, but that's ok.
        contours = new ArrayList<>();
        Imgproc.findContours(redPixels, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        //FeatureManager.logger.log("Contour Count: " + contours.size());
        //only bother continuing if there were any contours found
        if(contours.size() == 0) return input;

        double biggestArea = -1;
        //look through the list and find the biggest contour
        for(MatOfPoint contour : contours) {
            double area = Imgproc.boundingRect(contour).area();
            if(area > biggestArea) {
                biggestArea = area;
                biggestContour = contour;
            }
        }

        biggestContourBoundingRect = Imgproc.boundingRect(biggestContour);

        Imgproc.rectangle(
                input, // Buffer to draw on
                new Point(biggestContourBoundingRect.x, biggestContourBoundingRect.y), // First point which defines the rectangle
                new Point(biggestContourBoundingRect.x + biggestContourBoundingRect.width,
                        biggestContourBoundingRect.y + biggestContourBoundingRect.height), // Second point which defines the rectangle
                new Scalar(0.5, 255, 0), // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        largeBlobCenterX = biggestContourBoundingRect.x + (biggestContourBoundingRect.width / 2);
        inputWidth = input.width();

        //depending on which third the blob's center falls into, report the result position.
        if(largeBlobCenterX < inputWidth / 3) {
            position = SkystonePosition.LEFT;
        } else if(largeBlobCenterX < (inputWidth * 2) / 3) {
            position = SkystonePosition.CENTER;
        } else {
            position = SkystonePosition.RIGHT;
        }

        return input;
    }
    public int getAnalysis() {
        return position.ordinal();
    }
    public double getAnalysisPrecise() {
        //avoid divide-by-0 error
        if(inputWidth == 0) return 0;

        return largeBlobCenterX / (double)inputWidth;
    }
}

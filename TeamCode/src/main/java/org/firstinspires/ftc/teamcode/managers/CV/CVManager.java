/*
 * Copyright (c) 2019 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.managers.CV;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.managers.feature.FeatureManager;
import org.jetbrains.annotations.Nullable;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;
import org.openftc.easyopencv.OpenCvWebcam;

public class CVManager extends FeatureManager {
    public OpenCvWebcam webcam;
    PipelineThatExposesSomeAnalysis pipeline;
    PipelineThatExposesSomeSecondaryAnalysis pipeline_Secondary;
    PipelineThatExposesSomeTestingAnalysis pipeline_Testing;
    private int active_pipeline = 0;

    public CVManager(HardwareMap hardwareMap) {
        //only initialize the webcam if we're NOT unit-testing.
        //Trying to test cv on a laptop doesn't work :'(
        if (hardwareMap.appContext != null) {
            this.pipeline = new ColorSensor123();
            this.pipeline_Secondary = new MinesweeperPipeline_Secondary();
            this.pipeline_Testing = new ColorSensor123_TEST();
            /*
             * Instantiate an OpenCvCamera object for the camera we'll be using.
             * In this sample, we're using a webcam. Note that you will need to
             * make sure you have added the webcam to your configuration file and
             * adjusted the name here to match what you named it in said config file.
             *
             * We pass it the view that we wish to use for camera monitor (on
             * the RC phone). If no camera monitor is desired, use the alternate
             * single-parameter constructor instead (commented out below)
             */
            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Eyer"), cameraMonitorViewId);

            // OR...  Do Not Activate the Camera Monitor View
            //webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Eyer"));

            /*
             * Specify the image processing pipeline we wish to invoke upon receipt
             * of a frame from the camera. Note that switching pipelines on-the-fly
             * (while a streaming session is in flight) *IS* supported.
             */
                webcam.setPipeline(pipeline);
                active_pipeline = 0;

            /*
             * Open the connection to the camera device. New in v1.4.0 is the ability
             * to open the camera asynchronously, and this is now the recommended way
             * to do it. The benefits of opening async include faster init time, and
             * better behavior when pressing stop during init (i.e. less of a chance
             * of tripping the stuck watchdog)
             *
             * If you really want to open synchronously, the old method is still available.
             */
            webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
            webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    /*
                     * Tell the webcam to start streaming images to us! Note that you must make sure
                     * the resolution you specify is supported by tpubliche camera. If it is not, an exception
                     * will be thrown.
                     *
                     * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                     * supports streaming from the webcam in the uncompressed YUV image format. This means
                     * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                     * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                     *
                     * Also, we specify the rotation that the webcam is used in. This is so that the image
                     * from the camera sensor can be rotated such that it is always displayed with the image upright.
                     * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                     * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                     * away from the user.
                     */
                    webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);
                }

                @Override
                public void onError(int errorCode) {
                    /*
                     * This will be called if the camera could not be opened
                     */
                }
            });
        }
    }
    //This is identical in every way, except allows more flexibilty in which pipeline you use
    public CVManager(HardwareMap hardwareMap, int pipeline_index) {
        if (hardwareMap.appContext != null) {
            this.pipeline = new ColorSensor123();
            this.pipeline_Secondary = new MinesweeperPipeline_Secondary();
            this.pipeline_Testing = new ColorSensor123_TEST();
            int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
            webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Eyer"), cameraMonitorViewId);
            if (pipeline_index == 0) {
                webcam.setPipeline(pipeline);
            } else if (pipeline_index == 1) {
                webcam.setPipeline(pipeline_Secondary);
            } else if (pipeline_index == 2) {
                webcam.setPipeline(pipeline_Testing);
            }
            else {
                webcam.setPipeline(pipeline);
            }
            active_pipeline = pipeline_index;
            webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
            webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() { webcam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT); }
                @Override
                public void onError(int errorCode) {}
            });
        }
    }

    public int getCVPositionNumberWhereZeroIsLeftOneIsMiddleAndTwoIsRight() {
        if (pipeline == null) return 0;
        else return pipeline.getAnalysis();
    }

    public double getCVPrecisePosition() {
        if (pipeline == null) return 0;
        else return pipeline.getAnalysisPrecise();
    }

    public double getCVPositionNumberWhereZeroIsLeftOneIsHalfLeftAndTwoIsMiddle() {
        return Math.floor(Math.min(0.9, pipeline.getAnalysisPrecise() * 2.0) * 3);
    }

    public int getCVNumberForTesting() {
        return 0;
    }


    public String getColor() {
        int color = pipeline.getAnalysis();
        if (color == 1) {
            return "Pink";
        } else if (color == 2) {
            return "Green";
        } else if (color == 3) {
            return "Blue";
        } else {
            return "Error";
        }
    }

    public int getColorRaw() {
        int color = pipeline_Testing.getAnalysis();
        return color;
    }

    public int getCameraId()
    {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createInternalCamera(OpenCvInternalCamera.CameraDirection.BACK, cameraMonitorViewId);

        return cameraMonitorViewId;
    }

    public int getAnalysis()
    {
        return pipeline.getAnalysis();
    }

    public double getAnalysisPrecise() {return pipeline_Secondary.getAnalysisPrecise();}

    public int getAnalysisSecondary() {return pipeline_Secondary.getAnalysis();}

    public double getAnalysisPreciseSecondary() {return pipeline_Secondary.getAnalysisPrecise();}

    public int getAnalysisTest() {return pipeline_Testing.getAnalysis();}

    public double getAnalysisPreciseTest() {return pipeline_Testing.getAnalysisPrecise();}


    /**
     * gridDraw can only be used inside a pipeline, but it's so useful I had to put it here. To use it, copy it into the pipeline of your choice <br>
     * It makes a green/red grid on your image. As written here, the red lines will appear every 20 pixels. <br>
     * I recommend using it in conjunction with a blue viewbox to calibrate the CV detection window
     * @param width - the horizontal resolution of your image in pixels
     * @param height - the vertical resolution of your image in pixels
     * @param input - the image the grid is drawn on
     */
    void gridDraw(int width, int height, Mat input) {
        final Scalar GREEN = new Scalar(0, 255, 0);
        final Scalar RED = new Scalar(255,0,0);
        Scalar COLOR = GREEN;
        Point TopLeftThing = new Point(0,0);
        Point BottomRightThing = new Point(width,0);
        for (int currentHeight = 0; currentHeight < height; currentHeight += 20)
        {
            TopLeftThing.y = currentHeight;
            BottomRightThing.y = currentHeight;
            if (currentHeight % 100 == 0)
            {
                COLOR = RED;
            }
            else
            {
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
        for (int currentWidth = 0; currentWidth < width; currentWidth += 20)
        {
            TopLeftThing.x = currentWidth;
            BottomRightThing.x = currentWidth;

            if (currentWidth % 100 == 0)
            {
                COLOR = RED;
            }
            else
            {
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


    public void stopWebcam() {
        /*
         * IMPORTANT NOTE: calling stopStreaming() will indeed stop the stream of images
         * from the camera (and, by extension, stop calling your vision pipeline). HOWEVER,
         * if the reason you wish to stop the stream early is to switch use of the camera
         * over to, say, Vuforia or TFOD, you will also need to call closeCameraDevice()
         * (commented out below), because according to the Android Camera API documentation:
         *         "Your application should only have one Camera object active at a time for
         *          a particular hardware camera."
         *
         * NB: calling closeCameraDevice() will internally call stopStreaming() if applicable,
         * but it doesn't hurt to call it anyway, if for no other reason than clarity.
         *
         * NB2: if you are stopping the camera stream to simply save some processing power
         * (or battery power) for a short while when you do not need your vision pipeline,
         * it is recommended to NOT call closeCameraDevice() as you will then need to re-open
         * it the next time you wish to activate your vision pipeline, which can take a bit of
         * time. Of course, this comment is irrelevant in light of the use case described in
         * the above "important note".
         */
        webcam.stopStreaming();
        //webcam.closeCameraDevice();

        /*
         * For the purposes of this sample, throttle ourselves to 10Hz loop to avoid burning
         * excess CPU cycles for no reason. (By default, telemetry is only sent to the DS at 4Hz
         * anyway). Of course in a real OpMode you will likely not want to do this.
         */
//            sleep(100);
    }
}

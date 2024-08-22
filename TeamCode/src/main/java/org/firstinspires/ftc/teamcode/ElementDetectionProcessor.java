package org.firstinspires.ftc.teamcode;

import android.graphics.Canvas;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/*
  A pipeline to return the custom sleeve's indicated position
 */
public class ElementDetectionProcessor implements VisionProcessor {

    // RGB values for red and blue
    public static double RedR = 255;
    public static double RedG = 0;
    public static double RedB = 0;
    public static double BlueR = 0;
    public static double BlueG = 0;
    public static double BlueB = 255;

    // Regions of interest (ROI) for sleeve detection
    // for 1280 x 720
    // moving to 1920 x 1080
    // moving to 800 x 600
    private static final Rect LEFT_NEAR_ROI = new Rect(200, 375, 30, 30);
    private static final Rect LEFT_CENTER_ROI = new Rect(494, 357, 30, 30);
    private static final Rect RIGHT_NEAR_ROI = new Rect(594, 333, 30, 30);
    private static final Rect RIGHT_CENTER_ROI = new Rect(275, 349, 30, 30);
    private static final Rect MIDDLE_NEAR_ROI = new Rect(720, 333, 30, 30);
    private static final Rect MIDDLE_CENTER_ROI = new Rect(360, 340, 30, 30);


    // Scalar for white color
    private static final Scalar WHITE = new Scalar(255, 255, 255);

    // Thresholds for color detection
    private final double redThresholdMain = 90;
    private final double redThresholdSecondary = 70;
    private final double blueThresholdMain = 90;
    private final double blueThresholdSecondary = 70;

    // Boolean flags for robot position and detection
    private final boolean onBlueSide;
    private int intElementPosition;
    private boolean senseLeft;
    private boolean senseCenter;

    /**
     * Constructor for the pipeline to detect the prop's position
     */
    public ElementDetectionProcessor() {
        super();
        // Set robot position and detection flags
        onBlueSide = true;
        senseLeft = false;
        senseCenter = false;
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        // Initialization method, not used in this implementation
    }

    @Override
    public Mat processFrame(Mat input, long longInt) {
        Rect nearRoi = MIDDLE_NEAR_ROI;
        Rect centerRoi = MIDDLE_CENTER_ROI;
        int unseenSpot = 1;
        int seenSpot = 3;

        int colorIndexMain = onBlueSide ? 2 : 0;
        int colorIndexSecondary = onBlueSide ? 0 : 2;

        // Get the submat frame and calculate average color values
        Mat areaMatLeft = input.submat(nearRoi);
        Scalar sumColorsLeft = Core.sumElems(areaMatLeft);

        Mat areaMatCenter = input.submat(centerRoi);
        Scalar sumColorsCenter = Core.sumElems(areaMatCenter);

        double avgNearMain = sumColorsLeft.val[colorIndexMain] / (nearRoi.area());
        RobotLog.vv("tt-prop", "average near main %.2f", avgNearMain);
        double avgCenterMain = sumColorsCenter.val[colorIndexMain] / (nearRoi.area());
        RobotLog.vv("tt-prop", "average center main %.2f", avgCenterMain);

        double avgNearSecondary = sumColorsLeft.val[colorIndexSecondary] / (nearRoi.area());
        RobotLog.vv("tt-prop", "average near secondary %.2f", avgNearSecondary);
        double avgCenterSecondary = sumColorsCenter.val[colorIndexSecondary] / (nearRoi.area());
        RobotLog.vv("tt-prop", "average center secondary %.2f", avgCenterSecondary);
        Scalar targetColor = onBlueSide ? new Scalar(BlueR, BlueG, BlueB) : new Scalar(RedR, RedG, RedB);
        boolean aboveThresholdNear =
                (onBlueSide ? (avgNearMain > blueThresholdMain && avgNearSecondary < redThresholdSecondary) :
                        (avgNearMain > redThresholdMain && avgNearSecondary < blueThresholdSecondary))  ||
                                (avgNearMain - avgNearSecondary > 80);
        boolean aboveThresholdCenter =
                (onBlueSide ? (avgCenterMain > blueThresholdMain && avgCenterSecondary < redThresholdSecondary) :
                        (avgCenterMain > redThresholdMain && avgCenterSecondary < blueThresholdSecondary))
                        || (avgCenterMain - avgCenterSecondary > 80);

        // Change the bounding box color based on the sleeve color
        Imgproc.rectangle(
                input,
                nearRoi,
                aboveThresholdNear ? targetColor : WHITE,
                2
        );

        Imgproc.rectangle(
                input,
                centerRoi,
                aboveThresholdCenter ? targetColor : WHITE,
                2
        );


        if(aboveThresholdNear){
            intElementPosition = seenSpot;
            senseLeft = true;
        } else if(aboveThresholdCenter){
            intElementPosition = 2;
            senseCenter = true;
        } else {
            senseLeft = false;
            senseCenter = false;
        }

        if (!senseCenter && !senseLeft) {
            intElementPosition = unseenSpot;
        }

        // Release Mat objects and return input
        areaMatLeft.release();
        areaMatCenter.release();
        return input;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
        // Method for drawing frames, not used in this implementation
    }

    /**
     * Method which returns the prop position
     *
     * @return the prop position
     */
    public int getPropPosition(){
        return intElementPosition;
    }
}
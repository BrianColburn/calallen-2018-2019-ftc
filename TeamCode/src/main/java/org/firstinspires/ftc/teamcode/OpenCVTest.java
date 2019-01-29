package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "OpenCV Test", group = "Test")
public class OpenCVTest extends OpMode {
    private GoldAlignDetector detector;

    @Override
    public void init() {
        detector = new GoldAlignDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        detector.useDefaults();

        // Optional Tuning
        detector.alignSize = 100; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        detector.alignPosOffset = 100; // How far from center frame to offset this alignment zone.
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005;

        detector.ratioScorer.weight = 5;
        detector.ratioScorer.perfectRatio = 1.0;

        detector.enable();
    }

    void updateInfo() {
        if (detector.isFound()) {
            telemetry.addData("IsAligned", detector.getAligned()); // Is the bot aligned with the gold mineral
            telemetry.addData("X Pos", detector.getXPosition()); // Gold X pos.
            telemetry.addData("Y Pos", detector.getYPosition());
            telemetry.addData("Dimensions", "%d, %d", detector.getRect().width, detector.getRect().height);
            telemetry.addData("Distance", "%.2f, %.2f, %.2f", detector.getDistances()[0], detector.getDistances()[1], detector.getDistances()[2]);
            telemetry.addData("Variance", "%.2f, %.2f", detector.getDistances()[2]-detector.getDistances()[0], (detector.getDistances()[2] + detector.getDistances()[0])/2);
        }
    }

    @Override
    public void init_loop() {
        updateInfo();

        int margin = 1;
        int target = 30;

        if (detector.isFound()) {
            if (detector.getAligned() != 0) {
                telemetry.addData("Action", "turn " + detector.getAligned());
            } else if ((detector.getDistances()[1]+margin)/target > 1 || (detector.getDistances()[1]-margin)/target > 1) {
                telemetry.addData("Action","Forward!");
            } else if ((detector.getDistances()[1]+margin)/target < 1 || (detector.getDistances()[1]-margin)/target < 1) {
                telemetry.addData("Action", "Back!");
            } else {
                telemetry.addData("Action", "This shouldn't be happening");
            }
        } else {
            telemetry.addData("Action", "!!!!");
        }
    }

    @Override
    public void loop() {}
}

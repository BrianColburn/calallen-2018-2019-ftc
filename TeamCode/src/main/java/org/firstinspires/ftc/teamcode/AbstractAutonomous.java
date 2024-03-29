package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractAutonomous extends OpMode {
    public ElapsedTime runtime = new ElapsedTime();
    public Logger logger = Logger.getLogger("AALogger");

    //region Gold Detector and Motor variables
    public GoldAlignDetector detector;
    // looking for a 5cm X 5cm X 5cm gold cube
    // ~170px, 100px at 21 cm
    // 85px by 115px at 30 cm
    public DcMotor[] mot = new DcMotor[5];
    public Servo[] ser = new Servo[2];
    public WheelManager wm;
    public boolean direction = false;
    public double angleOffset = 0;
    //endregion

    //region State stuff
    //region State variables
    public enum State {
        OFF,       // Robot is off
        HANGING,   // We need to deploy
        TOKEN,     // Drop off the token
        DEPOT,     // In the crater
        TRANSIENT, // En route
        CUBE,      // Looking for a cube
        CRATER     // In the crater
    }
    public LinkedList<State> stateHistory = new LinkedList<>();
    public State state;
    public ElapsedTime stateTime = new ElapsedTime();
    public long stateIterations;
    public State postHang;
    //endregion

    public void changeState(State s) {
        logger.info(String.format("Changing from state %s to state %s, history: %s", state, s, stateHistory));
        state = s;
        stateHistory.push(state);
        stateIterations = 0;
        stateTime.reset();
    }
    //endregion

    public Telemetry.Item logAndAddData(Level logLevel, String caption, String format, Object... args) {
        logger.log(logLevel, String.format(caption +": "+ format, args));
        return telemetry.addData(caption, format, args);
    }

    public void updateInfo() {
        logAndAddData(Level.INFO, "Runtime", "%.2f", runtime.seconds());
        double[] pos = wm.getPolPos();
        logAndAddData(Level.INFO, "Position","%.2f, %.2f", pos[0], pos[1]*1800/Math.PI);
        logAndAddData(Level.INFO, "CM    ", "(%f)/_(%f)", wm.getCM(),wm.getDegrees());
        logAndAddData(Level.INFO, "Inches", "(%f)/_(%f)", wm.getInches(),wm.getDegrees());
        logAndAddData(Level.INFO, "Encoders", "FL: %d, BR: %d, HK: %d", mot[1].getCurrentPosition(), mot[3].getCurrentPosition(), mot[4].getCurrentPosition());
        logAndAddData(Level.INFO, "WM","%s",wm);
        if (detector.isFound()) {
            logAndAddData(Level.INFO, "IsAligned", "%d", detector.getAligned()); // Is the bot aligned with the gold mineral
            logAndAddData(Level.INFO, "X Pos", "%.2f", detector.getXPosition()); // Gold X pos.
            logAndAddData(Level.INFO, "Y Pos", "%.2f", detector.getYPosition());
            logAndAddData(Level.INFO, "Dimensions", "%d, %d", detector.getRect().width, detector.getRect().height);
            logAndAddData(Level.INFO, "Distance", "%.2f, %.2f, %.2f", detector.getDistances()[0], detector.getDistances()[1], detector.getDistances()[2]);
            logAndAddData(Level.INFO, "Variance", "%.2f, %.2f", detector.getDistances()[2]-detector.getDistances()[0], (detector.getDistances()[2] + detector.getDistances()[0])/2);
        }
    }

    @Override
    public void init() {
        try {
            logger.addHandler(new FileHandler("/storage/emulated/0/AALog%u.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            telemetry.addData("UNABLE TO CREATE FILE HANDLER",e.getMessage());
        }

        //region Initialize State Machine
        stateHistory.push(State.OFF);
        changeState(State.OFF);
        //endregion


        //region Initialize Gold Detector
        detector = new GoldAlignDetector();
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance());
        detector.useDefaults();

        // Optional Tuning
        detector.alignSize = 100; // How wide (in pixels) is the range in which the gold object will be aligned. (Represented by green bars in the preview)
        detector.alignPosOffset = 0; // How far from center frame to offset this alignment zone.
        detector.downscale = 0.4; // How much to downscale the input frames

        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA; // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.005;

        detector.ratioScorer.weight = 5;
        detector.ratioScorer.perfectRatio = 1.0;

        detector.enable();
        //endregion


        //region Initialize motors and servos
        for (int i=0;i<mot.length;i++) {
            try {
                mot[i] = hardwareMap.get(DcMotor.class, "mot" + i);
                mot[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                mot[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } catch (IllegalArgumentException e){
                telemetry.addData("mot"+i+" is null","");
            }
        }
        mot[0].setDirection(DcMotor.Direction.REVERSE);
        mot[3].setDirection(DcMotor.Direction.REVERSE);

        ser[0] = hardwareMap.get(Servo.class, "ser0");
        ser[1] = hardwareMap.get(Servo.class, "ser1");
        ser[0].setPosition(0);
        ser[1].setPosition(1);
        //endregion
    }

    @Override
    public void init_loop() {
        updateInfo();
    }

    @Override
    public void start() {
        changeState(State.HANGING);
        runtime.reset();
    }
}

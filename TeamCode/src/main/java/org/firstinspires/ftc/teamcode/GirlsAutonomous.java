package org.firstinspires.ftc.teamcode;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.LinkedList;


@Autonomous(name="GirlsAutonmous", group ="Test")
public class GirlsAutonomous extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();

    //region Gold Detector and Motor variables
    private GoldAlignDetector detector;
    private DcMotor[] mot = new DcMotor[5];
    private Servo servo;
    //endregion

    private boolean direction = false;
    private boolean attack = false;


    //region State variables
    private enum State {
        HANGING,   // We need to deploy
        TOKEN,     // Drop off the token
        DEPOT,     // Token has been dropped off
        TRANSIENT, // En route to the crater
        CUBE,      // Looking for a cube
        CRATER     // In the crater
    }
    private LinkedList<State> stateHistory = new LinkedList<>();
    private State state;
    private ElapsedTime stateTime = new ElapsedTime();
    //endregion

    private WheelManager wm;


    @Override
    public void init() {
        telemetry.addData("Status", "DogeCV 2018.0 - Girls Autonomous");


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
            } catch (IllegalArgumentException e){
                telemetry.addData("mot"+i+" is null","");
            }
        }
        mot[0].setDirection(DcMotor.Direction.REVERSE);
        mot[3].setDirection(DcMotor.Direction.REVERSE);

        mot[4] = hardwareMap.get(DcMotor.class, "mot4");

        servo = hardwareMap.get(Servo.class, "ser0");
        servo.setPosition(0);

        wm = new WheelManager(mot, 10/2, (2*Math.PI)/2.5, 37.5, .2);
        //endregion
    }

    void changeState(State s) {
        state = s;
        stateHistory.push(state);
        stateTime.reset();
    }

    void updateInfo() {
        telemetry.addData("Position", "(%.2f, %.2f)", wm.getPolPos()[0], wm.getPolPos()[1]*1800/Math.PI);
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
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
        changeState(State.HANGING);
    }


    @Override
    public void loop() {
        updateInfo();
        switch (state) {
            //region State: Hanging
            case HANGING: {
                break;
            }
            //endregion
            //region State: Token
            case TOKEN: {
                if (wm.getPolPos()[0] > 170)
                { // Stop moving and change states
                    changeState(State.DEPOT);
                    wm.setPower(0,0);
                } else if (wm.getPolPos()[0] > 150)
                { // Drop the token
                    servo.setPosition(1);
                } else { // Keep moving forward
                    break;
                }
                break;
            }
            //endregion
            //region State: Depot
            case DEPOT: {
                // We need to rotate to face the crater
                if (wm.getDegrees() <= 120) {
                    wm.setPower(-1,1);
                }
                // We are facing the crater
                else if (wm.getDegrees() > 120){
                    changeState(State.TRANSIENT);
                }
                break;
            }
            //endregion
            //region State: Transient
            case TRANSIENT: {
                wm.setPower(1,1);
                break;
            }
            //endregion
            //region State: Cube
            case CUBE: {
                if (detector.isFound()) { // We found a cube (give or take)
                    int ix = 2;
                    int margin = 1;
                    int target = 30;

                    if (detector.getAligned() != 0)
                    { // We need to line up our approach
                        telemetry.addData("Action", "turn " + detector.getAligned());
                        wm.setPower(detector.getAligned()*.125, -detector.getAligned()*.125);
                    } else if (
                                ((detector.getDistances()[ix] + margin) / target > 1 &&
                                    (detector.getDistances()[ix] - margin) / target > 1) ||
                                attack
                            )
                    { // The cube is at least `target' cm away
                        // Or we are charging at it
                        telemetry.addData("Action","Towards the Cube!");
                        wm.setPower(-.125,-.125); // Girl's camera is on the back, so we backup.
                        attack = true;
                    } else if (
                            ((detector.getDistances()[ix] + margin) / target < 1 &&
                                    (detector.getDistances()[ix] - margin) / target < 1) &&
                                    !attack
                            )
                    { // The cube is closer than `target' cm
                        // And we are not charging at it
                        telemetry.addData("Action", "Away!");
                        wm.setPower(.125,.125);
                    } else { // Boys, we gottem.
                        wm.setPower(0,0);
                        telemetry.addData("Action", "CHARGE!");
                        servo.setPosition(180 / 180.);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wm.setPower(-.4,-.4);

                        //while (detector.isFound());
                        try {
                            Thread.sleep(1200);
                            servo.setPosition(0);
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // Lost the cube
                    if (attack) { // Probably lost the cube because we ran over it
                        telemetry.addData("Action", "Keep Calm, and CHARGE!!!");
                    } else { // Lost the cube for any other reason
                        telemetry.addData("Action", "Panic");
                        if (wm.getPolPos()[1]*1800/Math.PI > 20 && !direction) {
                            wm.setPower(.125,-.125);
                        } else {
                            direction = true;
                            wm.setPower(-.125,.125);
                        }
                    }
                }
                break;
            }
            //endregion
            //region State: Crater
            case CRATER: {
                break;
            }
            //endregion
            //region State: !!!!
            default: {
                throw new IllegalStateException("Illegal state: " + state);
            }
            //endregion
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        wm.setPower(0,0);
        servo.setPosition((15)/180);
        detector.disable();
    }

}

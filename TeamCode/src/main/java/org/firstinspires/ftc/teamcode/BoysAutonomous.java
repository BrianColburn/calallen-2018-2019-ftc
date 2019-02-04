/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.DogeCV;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.LinkedList;


@Autonomous(name="Boys Autonomous", group="DogeCV")

public class BoysAutonomous extends OpMode
{
    private ElapsedTime runtime = new ElapsedTime();

    //region Gold Detector and Motor variables
    private GoldAlignDetector detector;
    // looking for a 5cm X 5cm X 5cm gold cube
    // ~170px, 100px at 21 cm
    // 85px by 115px at 30 cm
    private DcMotor[] mot = new DcMotor[5];
    private Servo servo;
    private WheelManager wm;
    private boolean direction = false;
    //endregion


    //region State variables
    private enum State {
        OFF,       // Robot is off
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
    private long stateIterations;
    //endregion

    @Override
    public void init() {
        telemetry.addData("Status", "DogeCV 2018.0 - Gold Align Example");
        stateHistory.push(State.OFF);
        changeState(State.OFF);

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

        for (int i=0;i<mot.length;i++) {
            try {
                mot[i] = hardwareMap.get(DcMotor.class, "mot" + i);
            } catch (IllegalArgumentException e){
                telemetry.addData("mot"+i+" is null","");
            }
        }
        mot[0].setDirection(DcMotor.Direction.REVERSE);
        mot[3].setDirection(DcMotor.Direction.REVERSE);

        servo = hardwareMap.get(Servo.class, "ser0");
        //servo.scaleRange(0,.8);
        servo.setPosition(180/180.);

        wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1);
    }

    void changeState(State s) {
        state = s;
        stateHistory.push(state);
        stateIterations = 0;
        stateTime.reset();
    }

    void updateInfo() {
        telemetry.addData("Runtime", "%.2f", runtime.seconds());
        telemetry.addData("Position","%.2f, %.2f", wm.getPolPos()[0], wm.getPolPos()[1]);
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
        changeState(State.HANGING);
        runtime.reset();
    }


    @Override
    public void loop() {
        updateInfo();
        switch (state) {
            //region State: Hanging
            case HANGING: {
                if (runtime.seconds() <= 10) {
                    mot[4].setPower(-1);
                } else if (getRuntime() > 11) {
                    changeState(State.CUBE);
                    mot[4].setPower(0);
                }
                break;
            }
            //endregion
            //region State: Token
            case TOKEN: {
                if (wm.getPolPos()[0] > 40)
                { // Stop moving and change states
                    changeState(State.DEPOT);
                    wm.setPower(0,0);
                } else if (wm.getPolPos()[0] > 35)
                { // Drop the token
                    servo.setPosition(1);
                } else { // Keep moving forward
                    wm.setPower(.4,.4);
                    break;
                }
                break;
            }
            //endregion
            //region State: Depot
            case DEPOT: {
                break;
            }
            //endregion
            //region State: Transient
            case TRANSIENT: {
                switch (stateHistory.get(1)) {
                    case DEPOT: {
                        if (wm.getPolPos()[0] < 350) {
                            wm.setPower(1, 1);
                        } else {
                            wm.setPower(0,0);
                            changeState(State.CRATER);
                        }
                        break;
                    }
                    case CRATER: {
                        // head to the depot
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                break;
            }
            //endregion
            //region State: Cube
            case CUBE: {
                if (detector.isFound()) {
                    if (detector.getAligned() != 0) {
                        wm.setPower(detector.getAligned() * .125, -detector.getAligned() * .125);
                    /*for (DcMotor m : mot) {
                        m.setPower(detector.getAligned() * .125);
                    }*/
                    } else {
                        int ix = 2;
                        int margin = 1;
                        int target = 30;
                        if ((detector.getDistances()[ix] + margin) / target > 1 && (detector.getDistances()[ix] - margin) / target > 1) {
                            wm.setPower(.125, .125);
                        /*mot[0].setPower(-.125);
                        mot[3].setPower(-.125);
                        mot[1].setPower(.125);
                        mot[2].setPower(.125);*/
                            telemetry.addLine("Forward!");
                        } else if ((detector.getDistances()[ix] + margin) / target < 1 && (detector.getDistances()[ix] - margin) / target < 1) {
                            wm.setPower(-.125, -.125);
                        /*mot[0].setPower(.125);
                        mot[3].setPower(.125);
                        mot[1].setPower(-.125);
                        mot[2].setPower(-.125);*/
                            telemetry.addLine("Back!");
                        } else {
                            wm.setPower(0, 0);
                        /*for (DcMotor m : mot) {
                            m.setPower(0);
                        }*/
                            telemetry.addLine("Stop!");
                            servo.setPosition(0 / 180.);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wm.setPower(.4, .4);
                            /*mot[0].setPower(-.4);//125);
                            mot[3].setPower(-.4);//125);
                            mot[1].setPower(.4);//125);
                            mot[2].setPower(.4);//125);*/

                            //while (detector.isFound());
                            try {
                                Thread.sleep(1200);
                                servo.setPosition(1);
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            changeState(State.CRATER);
                        }
                    }
                } else if (runtime.seconds() > 25) { // We don't see the cube
                    wm.setPower(.4, .4);
                /*mot[0].setPower(-.4);//125);
                mot[3].setPower(-.4);//125);
                mot[1].setPower(.4);//125);
                mot[2].setPower(.4);//125);*/
                } else { // We don't see the cube
                /*for (DcMotor m : mot) {
                    m.setPower(.125);
                }*/
                    if (wm.getPolPos()[1] * 1800 / Math.PI > 20 && !direction) {
                        wm.setPower(-.125, .125);
                    } else {
                        wm.setPower(.125, -.125);
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
            //region State: Off
            case OFF: {
                if (stateIterations == 0) {
                    stop();
                }
                break;
            }
            //endregion
            //region State: !!!!
            default: {
                throw new IllegalStateException("Illegal state: " + state);
            }
            //endregion
        }
        stateIterations++;
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        detector.disable();
        wm.setPower(0,0);
        servo.setPosition(1);
        //servo.setPosition(15/180);
    }

}

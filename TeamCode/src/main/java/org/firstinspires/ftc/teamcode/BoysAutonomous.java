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


@Autonomous(name="Boys Autonomous", group="DogeCV")

public class BoysAutonomous extends OpMode
{
    private GoldAlignDetector detector;
    // looking for a 5cm X 5cm X 5cm gold cube
    // ~170px, 100px at 21 cm
    // 85px by 115px at 30 cm
    private DcMotor[] mot = new DcMotor[5];
    private Servo servo;
    private enum State {HANGING, CUBE, IN_PIT}
    private State state;
    private WheelManager wm;
    private boolean direction = false;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ElapsedTime runtime = new ElapsedTime();
    //mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    //mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);


    @Override
    public void init() {
        telemetry.addData("Status", "DogeCV 2018.0 - Gold Align Example");

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
        state = State.HANGING;
        runtime.reset();
    }


    @Override
    public void loop() {
        if (state == State.HANGING) {
            if (runtime.seconds() <= 10) {
                mot[4].setPower(-1);
            } else if (getRuntime() > 11) {
                state = State.CUBE;
            } else {
                mot[4].setPower(0);
            }
            updateInfo();
        } else if (state == State.CUBE) {
            if (detector.isFound()) {
                updateInfo();
                if (detector.getAligned() != 0) {
                    wm.setPower(detector.getAligned() * .125,-detector.getAligned() * .125);
                    /*for (DcMotor m : mot) {
                        m.setPower(detector.getAligned() * .125);
                    }*/
                } else {
                    int ix = 2;
                    int margin = 1;
                    int target = 30;
                    if ((detector.getDistances()[ix] + margin) / target > 1 && (detector.getDistances()[ix] - margin) / target > 1) {
                        wm.setPower(.125,.125);
                        /*mot[0].setPower(-.125);
                        mot[3].setPower(-.125);
                        mot[1].setPower(.125);
                        mot[2].setPower(.125);*/
                        telemetry.addLine("Forward!");
                    } else if ((detector.getDistances()[ix] + margin) / target < 1 && (detector.getDistances()[ix] - margin) / target < 1) {
                        wm.setPower(-.125,-.125);
                        /*mot[0].setPower(.125);
                        mot[3].setPower(.125);
                        mot[1].setPower(-.125);
                        mot[2].setPower(-.125);*/
                        telemetry.addLine("Back!");
                    } else {
                        wm.setPower(0,0);
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
                        wm.setPower(.4,.4);
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

                        stop();
                        state = State.IN_PIT;
                    }
                }
            } else if (runtime.seconds() > 25) { // We don't see the cube
                wm.setPower(.4,.4);
                /*mot[0].setPower(-.4);//125);
                mot[3].setPower(-.4);//125);
                mot[1].setPower(.4);//125);
                mot[2].setPower(.4);//125);*/
            } else { // We don't see the cube
                /*for (DcMotor m : mot) {
                    m.setPower(.125);
                }*/
                if (wm.getPolPos()[1]*1800/Math.PI > 20 && !direction) {
                    wm.setPower(-.125,.125);
                } else {
                    wm.setPower(.125,-.125);
                }
            }
        } else { // In the pit
            wm.setPower(0,0);
            /*for (DcMotor m : mot) {
                m.setPower(0);
            }*/
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        detector.disable();
        for (DcMotor m : mot) {
            m.setPower(0);
        }
        servo.setPosition(1);
        //servo.setPosition(15/180);
    }

}

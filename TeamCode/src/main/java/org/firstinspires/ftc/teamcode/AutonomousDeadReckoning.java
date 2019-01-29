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

import org.firstinspires.ftc.teamcode.GoldAlignDetector;
import org.firstinspires.ftc.teamcode.WheelManager;


@Autonomous(name="ADR", group="DogeCV")

public class AutonomousDeadReckoning extends OpMode
{
    private GoldAlignDetector detector;
    // looking for a 5cm X 5cm X 5cm gold cube
    // ~170px, 100px at 21 cm
    // 85px by 115px at 30 cm
    private DcMotor[] mot = new DcMotor[4];
    private Servo servo;

    private WheelManager wm;


    @Override
    public void init() {
        telemetry.addData("Status", "DogeCV 2018.0 - Gold Align Example");

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

        mot[0] = hardwareMap.get(DcMotor.class, "mot0");
        mot[1] = hardwareMap.get(DcMotor.class, "mot1");
        mot[2] = hardwareMap.get(DcMotor.class, "mot2");
        mot[3] = hardwareMap.get(DcMotor.class, "mot3");
        mot[0].setDirection(DcMotor.Direction.REVERSE);
        mot[3].setDirection(DcMotor.Direction.REVERSE);

        servo = hardwareMap.get(Servo.class, "ser0");
        servo.setPosition(180/180.);

        wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, .2);
    }

    void updateInfo() {
        if (detector.isFound()) {
            telemetry.addData("IsAligned", detector.getAligned()); // Is the bot aligned with the gold mineral
            //telemetry.addData("X Pos", detector.getXPosition()); // Gold X pos.
            //telemetry.addData("Y Pos", detector.getYPosition());
            //telemetry.addData("Dimensions", "%d, %d", detector.getRect().width, detector.getRect().height);
            telemetry.addData("Distance", "%.2f, %.2f, %.2f", detector.getDistances()[0], detector.getDistances()[1], detector.getDistances()[2]);
            telemetry.addData("Variance", "%.2f, %.2f", detector.getDistances()[2]-detector.getDistances()[0], (detector.getDistances()[2] + detector.getDistances()[0])/2);
        }
        telemetry.addData("Position", "(%.2f, %.2f)", wm.getCartPos()[0], wm.getCartPos()[1]);
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

    }


    @Override
    public void loop() {
        if (detector.isFound()) {
            updateInfo();
            if (detector.getAligned() != 0) {
                wm.setPower(detector.getAligned(), -detector.getAligned());
            } else {
                int margin = 1;
                int target = 15;
                if ((detector.getDistances()[1]+margin)/target > 1 && (detector.getDistances()[1]-margin)/target > 1) {
                    wm.setPower(1,1);
                    telemetry.addLine("Forward!");
                } else if ((detector.getDistances()[1]+margin)/target < 1 && (detector.getDistances()[1]-margin)/target < 1) {
                    wm.setPower(-1,-1);
                    telemetry.addLine("Back!");
                } else {
                    wm.setPower(0,0);
                    telemetry.addLine("Stop!");
                    servo.setPosition(0/180.);

                    wm.setPower(1,1);
                    //mot[0].setPower(-.125);
                    //mot[3].setPower(-.125);
                    //mot[1].setPower(.125);
                    //mot[2].setPower(.125);

                    //while (detector.isFound());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    wm.setPower(0,0);
                }
            }
        } else {
            wm.setPower(1,-1);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        detector.disable();
        wm.setPower(0,0);
        servo.setPosition(15/180);
    }

}

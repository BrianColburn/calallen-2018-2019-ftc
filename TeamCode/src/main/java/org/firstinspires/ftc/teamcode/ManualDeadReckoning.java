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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.WheelManager;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="MDR", group="Iterative Opmode")
public class ManualDeadReckoning extends OpMode
{
    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor[] mot = new DcMotor[5];
    private WheelManager wm;
    DcMotor hook;
    int hookDir = 1;
    double hookReverseTime = 0;
    Servo servo;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        for (int i=0;i<mot.length;i++) {
            try {
                mot[i] = hardwareMap.get(DcMotor.class, "mot" + i);
            } catch (IllegalArgumentException e){
                telemetry.addData("mot"+i+" is null","");
            }
        }
        mot[0].setDirection(DcMotor.Direction.REVERSE);
        mot[3].setDirection(DcMotor.Direction.REVERSE);
        for (int i=0;i<mot.length;i++) {
            mot[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            mot[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        servo = hardwareMap.get(Servo.class, "ser0");
        servo.setPosition(0);

        wm = new WheelManager(mot, 10/2, (2*Math.PI)/2.5, 40.64, 1, 1120);

        hook  = mot[4];

        //wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, .2);

        mot[0].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, .2);

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        for (int i=0;i<mot.length;i++) {
            mot[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        runtime.reset();
    }

    int target = 10;

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;


        // Choose to drive using either Tank Mode, or POV Mode
        // Comment out the method that's not used.  The default below is POV.

        // POV Mode uses left stick to go forward, and right stick to turn.
        // - This uses basic math to combine motions and is easier to drive straight.
        double drive = -gamepad1.right_stick_y;
        double turn  =  gamepad1.right_stick_x;
        leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;
        double hookPow = Math.abs(gamepad1.left_stick_y) > Math.abs(gamepad2.left_stick_y) ? gamepad1.left_stick_y : gamepad2.left_stick_y;
        //double servoPos = Math.max(Math.max(gamepad1.right_trigger, gamepad2.right_trigger), 1/180.);

        // Tank Mode uses one stick to control each wheel.
        // - This requires no math, but it is hard to drive forward slowly and keep straight.
        // leftPower  = -gamepad1.left_stick_y ;
        // rightPower = -gamepad1.right_stick_y ;

        // Send calculated power to wheels
        wm.setPower(leftPower, rightPower);
        hook.setPower(hookPow);
        //servo.setPosition(servoPos);

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f), hook (%.2f)", leftPower, rightPower, hookPow);
        //telemetry.addData("Servo", "(%.0f) Degrees", servoPos*180);
        double[] pos = wm.getPolPos();//getCartPos();
        //telemetry.addData("Position", "(%.2f, %.2f, %.2f)", pos[0], pos[1], pos[2]*180/Math.PI%360);

        telemetry.addData("Position", "(%.2f)/_(%.2f)", pos[0],pos[1]*1800/Math.PI);
        telemetry.addData("Corrected", "(%f)/_(%f)", 25.4/3.533482*pos[0],360./511*pos[1]*1800/Math.PI);
        telemetry.addData("Inches", "(%f)/_(%f)", 10/3.533482*pos[0],pos[1]*1800/Math.PI);

        telemetry.addData("Encoders", "FL: %d, BR: %d, HK: %d", mot[1].getCurrentPosition(), mot[3].getCurrentPosition(), mot[4].getCurrentPosition());
        //telemetry.addData("Position", "(%.2f)/_(%.2f)", pos[0],pos[1]*1800/Math.PI);
        telemetry.addData("Target Position", mot[0].getTargetPosition());
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}

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

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.BoysController;
import org.firstinspires.ftc.teamcode.Controller;
import org.firstinspires.ftc.teamcode.Foo;
import org.firstinspires.ftc.teamcode.GirlsController;

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

@TeleOp(name="Manual", group="Iterative Opmode")
public class BasicOpMode_Iterative extends OpMode
{
    // Declare OpMode members.
    SensorManager sensorManager;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor[] mot = new DcMotor[8];
    DcMotor hook;
    int hookDir = 1;
    double hookReverseTime = 0;
    Servo[] ser;
    double servoPos = 0;
    Foo foo;

    Controller c;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        while (c == null) {
            if (gamepad1.y) {
                c = new BoysController(gamepad1, gamepad2);
            } else if (gamepad1.x) {
                c = new GirlsController(gamepad1, gamepad2);
            }
        }

        ser = new Servo[2];

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
        hook  = mot[4];
        for (int i=0;i<ser.length;i++) {
            try {
                ser[i] = hardwareMap.get(Servo.class, "ser"+i);
            } catch (IllegalArgumentException e) {
                telemetry.addData("ser"+i+" is null","");
            }
        }

        if (c instanceof BoysController && ser[0] != null) {
            //ser[0].scaleRange(0,.75);
        }

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        mot[0].setDirection(DcMotor.Direction.REVERSE);
        mot[3].setDirection(DcMotor.Direction.REVERSE);

        /*foo = new Foo(telemetry);
        sensorManager = (SensorManager)new Activity().getSystemService(Context.SENSOR_SERVICE);
        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(foo, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(foo, asensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(foo, msensor, SensorManager.SENSOR_DELAY_GAME);*/

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
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        c.update_state();

        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower;
        double rightPower;

        if (gamepad1.left_bumper && runtime.seconds() - hookReverseTime >= .5) {
            hookDir *= -1;
            hookReverseTime = runtime.seconds();
        }
        double[] wheel_powers = c.wheel_power();
        leftPower  = wheel_powers[0];
        rightPower = wheel_powers[1];
        double hookPow = c.lift_power();
        servoPos = c.servo_pos();

        // Send calculated power to wheels
        mot[1].setPower(leftPower);
        mot[2].setPower(leftPower);
        mot[0].setPower(rightPower);
        mot[3].setPower(rightPower);
        if (hook != null) hook.setPower(hookPow);
        if (ser[0] != null) ser[0].setPosition(servoPos);
        if (ser.length == 2 && ser[1] != null) {
            ser[1].setPosition(c.servo2_pos());
        }

        if (mot[6] != null) {
            if (gamepad1.dpad_up || gamepad1.dpad_down) {
                mot[6].setPower(gamepad1.dpad_up ? 1 : -1);
            } else {
                mot[6].setPower(0);
            }
        }

        if (mot[7] != null) {
            if (gamepad1.dpad_left || gamepad1.dpad_right) {
                mot[7].setPower(gamepad1.dpad_left ? 1 : -1);
            } else {
                mot[7].setPower(0);
            }
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Motors", "left (%.2f), right (%.2f), hook (%.2f)", leftPower, rightPower, hookPow);
        telemetry.addData("Servo", "(%.0f) Degrees, actually (%.2f)", servoPos*180,servoPos);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }

}

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

package org.firstinspires.ftc.teamcode

import android.hardware.SensorManager

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime

import java.util.ArrayList

import java8.util.Optional

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

@TeleOp(name = "Manual", group = "Iterative Opmode")
class Manual : OpMode() {
    // Declare OpMode members.
    internal var sensorManager: SensorManager? = null
    private val runtime = ElapsedTime()
    private val mot = ArrayList<DcMotor?>()
    internal var hook: DcMotor? = null
    internal var hookDir = 1
    internal var hookReverseTime = 0.0
    internal val ser = ArrayList<Servo?>()
    internal var servoPos = 0.0

    internal var c: Controller? = null

    /*
     * Code to run ONCE when the driver hits INIT
     */
    override fun init() {
        telemetry.addData("Status", "Initialized")
        while (c == null) {
            if (gamepad1.y) {
                c = BoysController(gamepad1, gamepad2)
            } else if (gamepad1.x) {
                c = GirlsController(gamepad1, gamepad2)
            }
        }

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        for (i in 0..7) {
            mot.add(hardwareMap.tryGet(DcMotor::class.java, "mot$i"))
            if (mot[i] !is DcMotor) {
                telemetry.addData("mot$i is null", "")
            }
        }
        hook = mot[4]
        for (i in 0..7) {
            ser.add(hardwareMap.tryGet(Servo::class.java, "ser$i"))
            if (ser[i] !is Servo) {
                telemetry.addData("ser$i is null", "")
            }
        }

        if (c is BoysController && ser[0] is Servo) {
            //ser[0].scaleRange(0,.75);
        }

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        mot[0]?.direction = DcMotorSimple.Direction.REVERSE
        mot[3]?.direction = DcMotorSimple.Direction.REVERSE

        /*foo = new Foo(telemetry);
        sensorManager = (SensorManager)new Activity().getSystemService(Context.SENSOR_SERVICE);
        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(foo, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(foo, asensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(foo, msensor, SensorManager.SENSOR_DELAY_GAME);*/

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized")
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    override fun init_loop() {}

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    override fun start() {
        runtime.reset()
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    override fun loop() {
        c!!.update_state()

        // Setup a variable for each drive wheel to save power level for telemetry
        val leftPower: Double
        val rightPower: Double

        if (gamepad1.left_bumper && runtime.seconds() - hookReverseTime >= .5) {
            hookDir *= -1
            hookReverseTime = runtime.seconds()
        }
        val wheel_powers = c!!.wheel_power()
        leftPower = wheel_powers[0]
        rightPower = wheel_powers[1]
        val hookPow = c!!.lift_power()
        servoPos = c!!.servo_pos()

        // Send calculated power to wheels
        mot[1]?.power = leftPower
        mot[2]?.power = leftPower
        mot[0]?.power = rightPower
        mot[3]?.power = rightPower
        hook?.power = hookPow
        ser[0]?.let { it.position = servoPos }
        ser[1]?.let { it.position = c!!.servo2_pos() }

        if (gamepad1.dpad_up || gamepad1.dpad_down) {
            mot[6]?.power = (if (gamepad1.dpad_up) 1 else -1).toDouble()
        } else {
            mot[6]?.power = 0.0
        }

        if (gamepad1.dpad_left || gamepad1.dpad_right) {
            mot[7]?.power = (if (gamepad1.dpad_left) 1 else -1).toDouble()
        } else {
            mot[7]?.power = 0.0
        }

        // Show the elapsed game time and wheel power.
        telemetry.addData("Status", "Run Time: $runtime")
        telemetry.addData("Motors", "left (%.2f), right (%.2f), hook (%.2f)", leftPower, rightPower, hookPow)
        telemetry.addData("Servo", "(%.0f) Degrees, actually (%.2f)", servoPos * 180, servoPos)
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    override fun stop() {}

}

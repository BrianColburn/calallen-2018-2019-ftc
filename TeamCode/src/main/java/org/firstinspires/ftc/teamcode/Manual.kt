package org.firstinspires.ftc.teamcode

import android.hardware.SensorManager

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime

import java.util.ArrayList

/**
 * Provides a bare minimum manual control opmode.
 * This can be used to figure out whether a bug is resulting from hardware or software.
 */
@TeleOp(name = "Manual", group = "Iterative Opmode")
class Manual : OpMode() {
    // Declare OpMode members.
    private val runtime = ElapsedTime()
    private val mot = ArrayList<DcMotor?>()
    internal var hook: DcMotor? = null
    internal val ser = ArrayList<Servo?>()
    internal var servoPos = 0.0

    internal var c: Controller? = null

    /*
     * Code to run ONCE when the driver hits INIT
     */
    override fun init() {
        telemetry.addData("Status", "Initialized")
        while (c !is Controller) {
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

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        mot[0]?.direction = DcMotorSimple.Direction.REVERSE
        mot[3]?.direction = DcMotorSimple.Direction.REVERSE

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized")
    }

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
        c!!.updateState()

        // Setup a variable for each drive wheel to save power level for telemetry
        val leftPower: Double
        val rightPower: Double

        val wheelPowers = c!!.wheelPower()
        leftPower = wheelPowers[0]
        rightPower = wheelPowers[1]
        val hookPow = c!!.liftPower()
        servoPos = c!!.servoPos()

        // Send calculated power to wheels
        mot[1]?.power = leftPower
        mot[2]?.power = leftPower
        mot[0]?.power = rightPower
        mot[3]?.power = rightPower
        hook?.power = hookPow
        ser[0]?.let { it.position = servoPos }
        ser[1]?.let { it.position = c!!.servo2Pos() }

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

}

package org.firstinspires.ftc.teamcode

import android.hardware.SensorManager
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.*
import java.util.*

/**
 * Manual control opmode with bells and whistles.
 * This is supposed to be used mostly to develop things for autonomous.
 */

@TeleOp(name = "Manual (bells and whistles)", group = "Iterative Opmode")
class ManualBW : OpMode() {
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

    //region BellsAndWhistles
    // The IMU sensor object
    internal var imu: BNO055IMU? = null

    // State used for updating telemetry
    internal lateinit var angles: Orientation
    internal lateinit var gravity: Acceleration
    //endregion

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

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        mot[0]?.direction = DcMotorSimple.Direction.REVERSE
        mot[3]?.direction = DcMotorSimple.Direction.REVERSE

        // Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized")

        //region InitBellsAndWhistles
        val parameters = BNO055IMU.Parameters()
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        parameters.calibrationDataFile = "BNO055IMUCalibration.json" // see the calibration sample opmode
        parameters.loggingEnabled = true
        parameters.loggingTag = "IMU"
        parameters.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()
        imu = hardwareMap.tryGet(BNO055IMU::class.java, "imu")
        imu?.initialize(parameters)

        composeTelemetry()
        //endregion
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    override fun start() {
        imu?.startAccelerationIntegration(Position(), Velocity(), 1000)
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


    internal fun composeTelemetry() {

        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction {
            // Acquiring the angles is relatively expensive; we don't want
            // to do that in each of the three items that need that info, as that's
            // three times the necessary expense.
            angles = imu!!.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
            gravity = imu!!.getGravity()
        }

        telemetry.addLine()
                .addData("status") { imu?.getSystemStatus()?.toShortString() }
                .addData("calib") { imu?.getCalibrationStatus().toString() }

        telemetry.addLine()
                .addData("heading") { formatAngle(angles.angleUnit, angles.firstAngle.toDouble()) }
                .addData("roll") { formatAngle(angles.angleUnit, angles.secondAngle.toDouble()) }
                .addData("pitch") { formatAngle(angles.angleUnit, angles.thirdAngle.toDouble()) }

        telemetry.addLine()
                .addData("grvty") { gravity.toString() }
                .addData("mag") {
                    String.format(Locale.getDefault(), "%.3f",
                            Math.sqrt(gravity.xAccel * gravity.xAccel
                                    + gravity.yAccel * gravity.yAccel
                                    + gravity.zAccel * gravity.zAccel))
                }
    }

    //----------------------------------------------------------------------------------------------
    // Formatting
    //----------------------------------------------------------------------------------------------

    internal fun formatAngle(angleUnit: AngleUnit, angle: Double): String {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle))
    }

    internal fun formatDegrees(degrees: Double): String {
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees))
    }
}

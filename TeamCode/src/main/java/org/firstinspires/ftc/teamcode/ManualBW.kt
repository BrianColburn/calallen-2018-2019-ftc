package org.firstinspires.ftc.teamcode

import android.hardware.SensorManager
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.*
import org.firstinspires.ftc.teamcode.wheelmanager.WheelManager
import java.util.*

/**
 * Manual control opmode with bells and whistles.
 * This is supposed to be used mostly to develop things for autonomous.
 */

@TeleOp(name = "Manual (bells and whistles)", group = "Linear Opmode")
class ManualBW : LinearOpMode() {

    // Declare OpMode members.
    internal var sensorManager: SensorManager? = null
    private val runtime = ElapsedTime()
    private val mot = ArrayList<DcMotor?>()
    internal var hook: DcMotor? = null
    internal var hookDir = 1
    internal var hookReverseTime = 0.0
    internal val ser = ArrayList<Servo?>()
    internal var servoPos = 0.0

    lateinit var c: Controller // We can't initialize c here because the gamepads are still null

    //region BellsAndWhistles
    // The IMU sensor object
    internal var imu: BNO055IMU? = null

    // State used for updating telemetry
    internal lateinit var angles: Orientation
    internal lateinit var gravity: Acceleration


    lateinit var wm: WheelManager
    //endregion


    override fun runOpMode() {
        //region Initialization
        telemetry.addData("Status", "Initializing")
        c = BoysController(gamepad1, gamepad2)

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

        wm = WheelManager(mot.takeWhile { it is DcMotor }, 8.89 / 2, 37.5, 1160, this)
        wm.initializeMotors(intArrayOf(0,3))
        wm.setPower(0.0,0.0)

        for (i in 0..7) {
            ser.add(hardwareMap.tryGet(Servo::class.java, "ser$i"))
            if (ser[i] !is Servo) {
                telemetry.addData("ser$i is null", "")
            }
        }

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
        //endregion
        //region Initloop
        while(!opModeIsActive()) {

        }
        //endregion
        //region On Start
        imu?.startAccelerationIntegration(Position(), Velocity(), 1000)
        runtime.reset()
        //endregion
        //region Loop
        while (opModeIsActive()) {
            c.update_state()

            // Setup a variable for each drive wheel to save power level for telemetry
            val leftPower: Double
            val rightPower: Double

            val wheel_powers = c.wheel_power()
            leftPower = wheel_powers[0]
            rightPower = wheel_powers[1]
            val hookPow = c.lift_power()
            servoPos = c.servo_pos()

            // Send calculated power to wheels
            wm.setPower(leftPower, rightPower)
            hook?.power = hookPow
            ser[0]?.let { it.position = servoPos }
            ser[1]?.let { it.position = c.servo2_pos() }

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
        //endregion
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

        telemetry.addLine()
                .addData("X") { wm.getCartPos()[0] }
                .addData("Y") { wm.getCartPos()[1] }
                .addData("T") { wm.getCartPos()[2] }
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

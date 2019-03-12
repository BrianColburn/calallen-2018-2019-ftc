package org.firstinspires.ftc.teamcode

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.navigation.*
import java.util.*

@Autonomous(name="KLOM", group = "kotlin")
class KLOM: LinearOpMode() {
    lateinit var angles: Orientation
    lateinit var gravity: Acceleration

    override fun runOpMode() {
        // The IMU sensor object
        val imu = hardwareMap.tryGet<BNO055IMU>(BNO055IMU::class.java, "imu");

        // State used for updating telemetry
        val parameters = BNO055IMU.Parameters()
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        parameters.calibrationDataFile = "BNO055IMUCalibration.json" // see the calibration sample opmode
        parameters.loggingEnabled = true
        parameters.loggingTag = "IMU"
        var accelerationIntegrator: BNO055IMU.AccelerationIntegrator
        parameters.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()
        imu?.initialize(parameters)

        // Set up our telemetry dashboard
        composeTelemetry(imu)

        // Wait until we're told to go
        waitForStart()

        // Start the logging of measured acceleration
        imu?.startAccelerationIntegration(Position(), Velocity(), 1000)

        // Loop and update the dashboard
        while (opModeIsActive()) {
            telemetry.update()
        }
    }

    //----------------------------------------------------------------------------------------------
    // Telemetry Configuration
    //----------------------------------------------------------------------------------------------

    internal fun composeTelemetry(imu: BNO055IMU?) {
        if (imu is BNO055IMU) {

            // At the beginning of each telemetry update, grab a bunch of data
            // from the IMU that we will then display in separate lines.
            telemetry.addAction {
                // Acquiring the angles is relatively expensive; we don't want
                // to do that in each of the three items that need that info, as that's
                // three times the necessary expense.
                angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES)
                gravity = imu.gravity
            }

            telemetry.addLine()
                    .addData("status") { imu.systemStatus.toShortString() }
                    .addData("calib") { imu.calibrationStatus.toString() }

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
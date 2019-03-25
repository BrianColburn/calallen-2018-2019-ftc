package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.ColorSensor

@TeleOp(name = "ColorSensorTest", group = "dev")
class ColorSensorTest : LinearOpMode() {
    override fun runOpMode() {
        val sclr = hardwareMap.tryGet(ColorSensor::class.java, "sclr")!!
        while (!opModeIsActive());
        while (opModeIsActive()) {
            telemetry.addData("ARGB:", "%x", sclr.argb())
            telemetry.addData("RGB", "%d,%d,%d", sclr.red(), sclr.green(), sclr.blue())
            telemetry.update()
        }
    }
}
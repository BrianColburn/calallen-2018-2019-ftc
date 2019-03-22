package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.ColorSensor
import org.firstinspires.ftc.robotcontroller.external.samples.SensorREVColorDistance

class ColorSensorTest : LinearOpMode() {
    override fun runOpMode() {
        val sclr = hardwareMap.tryGet(ColorSensor::class.java, "sclr")
        while (!opModeIsActive());
        while (opModeIsActive()) {
            telemetry.addData("ARGB:", "%x", sclr?.argb())
            telemetry.update()
        }
    }
}
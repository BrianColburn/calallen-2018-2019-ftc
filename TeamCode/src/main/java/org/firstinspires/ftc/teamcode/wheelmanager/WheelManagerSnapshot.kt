package org.firstinspires.ftc.teamcode.wheelmanager

import org.firstinspires.ftc.teamcode.units.Centimeter
import org.firstinspires.ftc.teamcode.units.Degree

data class WheelManagerSnapshot(
        val time: Double,
        val encoders: IntArray,
        val cm: Centimeter,
        val degrees: Degree
)

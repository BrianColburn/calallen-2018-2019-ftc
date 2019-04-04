package org.firstinspires.ftc.teamcode.wheelmanager

import org.firstinspires.ftc.teamcode.units.Degree
import org.firstinspires.ftc.teamcode.units.Distance

data class WheelManagerSnapshot(
        val time: Double,
        val encoders: IntArray,
        val distance: Distance,
        val degrees: Degree
)

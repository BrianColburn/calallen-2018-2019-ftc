package org.firstinspires.ftc.teamcode.units

import java.util.*

open class Foot(v: Double) : Inches(v*12) {
    override fun toString(): String {
        return String.format(Locale.getDefault(), "%.1fft", value/12)
    }
}

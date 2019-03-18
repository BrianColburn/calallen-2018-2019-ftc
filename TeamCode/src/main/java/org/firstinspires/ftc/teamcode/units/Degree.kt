package org.firstinspires.ftc.teamcode.units

class Degree(val value: Double): Angle {
    override fun getDegrees(): Double {
        return value
    }

    operator fun minus(degrees: Degree): Degree {
        return Degree(value - degrees.value)
    }
}
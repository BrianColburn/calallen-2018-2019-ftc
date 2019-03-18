package org.firstinspires.ftc.teamcode.units

interface Distance : Unit, Comparable<Distance> {
    fun value(): Double
    fun toCM(): Centimeter
    fun toInches(): Inches


    fun abs(): Double {
        return Math.abs(value())
    }

    fun signum(): Double {
        return Math.signum(value())
    }

    override fun compareTo(other: Distance): Int {
        return value().compareTo(other.value())
    }
}
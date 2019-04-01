package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

abstract class Distance : Comparable<Distance> {
    abstract val unit: DistanceUnit
    abstract fun value(): Double
    abstract fun toCM(): Centimeter
    abstract fun toInches(): Inches


    abstract fun <T> of(value: Double, unit: DistanceUnit): T

    fun abs(): Double {
        return Math.abs(value())
    }

    fun signum(): Double {
        return Math.signum(value())
    }

    override fun compareTo(other: Distance): Int {
        return value().compareTo(other.value())
    }

    fun <T: DistanceUnit> fromUnit(him: T, his: Double): T {
        return of(him.fromUnit(him, his), him)
    }
}
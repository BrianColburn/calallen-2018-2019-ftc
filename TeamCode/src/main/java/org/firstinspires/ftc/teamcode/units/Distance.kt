package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

abstract class Distance : Comparable<Distance> {
    abstract val unit: DistanceUnit
    abstract val value: Double
    abstract fun toCM(): Centimeter
    abstract fun toInches(): Inches


    fun abs(): Double {
        return Math.abs(value)
    }

    fun signum(): Double {
        return Math.signum(value)
    }

    override fun compareTo(other: Distance): Int {
        return value.compareTo(other.toUnit(unit).value)
    }

    fun toUnit(him: DistanceUnit): Distance {
        return object : Distance() {
            override val unit = him
            override val value = him.fromUnit(this@Distance.unit, this@Distance.value)

            override fun toCM(): Centimeter {
                return Centimeter(this.toUnit(DistanceUnit.CM).value)
            }

            override fun toInches(): Inches {
                return Inches(this.toUnit(DistanceUnit.INCH).value)
            }
        }
    }
}

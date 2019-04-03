package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

/**
 * Provides a base distance class.
 * A Distance is a Double paired with a DistanceUnit.
 *
 * This class provides comparison, conversion, and basic math for distances.
 * Multiplication between distances is not supported; however, scaling is.
 */
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

    operator fun times(s: Number) = object : Distance() {
        override val unit = this@Distance.unit
        override val value = this@Distance.value * s.toDouble()

        override fun toCM(): Centimeter {
            return Centimeter(this.toUnit(Centimeter.unit).value)
        }

        override fun toInches(): Inches {
            return Inches(this.toUnit(Inches.unit).value)
        }
    }

    operator fun plus(d: Distance) = object : Distance() {
        override val unit = this@Distance.unit
        override val value = this@Distance.value + d.toUnit(unit).value

        override fun toCM(): Centimeter {
            return Centimeter(this.toUnit(Centimeter.unit).value)
        }

        override fun toInches(): Inches {
            return Inches(this.toUnit(Inches.unit).value)
        }
    }

    operator fun minus(d: Distance) = this + -d

    operator fun unaryMinus() = object : Distance() {
        override val unit = this@Distance.unit
        override val value = -this@Distance.value

        override fun toCM(): Centimeter {
            return Centimeter(this.toUnit(Centimeter.unit).value)
        }

        override fun toInches(): Inches {
            return Inches(this.toUnit(Inches.unit).value)
        }
    }

    fun toUnit(him: DistanceUnit): Distance {
        return object : Distance() {
            override val unit = him
            override val value = him.fromUnit(this@Distance.unit, this@Distance.value)

            override fun toCM(): Centimeter {
                return Centimeter(this.toUnit(Centimeter.unit).value)
            }

            override fun toInches(): Inches {
                return Inches(this.toUnit(Inches.unit).value)
            }
        }
    }

    override fun toString(): String {
        return unit.toString(value)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Distance) {
            if (this.unit == other.unit) {
                //println("${Math.round(this.value*1000)} =?= ${Math.round(other.value*1000)}")
                return Math.round(this.value*1000) == Math.round(other.value*1000)
            } else { // We need to get a common unit
                //println("${toCM()} =?= ${other.toCM()}")
                return this.toCM() == other.toCM()
            }
        }
        return false
    }
}

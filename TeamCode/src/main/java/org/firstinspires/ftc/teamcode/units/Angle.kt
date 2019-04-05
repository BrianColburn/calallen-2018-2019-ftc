package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import java.util.*

class Angle(val baseUnit: AngleUnit,
            val relation: Double,
            val precision: Int,
            val symbol: String,
            value: Double) : Comparable<Angle> {
    val value = relation*value

    constructor(unit: AngleUnit, value: Double):
            this(unit,
                    1.0,
                    when (unit) {
                        AngleUnit.DEGREES -> 2
                        AngleUnit.RADIANS -> 8
                    },
                    when (unit) {
                        AngleUnit.DEGREES -> "°"
                        AngleUnit.RADIANS -> "rad"
                    },
                    value)

    fun abs(): Double {
        return Math.abs(value)
    }

    fun signum(): Double {
        return Math.signum(value)
    }

    val degrees get() = this.toUnit(AngleUnit.DEGREES)
    val radians get() = this.toUnit(AngleUnit.RADIANS)

    override fun compareTo(other: Angle): Int {
        return value.compareTo(other.toUnit(baseUnit).value)
    }

    operator fun times(s: Number) = Angle(this.baseUnit, this.value * s.toDouble())

    operator fun plus(a: Angle) = Angle(this.baseUnit, this.value + a.toUnit(this.baseUnit).value)

    operator fun minus(a: Angle) = this + -a

    operator fun unaryMinus() = Angle(this.baseUnit, -this.value)

    fun toUnit(newUnit: AngleUnit) = Angle(newUnit, newUnit.fromUnit(this.baseUnit, this.value))

    fun normalize(): Angle {
        return when (baseUnit) {

            AngleUnit.DEGREES -> Degree(AngleUnit.normalizeDegrees(value))
            AngleUnit.RADIANS -> Radian(AngleUnit.normalizeRadians(value))
        }
    }

    override fun toString(): String = String.format(Locale.getDefault(), "%.${precision}f$symbol", value/relation)

    override fun equals(other: Any?): Boolean {
        if (other is Angle) {
            if (this.baseUnit == other.baseUnit) {
                return Math.round(this.value*Math.pow(10.0,precision.toDouble())) == Math.round(other.value*Math.pow(10.0,precision.toDouble()))
            } else { // We need to get a common baseUnit
                return this.degrees.normalize() == other.degrees.normalize()
            }
        }
        return false
    }
}

fun Degree(value: Double) = Angle(AngleUnit.DEGREES, value)
fun Radian(value: Double) = Angle(AngleUnit.RADIANS, value)
fun Tau(value: Double)    = Angle(AngleUnit.RADIANS, 2.0, 8, "tau", value)
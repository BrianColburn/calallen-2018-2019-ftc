package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import java.util.*

class Angle(override val baseUnit: AngleUnit,
            val relation: Double,
            val precision: Int,
            val symbol: String,
            value: Double) : Quantity<Angle, AngleUnit> {
    override val magnitude = relation*value

    constructor(unit: AngleUnit, value: Double):
            this(unit,
                    1.0,
                    when (unit) {
                        AngleUnit.DEGREES -> 2
                        AngleUnit.RADIANS -> 4
                    },
                    when (unit) {
                        AngleUnit.DEGREES -> "°"
                        AngleUnit.RADIANS -> "rad"
                    },
                    value)

    val degrees get() = this.toUnit(AngleUnit.DEGREES)
    val radians get() = this.toUnit(AngleUnit.RADIANS)

    override fun compareTo(other: Quantity<Angle, AngleUnit>): Int {
        return magnitude.compareTo(other.toUnit(baseUnit).magnitude)
    }

    override operator fun times(s: Number) = Angle(this.baseUnit, this.magnitude * s.toDouble())

    override operator fun plus(a: Quantity<Angle, AngleUnit>) = Angle(this.baseUnit, this.magnitude + a.toUnit(this.baseUnit).magnitude)

    override operator fun unaryMinus() = Angle(this.baseUnit, -this.magnitude)

    override fun toUnit(newUnit: AngleUnit) = Angle(newUnit, newUnit.fromUnit(this.baseUnit, this.magnitude))

    fun normalize(): Angle {
        return when (baseUnit) {

            AngleUnit.DEGREES -> Degree(AngleUnit.normalizeDegrees(magnitude))
            AngleUnit.RADIANS -> Radian(AngleUnit.normalizeRadians(magnitude))
        }
    }

    override fun toString(): String = String.format(Locale.getDefault(), "%.${precision}f$symbol", magnitude/relation)

    override fun equals(other: Any?): Boolean {
        if (other is Angle) {
            if (this.baseUnit == other.baseUnit) {
                return Math.round(this.magnitude*Math.pow(10.0,precision.toDouble())) == Math.round(other.magnitude*Math.pow(10.0,precision.toDouble()))
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
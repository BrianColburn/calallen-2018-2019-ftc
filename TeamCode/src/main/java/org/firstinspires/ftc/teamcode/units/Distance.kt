package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import java.util.*

/**
 * Provides a class that pairs FTC's DistanceUnit Enums with an explicit quantity.
 *
 * Comparisons between, conversions from, and basic math can be performed upon distances.
 * Multiplication between distances is not supported; however, scaling is.
 *
 * Instances can be created simply by supplying a unit and value,
 *  or more complex units can be specified in terms of a base DistanceUnit.
 * Derived units must supply a multiplicative relation to an existing DistanceUnit,
 *  the precision to use for comparisons and printing,
 *  and a symbol.
 */
open class Distance(val baseUnit: DistanceUnit,
                    val relation: Double,
                    val precision: Int,
                    val symbol: String,
                    value: Double) : Comparable<Distance> {
    val value = relation*value

    constructor(unit: DistanceUnit, value: Double):
            this(unit,
                    1.0,
                    when (unit) {
                        DistanceUnit.METER -> 3
                        DistanceUnit.CM -> 1
                        DistanceUnit.MM -> 0
                        DistanceUnit.INCH -> 2
                    },
                    unit.toString(),
                    value)


    fun toCM() = this.toUnit(DistanceUnit.CM)
    fun toInches() = this.toUnit(DistanceUnit.INCH)


    fun abs(): Double {
        return Math.abs(value)
    }

    fun signum(): Double {
        return Math.signum(value)
    }

    override fun compareTo(other: Distance): Int {
        return value.compareTo(other.toUnit(baseUnit).value)
    }

    operator fun times(s: Number) = Distance(this.baseUnit, this.value * s.toDouble())

    operator fun plus(d: Distance) = Distance(this.baseUnit, this.value + d.toUnit(this.baseUnit).value)

    operator fun minus(d: Distance) = this + -d

    operator fun unaryMinus() = Distance(this.baseUnit, -this.value)

    fun toUnit(newUnit: DistanceUnit) = Distance(newUnit, newUnit.fromUnit(this.baseUnit, this.value))

    override fun toString(): String = String.format(Locale.getDefault(), "%.${precision}f$symbol", value/relation);

    override fun equals(other: Any?): Boolean {
        if (other is Distance) {
            if (this.baseUnit == other.baseUnit) {
                //println("${Math.round(this.value*1000)} =?= ${Math.round(other.value*1000)}")
                return Math.round(this.value*Math.pow(10.0,precision.toDouble())) == Math.round(other.value*Math.pow(10.0,precision.toDouble()))
            } else { // We need to get a common baseUnit
                //println("${toCM()} =?= ${other.toCM()}")
                return this.toCM() == other.toCM()
            }
        }
        return false
    }
}

fun Centimeter(value: Double) = Distance(DistanceUnit.CM, value)
fun Inches(value: Double) = Distance(DistanceUnit.INCH, value)
fun Foot(value: Double) = Distance(DistanceUnit.INCH, 12.0, 1, "ft", value)

package org.firstinspires.ftc.teamcode.units

interface Quantity<Dimension: Quantity<Dimension, Unit>, Unit> : Comparable<Quantity<Dimension, Unit>> {

    val magnitude: Double
    val baseUnit: Unit

    fun toUnit(newUnit: Unit): Dimension

    operator fun times(s: Number): Dimension
        = mkQuantity(this.baseUnit, s.toDouble() * this.magnitude)

    operator fun div(s: Number): Dimension
        = mkQuantity(this.baseUnit, this.magnitude / s.toDouble())

    operator fun plus(q: Quantity<Dimension, Unit>): Dimension
        = mkQuantity(this.baseUnit, this.magnitude + q.toUnit(this.baseUnit).magnitude)

    operator fun minus(q: Quantity<Dimension, Unit>): Dimension
        = this + -q

    operator fun unaryMinus(): Dimension
        = mkQuantity(this.baseUnit, -this.magnitude)

    fun mkQuantity(baseUnit: Unit, magnitude: Double): Dimension

    fun signum(): Double {
        return Math.signum(magnitude)
    }

    fun abs(): Double {
        return Math.abs(magnitude)
    }

    override fun compareTo(other: Quantity<Dimension, Unit>): Int {
        return magnitude.compareTo(other.toUnit(baseUnit).magnitude)
    }
}

operator fun <D: Quantity<D, U>, U> Number.times(q: Quantity<D,U>) = q*this

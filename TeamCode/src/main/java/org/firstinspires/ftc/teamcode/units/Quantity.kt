package org.firstinspires.ftc.teamcode.units

interface Quantity<Dimension, Unit> : Comparable<Quantity<Dimension, Unit>> {

    val magnitude: Double
    val baseUnit: Unit

    fun toUnit(newUnit: Unit): Quantity<Dimension, Unit>

    operator fun times(s: Number): Quantity<Dimension, Unit>

    operator fun plus(d: Quantity<Dimension, Unit>): Quantity<Dimension, Unit>

    operator fun minus(q: Quantity<Dimension, Unit>) = this + -q

    operator fun unaryMinus(): Quantity<Dimension, Unit>


    fun signum(): Double {
        return Math.signum(magnitude)
    }

    fun abs(): Double {
        return Math.abs(magnitude)
    }
}

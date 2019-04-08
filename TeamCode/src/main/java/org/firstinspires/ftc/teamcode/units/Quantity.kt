package org.firstinspires.ftc.teamcode.units

interface Quantity<Dimension, Unit> : Comparable<Quantity<Dimension, Unit>> {

    val magnitude: Double
    val baseUnit: Unit

    fun toUnit(newUnit: Unit): Quantity<Dimension, Unit>
}

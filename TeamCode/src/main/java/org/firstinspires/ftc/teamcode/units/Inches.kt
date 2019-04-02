package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

open class Inches(override val value: Double) : Distance() {
    override val unit = DistanceUnit.INCH

    constructor(dist: Distance): this(dist.toUnit(DistanceUnit.INCH).value)

    override fun toCM(): Centimeter {
        return Centimeter(this)
    }

    override fun toInches(): Inches {
        return this
    }

    override fun compareTo(other: Distance): Int {
        return java.lang.Double.compare(value, other.toInches().value)
    }
}

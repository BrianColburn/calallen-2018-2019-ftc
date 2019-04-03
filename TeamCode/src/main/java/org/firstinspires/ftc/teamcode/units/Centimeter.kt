package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

open class Centimeter(override val value: Double) : Distance() {
    override val unit = Static.unit
    companion object Static {
        val unit = DistanceUnit.CM
    }

    constructor(dist: Distance): this(dist.toUnit(Static.unit).value)

    override fun toCM(): Centimeter {
        return this
    }

    override fun toInches(): Inches {
        return Inches(this)
    }
}

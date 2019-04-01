package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

open class Centimeter(val value: Double, override val unit: DistanceUnit) : Distance {
    override fun value(): Double {
        return value
    }

    override fun toCM(): Centimeter {
        return this
    }

    override fun toInches(): Inches {
        return Inches(value / 2.54)
    }

    operator fun minus(d: Distance): Centimeter {
        return Centimeter(value - d.toCM().value)
    }
}

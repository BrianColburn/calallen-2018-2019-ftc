package org.firstinspires.ftc.teamcode.units

import org.firstinspires.ftc.robotcore.external.navigation.Position

//see org.firstinspires.ftc.robotcore.external.navigation.NavUtil

public operator fun Position.plus(other: Position): Position {
    val norm = other.toUnit(this.unit)
    return Position(this.unit,
            this.x + norm.x,
            this.y + norm.y,
            this.z + norm.z,
            this.acquisitionTime)
}

public operator fun Position.minus(other: Position): Position {
    val norm = other.toUnit(this.unit)
    return Position(this.unit,
            this.x - norm.x,
            this.y - norm.y,
            this.z - norm.z,
            this.acquisitionTime)
}

public operator fun Position.unaryMinus(): Position {
    return Position().toUnit(this.unit) - this
}
package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad

/**
 * Provides a controller layout requested by the 2019 boys team.
 */
class BoysController : Controller {
    constructor(p: OpMode) : super(p)
    constructor(g1: Gamepad, g2: Gamepad) : super(g1,g2)

    override fun liftPower(): Double {
        return (if (Math.abs(g1.left_stick_y) > .01) g1.left_stick_y else g2.left_stick_y).toDouble()
    }

    override fun servoPos(): Double {
        return 1 - Math.max((if (g1.right_trigger > 0) g1.right_trigger else g2.right_trigger).toDouble(), 1 / 180.0)
    }

    override fun servo2Pos(): Double {
        return Math.max(if (g1.left_trigger > 0) g1.left_trigger else g2.left_trigger, 0f).toDouble()
    }
}

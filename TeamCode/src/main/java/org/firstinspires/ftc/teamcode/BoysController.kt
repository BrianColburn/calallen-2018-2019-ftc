package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad

class BoysController : Controller {
    constructor(p: OpMode) : super(p)
    constructor(g1: Gamepad, g2: Gamepad) : super(g1,g2)

    override fun lift_power(): Double {
        return (if (Math.abs(g1.left_stick_y) > .01) g1.left_stick_y else g2.left_stick_y).toDouble()
    }

    override fun servo_pos(): Double {
        return 1 - Math.max((if (g1.right_trigger > 0) g1.right_trigger else g2.right_trigger).toDouble(), 1 / 180.0)
    }

    override fun servo2_pos(): Double {
        return Math.max(if (g1.left_trigger > 0) g1.left_trigger else g2.left_trigger, 0f).toDouble()
    }
}

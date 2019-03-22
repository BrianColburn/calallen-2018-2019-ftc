package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.Range

abstract class Controller {

    internal var parent: OpMode? = null
    private var internalG1: Gamepad? = null
    internal val g1: Gamepad
        get() {
            parent.let { return it!!.gamepad1 }
            internalG1.let { return it!! }
            throw IllegalAccessException("Both parent and internalG1 are null")
        }
    internal abstract var g2: Gamepad

    constructor(g1: Gamepad, g2: Gamepad) {
        this.g2 = g2
        this.internalG1 = g1
    }

    constructor(parent: OpMode) {
        this.parent = parent
    }

    fun wheel_power(): DoubleArray {

        val drive = (-g1.right_stick_y).toDouble()
        val turn = g1.right_stick_x.toDouble()
        val leftPower = Range.clip(drive + turn, -1.0, 1.0)
        val rightPower = Range.clip(drive - turn, -1.0, 1.0)
        return doubleArrayOf(leftPower, rightPower)
    }

    abstract fun lift_power(): Double
    abstract fun servo_pos(): Double

    abstract fun servo2_pos(): Double

    open fun update_state() {}
}

package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.util.Range

/**
 * Provides an interface for controllers, allowing layouts to easily be swapped out.
 */
abstract class Controller {

    private var parent: OpMode? = null
    private var internalG1: Gamepad? = null
    private var internalG2: Gamepad? = null
    val g1: Gamepad
        get() {
            parent?.let { return it.gamepad1 }
            internalG1?.let { return it }
            throw IllegalAccessException("Both parent and internalG1 are null")
        }
    val g2: Gamepad
        get() {
            parent?.let { return it.gamepad2 }
            internalG2?.let { return it }
            throw IllegalAccessException("Both parent and internalG2 are null")
        }

    constructor(g1: Gamepad, g2: Gamepad) {
        this.internalG1 = g1
        this.internalG2 = g2
    }

    constructor(parent: OpMode) {
        this.parent = parent
    }

    fun wheelPower(): DoubleArray {

        val drive = (-g1.right_stick_y).toDouble()
        val turn = g1.right_stick_x.toDouble()
        val leftPower = Range.clip(drive + turn, -1.0, 1.0)
        val rightPower = Range.clip(drive - turn, -1.0, 1.0)
        return doubleArrayOf(leftPower, rightPower)
    }

    abstract fun liftPower(): Double
    abstract fun servoPos(): Double

    abstract fun servo2Pos(): Double

    open fun updateState() {}
}

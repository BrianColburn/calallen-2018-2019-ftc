package org.firstinspires.ftc.teamcode.wheelmanager

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import com.qualcomm.robotcore.util.ElapsedTime
import com.vuforia.Vuforia.init

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.units.Angle
import org.firstinspires.ftc.teamcode.units.Distance
import org.firstinspires.ftc.teamcode.units.Unit

import java.util.ArrayDeque
import java.util.Arrays
import java.util.Deque
import java.util.Random
import java.util.logging.Logger

import java8.util.J8Arrays
import java8.util.Optional
import java8.util.function.ToIntFunction

/**
 * Created by Brian Colburn
 */
class KWheelManager(val mot: List<DcMotor>, val wheelRadius: Double, val axleLength: Double, val encoderTicks: Int): WheelManager {
    var left  = 0
    var right = 0
    val time  = ElapsedTime()
    lateinit var telemetry: Telemetry

    override fun initializeMotors(motsToReverse: IntArray?) {
        motsToReverse?.forEach { mot[it].direction = DcMotorSimple.Direction.REVERSE }
    }

    fun initalizeLoggers(t: Telemetry) {
        telemetry = t
    }

    override fun setPower(l: Double, r: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEncoders(): IntArray {
        return intArrayOf(mot[0].currentPosition, mot[1].currentPosition, mot[2].currentPosition, mot[3].currentPosition);
    }

    override fun moveAnother(distanceToMove: Distance?, power: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveTo(pos: Distance?, power: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun turnAnother(angleToRotate: Angle?, power: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun turnTo(theta: Angle?, power: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun processInstruction(instruction: Instruction?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDegrees(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInches(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCM(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPolPos(): DoubleArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCartPos(): DoubleArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
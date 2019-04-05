package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.DcMotor

import org.firstinspires.ftc.teamcode.units.Centimeter
import org.firstinspires.ftc.teamcode.units.Foot
import org.firstinspires.ftc.teamcode.wheelmanager.WheelManager
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Arrays
import java.util.LinkedList

import java8.util.J8Arrays
import org.firstinspires.ftc.teamcode.units.Degree
import org.firstinspires.ftc.teamcode.units.Inches

object testkt {
    @JvmStatic
    fun main(args: Array<String>) {
        val foot = Foot(1.0)
        println("$foot = ${foot.toCM()} = ${foot.toInches()}")
        println(foot - Centimeter(30.48) == Inches(0.0))

        val ninety = Degree(90.0)
        println("$ninety = ${ninety.radians}")
    }
}

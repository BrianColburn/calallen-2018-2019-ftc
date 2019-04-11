package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.teamcode.units.*
                                         /* Foot
                                          * Centimeter
                                          * Inches
                                          * times
                                          * Degree
                                          * Radian
                                          */

object testkt {
    @JvmStatic
    fun main(args: Array<String>) {
        val foot = Foot(1.0)
        println("$foot = ${foot.toCM()} = ${foot.toInches()}")
        println(foot - Centimeter(30.48) == Inches(0.0))
        val footTests = listOf(foot*2, 2*foot, 2.0*foot, 4*foot/2, foot+foot, 3*foot-foot)
        println(footTests.all { it == footTests[0] })

        listOf(0,45,90,179,180,360).forEach {
            val angle = Degree(it.toDouble())
            println("$angle = ${angle.radians}")
        }
        Radian(Math.PI/4).let {
            println("$it = ${it.degrees}")
        }
    }
}

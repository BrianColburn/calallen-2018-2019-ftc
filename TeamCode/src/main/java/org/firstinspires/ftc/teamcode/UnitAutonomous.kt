package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime

import org.firstinspires.ftc.teamcode.units.Foot
import org.firstinspires.ftc.teamcode.wheelmanager.AngleInstruction
import org.firstinspires.ftc.teamcode.wheelmanager.DistanceInstruction
import org.firstinspires.ftc.teamcode.wheelmanager.Instruction
import org.firstinspires.ftc.teamcode.wheelmanager.WheelManager

import java.util.Arrays

import java8.util.Lists
import org.firstinspires.ftc.teamcode.units.Degree

@Autonomous(name = "InstructionAutonomous", group = "WIP")
class UnitAutonomous : LinearOpMode() {
    internal lateinit var wm: WheelManager
    internal var runtime = ElapsedTime()
    var mot: Array<DcMotor?> = arrayOfNulls(4)

    @Throws(InterruptedException::class)
    override fun runOpMode() {
        for (i in 0..3) {
            mot[i] = hardwareMap.tryGet(DcMotor::class.java, "mot$i")
        }
        val instructions = Lists.of(
                DistanceInstruction(Foot(3.0), .4),
                object : Instruction {
                    override fun apply(wm: WheelManager): WheelManager {
                        sleep(1000);
                        return wm
                    }},
                DistanceInstruction(Foot(2.0), .4, DistanceInstruction.DistanceOption.MOVE_TO),
                AngleInstruction(Degree(90.0), .4),
                DistanceInstruction(Foot(2.0), 1.0)
        )
        wm = WheelManager(mot, 8.89 / 2, 37.5, 1160, this, false)
        wm.initializeMotors(intArrayOf(0, 3))
        telemetry.addData("Info", "Waiting for start")
        telemetry.update()
        waitForStart()
        runtime.reset()

        for (instruction in instructions) {
            if (!opModeIsActive()) break
            telemetry.addData("Processing", instruction)
            updateInfo()
            telemetry.update()
            instruction.apply(wm)
        }
        while (opModeIsActive());
    }

    fun updateInfo() {
        telemetry.addData("Runtime", "%.2f", runtime.seconds())
        val pos = wm.polarPos
        telemetry.addData("Position", "%.2f, %.2f", pos[0], pos[1] * 1800 / Math.PI)
        telemetry.addData("CM    ", "(%f)/_(%f)", wm.cm, wm.degrees)
        telemetry.addData("Inches", "(%f)/_(%f)", wm.inches, wm.degrees)
        telemetry.addData("Encoders", "%s", Arrays.toString(wm.encoders))
        telemetry.addData("WM", "%s", wm)
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.units.Unit;

import java.util.List;

import java8.lang.Iterables;

public class UnitAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        List<Unit> units = null;
        WheelManager wm = null;
        waitForStart();

        Iterables.forEach(units, wm::processUnitInstruction);
    }
}

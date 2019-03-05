package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.units.Unit;
import org.firstinspires.ftc.teamcode.wheelmanager.Instruction;

import java.util.ArrayList;
import java.util.List;

import java8.lang.Iterables;
import java8.util.Lists;
import java8.util.Optional;

public class UnitAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Optional<DcMotor>[] mot = new Optional[8];
        for (int i=0;i<8;i++) {
            mot[i] = Optional.ofNullable(hardwareMap.tryGet(DcMotor.class, "mot"+i));
        }
        List<Instruction> instructions =
                Lists.of();
        WheelManager wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1,1160);
        waitForStart();

        Iterables.forEach(instructions, wm::processInstruction);
    }
}

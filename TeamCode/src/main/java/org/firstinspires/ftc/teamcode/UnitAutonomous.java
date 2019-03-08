package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.units.Angle;
import org.firstinspires.ftc.teamcode.units.Foot;
import org.firstinspires.ftc.teamcode.wheelmanager.AngleInstruction;
import org.firstinspires.ftc.teamcode.wheelmanager.DistanceInstruction;
import org.firstinspires.ftc.teamcode.wheelmanager.Instruction;
import org.firstinspires.ftc.teamcode.wheelmanager.WheelManager;

import java.util.Arrays;
import java.util.List;

import java8.lang.Iterables;
import java8.util.Lists;
import java8.util.Optional;

@Autonomous(name = "InstructionAutonomous", group = "WIP")
public class UnitAutonomous extends LinearOpMode {
    static Optional<DcMotor>[] mot = new Optional[4];
    WheelManager wm;
    ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {
        for (int i=0;i<4;i++) {
            mot[i] = Optional.ofNullable(hardwareMap.tryGet(DcMotor.class, "mot"+i));
        }
        List<Instruction> instructions = Lists.of(
                new DistanceInstruction(new Foot(3), .4),
                wm -> {
                    sleep(500);
                    telemetry.addData("Processing", "sleep");
                    return wm;
                },
                new DistanceInstruction(new Foot(2), .4, DistanceInstruction.DistanceOption.MOVE_TO),
                new AngleInstruction(() -> 90, .4),
                new DistanceInstruction(new Foot(2), 1)
                );
        wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1,1160);
        wm.initializeMotors(new int[]{0,3});
        wm.telemetry = Optional.of(telemetry);
        this.wm.lom = this;
        telemetry.addData("Info","Waiting for start");
        telemetry.update();
        waitForStart();
        runtime.reset();

        for (Instruction instruction : instructions) {
            if (!opModeIsActive()) break;
            telemetry.addData("Processing", instruction);
            updateInfo();
            telemetry.update();
            instruction.apply(wm);
        }
        while (opModeIsActive());
    }

    public void updateInfo() {
        telemetry.addData( "Runtime", "%.2f", runtime.seconds());
        double[] pos = wm.getPolPos();
        telemetry.addData( "Position","%.2f, %.2f", pos[0], pos[1]*1800/Math.PI);
        telemetry.addData( "CM    ", "(%f)/_(%f)", wm.getCM(),wm.getDegrees());
        telemetry.addData( "Inches", "(%f)/_(%f)", wm.getInches(),wm.getDegrees());
        telemetry.addData("Encoders", "%s", Arrays.toString(wm.getEncoders()));
        telemetry.addData( "WM","%s",wm);
    }
}

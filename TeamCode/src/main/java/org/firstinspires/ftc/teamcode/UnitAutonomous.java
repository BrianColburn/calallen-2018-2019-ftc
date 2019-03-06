package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

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

    public static void main(String[] args) {
        for (int i=0;i<4;i++) {
            mot[i] = Optional.ofNullable(new DcMotor() {
                @Override
                public MotorConfigurationType getMotorType() {
                    return null;
                }

                @Override
                public void setMotorType(MotorConfigurationType motorType) {

                }

                @Override
                public DcMotorController getController() {
                    return null;
                }

                @Override
                public int getPortNumber() {
                    return 0;
                }

                @Override
                public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {

                }

                @Override
                public ZeroPowerBehavior getZeroPowerBehavior() {
                    return null;
                }

                @Override
                public void setPowerFloat() {

                }

                @Override
                public boolean getPowerFloat() {
                    return false;
                }

                @Override
                public void setTargetPosition(int position) {

                }

                @Override
                public int getTargetPosition() {
                    return 0;
                }

                @Override
                public boolean isBusy() {
                    return false;
                }

                @Override
                public int getCurrentPosition() {
                    return 0;
                }

                @Override
                public void setMode(RunMode mode) {

                }

                @Override
                public RunMode getMode() {
                    return null;
                }

                @Override
                public void setDirection(Direction direction) {

                }

                @Override
                public Direction getDirection() {
                    return null;
                }

                @Override
                public void setPower(double power) {

                }

                @Override
                public double getPower() {
                    return 0;
                }

                @Override
                public Manufacturer getManufacturer() {
                    return null;
                }

                @Override
                public String getDeviceName() {
                    return null;
                }

                @Override
                public String getConnectionInfo() {
                    return null;
                }

                @Override
                public int getVersion() {
                    return 0;
                }

                @Override
                public void resetDeviceConfigurationForOpMode() {

                }

                @Override
                public void close() {

                }
            });
        }
        System.out.println(Arrays.toString(mot));
        List<Instruction> instructions = Lists.of(
                new DistanceInstruction(new Foot(3), .4),
                wm -> {
                    //sleep(500);
                    System.out.println("Sleep");
                    return wm;
                },
                new DistanceInstruction(new Foot(2), .4, DistanceInstruction.DistanceOption.MOVE_TO),
                new AngleInstruction((Angle) () -> 90, .4),
                new DistanceInstruction(new Foot(2), 1)
        );
        WheelManager wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1,1160);

        Iterables.forEach(instructions, wm::processInstruction);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        for (int i=0;i<4;i++) {
            mot[i] = Optional.ofNullable(new DcMotor() {
                @Override
                public MotorConfigurationType getMotorType() {
                    return null;
                }

                @Override
                public void setMotorType(MotorConfigurationType motorType) {

                }

                @Override
                public DcMotorController getController() {
                    return null;
                }

                @Override
                public int getPortNumber() {
                    return 0;
                }

                @Override
                public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {

                }

                @Override
                public ZeroPowerBehavior getZeroPowerBehavior() {
                    return null;
                }

                @Override
                public void setPowerFloat() {

                }

                @Override
                public boolean getPowerFloat() {
                    return false;
                }

                @Override
                public void setTargetPosition(int position) {

                }

                @Override
                public int getTargetPosition() {
                    return 0;
                }

                @Override
                public boolean isBusy() {
                    return false;
                }

                @Override
                public int getCurrentPosition() {
                    return 0;
                }

                @Override
                public void setMode(RunMode mode) {

                }

                @Override
                public RunMode getMode() {
                    return null;
                }

                @Override
                public void setDirection(Direction direction) {

                }

                @Override
                public Direction getDirection() {
                    return null;
                }

                @Override
                public void setPower(double power) {

                }

                @Override
                public double getPower() {
                    return 0;
                }

                @Override
                public Manufacturer getManufacturer() {
                    return null;
                }

                @Override
                public String getDeviceName() {
                    return null;
                }

                @Override
                public String getConnectionInfo() {
                    return null;
                }

                @Override
                public int getVersion() {
                    return 0;
                }

                @Override
                public void resetDeviceConfigurationForOpMode() {

                }

                @Override
                public void close() {

                }
            });
        }
        List<Instruction> instructions = Lists.of(
                new DistanceInstruction(new Foot(3), .4),
                wm -> {
                    sleep(500);
                    return wm;
                },
                new DistanceInstruction(new Foot(2), .4, DistanceInstruction.DistanceOption.MOVE_TO),
                new AngleInstruction(() -> 90, .4),
                new DistanceInstruction(new Foot(2), 1)
                );
        WheelManager wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1,1160);
        telemetry.addLine("Waiting for start");
        waitForStart();

        for (Instruction instruction : instructions) {
            telemetry.addData("Processing", instruction);
            instruction.apply(wm);
        }
    }
}

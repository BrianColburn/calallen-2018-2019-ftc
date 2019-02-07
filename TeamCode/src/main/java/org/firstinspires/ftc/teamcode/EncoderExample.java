package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "EncoderExample", group = "Test")
public class EncoderExample extends LinearOpMode {
    public void runOpMode() throws InterruptedException {
        DcMotor mot = hardwareMap.get(DcMotor.class, "mot0"); //Configures motors
        mot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //Resets encoders
        while(mot.getCurrentPosition() != 0) { //Ensures encoders are zero
            mot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        mot.setMode(DcMotor.RunMode.RUN_TO_POSITION); //Sets mode to use encoders
        mot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        waitForStart();
        mot.setTargetPosition(1160); //Sets motor to move 1440 ticks (1440 is one rotation for Tetrix motors)
        mot.setPower(.4);
        while(opModeIsActive()) { //While target has not been reached
        }
    }
}
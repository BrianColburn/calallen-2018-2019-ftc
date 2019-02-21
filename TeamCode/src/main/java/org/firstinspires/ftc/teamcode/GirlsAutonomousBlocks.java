package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "girlsautonomous (Blocks to Java)", group = "")
public class GirlsAutonomousBlocks extends LinearOpMode {

  private DcMotor mot1;
  private DcMotor mot2;
  private DcMotor mot0;
  private DcMotor mot3;
  private Servo ser0;
  private Servo ser1;

  /**
   * This function is executed when this Op Mode is selected from the Driver Station.
   */
  @Override
  public void runOpMode() {
    mot1 = hardwareMap.dcMotor.get("mot1");
    mot2 = hardwareMap.dcMotor.get("mot2");
    mot0 = hardwareMap.dcMotor.get("mot0");
    mot3 = hardwareMap.dcMotor.get("mot3");
    ser0 = hardwareMap.servo.get("ser0");
    ser1 = hardwareMap.servo.get("ser1");

    // Put initialization blocks here.
    mot1.setDirection(DcMotorSimple.Direction.REVERSE);
    mot2.setDirection(DcMotorSimple.Direction.REVERSE);
    waitForStart();
    if (opModeIsActive()) {
      // Put run blocks here.
      mot0.setPower(-1);
      mot1.setPower(-1);
      mot3.setPower(-0.3);
      mot2.setPower(-0.2);
      sleep(1750);
      mot3.setPower(0);
      mot2.setPower(0);
      mot0.setPower(0);
      mot1.setPower(0);
      ser0.setPosition(0.95);
      ser1.setPosition(0.95);
      sleep(1000);
      mot3.setPower(0.25);
      mot2.setPower(0.15);
      mot0.setPower(0.1);
      mot1.setPower(0.1);
      sleep(1000);
      mot3.setPower(6);
      mot2.setPower(0);
      mot0.setPower(5);
      mot1.setPower(-1.5);
      ser0.setPosition(-1);
      ser1.setPosition(0.95);
      sleep(1000);
      mot3.setPower(-0.3);
      mot2.setPower(-0.2);
      mot0.setPower(-1);
      mot1.setPower(-1);
      sleep(1780);
      mot3.setPower(0);
      mot2.setPower(0);
      mot0.setPower(0);
      mot1.setPower(0);
      ser0.setPosition(1);
      ser1.setPosition(0.95);
      sleep(1000);
    }
  }
}

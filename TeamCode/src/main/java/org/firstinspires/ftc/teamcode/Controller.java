package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

public abstract class Controller {
    Gamepad g1;
    Gamepad g2;

    public Controller(Gamepad g1, Gamepad g2) {
        this.g1 = g1;
        this.g2 = g2;
    }

    public double[] wheel_power() {

        double drive = -g1.right_stick_y;
        double turn  =  g1.right_stick_x;
        double leftPower    = Range.clip(drive + turn, -1.0, 1.0) ;
        double rightPower   = Range.clip(drive - turn, -1.0, 1.0) ;
        return new double[] {leftPower, rightPower};
    }

    public abstract double lift_power();
    public abstract double servo_pos();

    public abstract double servo2_pos();

    public void update_state(){}
}

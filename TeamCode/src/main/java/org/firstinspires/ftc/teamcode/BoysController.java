package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class BoysController extends Controller {

    public BoysController(Gamepad g1, Gamepad g2) {
        super(g1, g2);
    }

    @Override
    public double lift_power() {
        return Math.abs(g1.left_stick_y) > .01 ? g1.left_stick_y : g2.left_stick_y;
    }

    @Override
    public double servo_pos() {
        return 1-Math.max(g1.right_trigger > 0 ? g1.right_trigger : g2.right_trigger, 1/180.);
    }

    @Override
    public double servo2_pos() {
        return Math.max(g1.left_trigger > 0 ? g1.left_trigger : g2.left_trigger, 0);
    }
}

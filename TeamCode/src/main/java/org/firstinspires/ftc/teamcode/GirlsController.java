package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

public class GirlsController extends Controller {
    enum State {
        Normal, Chucking
    }

    private double servo_pos = 0;
    private State  state     = State.Normal;
    private long   stateTime = 0;

    public GirlsController(Gamepad g1, Gamepad g2) {
        super(g1, g2);
    }

    @Override
    public double lift_power() {
        return !(g1.dpad_down || g1.dpad_up) ? g1.left_stick_y : (g1.dpad_down?1:-1);
    }

    @Override
    public double servo_pos() {
        servo_pos = Math.min(Math.max(servo_pos + g2.left_stick_y/180,0),1);
        return servo_pos;
    }

    @Override
    public double servo2_pos() {
        double p = 1-g2.right_trigger;
        if (g2.left_stick_y < 0 &&
            servo_pos() <= 170 &&
            g2.right_trigger > 0) return 1-servo_pos();
        else return p;
    }

    private void change_state(State s) {
        state = s;
        stateTime = System.currentTimeMillis()/1000;
    }

    @Override
    public void update_state() {
        switch (state) {
            case Normal: {
                if (g2.dpad_up) {
                    change_state(State.Chucking);
                }
                break;
            }
            case Chucking: {
                if (System.currentTimeMillis()/1000 >= stateTime + 10) {
                    change_state(State.Normal);
                }
                break;
            }
            default:
                throw new IllegalStateException("No idea what state I should be in!");
        }
    }
}

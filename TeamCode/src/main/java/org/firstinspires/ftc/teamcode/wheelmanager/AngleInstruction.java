package org.firstinspires.ftc.teamcode.wheelmanager;

import org.firstinspires.ftc.teamcode.units.Angle;

public class AngleInstruction implements Instruction {
    private final Angle theta;
    private final double power;
    private final AngleOption option;

    public enum AngleOption {TURN_ANOTHER, TURN_TO}

    public AngleInstruction(Angle theta, double power, AngleOption option) {
        this.theta = theta;
        this.power = power;
        this.option = option;
    }

    public AngleInstruction(Angle theta, double power) {
        this(theta, power, AngleOption.TURN_ANOTHER);
    }

    @Override
    public WheelManager apply(WheelManager wm) {
        switch (option) {
            case TURN_ANOTHER:
                wm.turnAnother(theta, power);
                break;
            case TURN_TO:
                wm.turnTo(theta, power);
                break;
        }
        return wm;
    }

    @Override
    public String toString() {
        return "AngleInstruction";
    }
}

package org.firstinspires.ftc.teamcode.wheelmanager;

import org.firstinspires.ftc.teamcode.units.Distance;

public class DistanceInstruction implements Instruction {
    private final Distance dist;
    private final double power;
    private final DistanceOption option;

    public enum DistanceOption {MOVE_ANOTHER, MOVE_TO}

    public DistanceInstruction(Distance dist, double power, DistanceOption option) {
        this.dist = dist;
        this.power = power;
        this.option = option;
    }

    public DistanceInstruction(Distance dist, double power) {
        this(dist, power, DistanceOption.MOVE_ANOTHER);
    }

    @Override
    public WheelManager apply(WheelManager wm) {
        switch (option) {
            case MOVE_ANOTHER:
                wm.moveAnother(dist, power);
                break;
            case MOVE_TO:
                wm.moveTo(dist, power);
                break;
        }
        return wm;
    }

    @Override
    public String toString() {
        return "DistanceInstruction";
    }
}

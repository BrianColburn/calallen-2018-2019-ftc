package org.firstinspires.ftc.teamcode.units;

public interface Angle extends Unit{
    public double getDegrees();

    default double abs() {
        return Math.abs(getDegrees());
    }

    default double signum() {
        return Math.signum(getDegrees());
    }
}

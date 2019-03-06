package org.firstinspires.ftc.teamcode.wheelmanager;

public class WheelManagerSnapshot {
    public final double time;
    public final int[] encoderPositions;

    public WheelManagerSnapshot(double time, int[] encoderPositions) {
        this.time = time;
        this.encoderPositions = encoderPositions;
    }
}

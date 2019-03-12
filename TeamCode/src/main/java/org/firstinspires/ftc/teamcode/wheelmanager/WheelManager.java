package org.firstinspires.ftc.teamcode.wheelmanager;

import org.firstinspires.ftc.teamcode.units.Angle;
import org.firstinspires.ftc.teamcode.units.Distance;

public interface WheelManager {
    void initializeMotors(int[] motsToReverse);

    void setPower(double l, double r);

    int[] getEncoders();

    void moveAnother(Distance distanceToMove, double power);

    void moveTo(Distance pos, double power);

    void turnAnother(Angle angleToRotate, double power);

    void turnTo(Angle theta, double power);

    void processInstruction(Instruction instruction);

    double getDegrees();

    double getInches();

    double getCM();

    double[] getPolPos();

    double[] getCartPos();
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Autonomous (Crater)", group="Girls")
public class AutoCrater extends AutonomousProper {
    @Override
    public void init() {
        super.init();
        postHang = State.CRATER;
    }
}

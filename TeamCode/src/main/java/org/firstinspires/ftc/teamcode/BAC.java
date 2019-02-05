package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Boys Autonomous (Crater)", group="Boys")
public class BAC extends BoysAutonomous {
    @Override
    public void init() {
        super.init();
        postHang = State.CRATER;
    }
}

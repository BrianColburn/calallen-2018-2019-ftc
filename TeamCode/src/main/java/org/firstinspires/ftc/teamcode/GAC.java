package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Girls Autonomous (Crater)", group="Girls")
public class GAC extends GirlsAutonomous {
    @Override
    public void init() {
        super.init();
        postHang = State.CRATER;
    }
}

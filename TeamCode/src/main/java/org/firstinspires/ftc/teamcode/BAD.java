package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Boys Autonomous (Depot)", group="Boys")
public class BAD extends BoysAutonomous {
    @Override
    public void init() {
        super.init();
        postHang = State.TOKEN;
    }
}

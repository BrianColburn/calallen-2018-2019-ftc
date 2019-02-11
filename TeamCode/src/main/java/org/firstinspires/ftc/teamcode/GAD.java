package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Girls Autonomous (Depot)", group="Girls")
public class GAD extends GirlsAutonomous {
    @Override
    public void init() {
        super.init();
        postHang = State.TOKEN;
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Autonomous (Depot)", group="Girls")
public class AutoDepot extends AutonomousProper {
    @Override
    public void init() {
        super.init();
        postHang = State.TOKEN;
    }
}

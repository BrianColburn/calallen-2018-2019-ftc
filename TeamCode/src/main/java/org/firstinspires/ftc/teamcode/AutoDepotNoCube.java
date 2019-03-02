package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Autonomous (Depot NO CUBE)", group="Girls")
public class AutoDepotNoCube extends AutonomousProper {
    @Override
    public void init() {
        super.init();
        postHang = State.TOKEN;
        noCube = true;
    }

    @Override
    public void craterLoop() {
        changeState(State.OFF);
    }
}

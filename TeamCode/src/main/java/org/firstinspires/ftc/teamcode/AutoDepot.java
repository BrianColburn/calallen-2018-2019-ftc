package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="Autonomous (Depot DOES CUBE)", group="Girls")
public class AutoDepot extends AutonomousProper {
    @Override
    public void init() {
        super.init();
        postHang = State.TOKEN;
    }

    @Override
    public void craterLoop() {
        changeState(State.OFF);
    }
}

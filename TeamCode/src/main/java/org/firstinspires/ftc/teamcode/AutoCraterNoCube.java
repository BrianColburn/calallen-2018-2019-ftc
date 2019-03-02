package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.units.Foot;

@Autonomous(name="Autonomous (Crater NO CUBE)", group="Girls")
public class AutoCraterNoCube extends AutoCrater {
    @Override
    public void init() {
        super.init();
        noCube = true;
    }

    @Override
    public void craterLoop() {
        wm.setPower(1,1);
        id = wm.callAfter(new Foot(4), id, new Runnable() {
            @Override
            public void run() {
                wm.setPower(0,0);
                changeState(State.OFF);
                logger.info("Changed state");
            }
        });
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.units.Centimeter;
import org.firstinspires.ftc.teamcode.units.Foot;

@Autonomous(name="Autonomous (Crater DOES CUBE)", group="Girls")
public class AutoCrater extends AutonomousProper {
    @Override
    public void init() {
        super.init();
        postHang = State.CRATER;
    }

    @Override
    public void craterLoop() {
        if (Math.abs(wm.getDegrees()) > 5) {
            wm.setPower(-Math.signum(wm.getDegrees())*.4, Math.signum(wm.getDegrees())*.4);
        } else {
            wm.setPower(1, 1);
            id = wm.callAfter(new Centimeter(wm.getCM()-new Foot(4).toCM()), id, new Runnable() {
                @Override
                public void run() {
                    wm.setPower(0, 0);
                    changeState(State.OFF);
                    logger.info("Changed state");
                }
            });
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.Event;
import org.firstinspires.ftc.robotcore.external.StateTransition;
import org.firstinspires.ftc.teamcode.state.Events;
import static org.firstinspires.ftc.teamcode.state.Events.*;
import org.firstinspires.ftc.teamcode.state.ExtendedStateMachine;
import org.firstinspires.ftc.teamcode.state.States;
import static org.firstinspires.ftc.teamcode.state.States.*;

public abstract class AbstractAutonomous extends LinearOpMode {
    ExtendedStateMachine sm = new ExtendedStateMachine();
    @Override
    public void runOpMode() {
        // initialize
        sm.addState(OFF);
        sm.addTransition(new StateTransition(OFF, BeginDeployment, HANGING));
            sm.addTransition(new StateTransition(HANGING, DeployedToDepot, DEPOT));
            sm.addTransition(new StateTransition(DEPOT, DropOffToken, TOKEN));
            sm.addTransition(new StateTransition(TOKEN, TokenDroppedOff, DEPOT));
            sm.addTransition(new StateTransition(DEPOT, DepotToCrater, CRATER));
            sm.addTransition(new StateTransition(DEPOT, ParkedInDepot, OFF));
        sm.addTransition(new StateTransition(HANGING, DeployedToCrater, CRATER));

        while (!isStarted()) {
            // init loop
        }
        waitForStart();

        while (opModeIsActive()) {

        }
    }
}

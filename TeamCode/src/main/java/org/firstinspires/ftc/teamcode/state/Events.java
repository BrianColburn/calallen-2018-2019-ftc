package org.firstinspires.ftc.teamcode.state;

import org.firstinspires.ftc.robotcore.external.Event;

public enum Events implements Event {
    BeginDeployment,
    DeployedToDepot,
    DeployedToCrater,
    CraterToDepot,
    DepotToCrater,
    DropOffToken,
    TokenDroppedOff,
    FindCube,
    ParkedInCrater,
    ParkedInDepot
    ;

    @Override
    public String getName() {
        return name();
    }
}

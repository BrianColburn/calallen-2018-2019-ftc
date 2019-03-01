package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.units.Centimeter;
import org.firstinspires.ftc.teamcode.units.Foot;

import java.util.logging.Handler;


//@Autonomous(name="Boys Autonomous", group="Boys")

public class AutonomousProper extends AbstractAutonomous
{
    long id = 0;

    @Override
    public void init() {
        super.init();
        telemetry.addData("Status", "Autonomous (Proper)");

        detector.alignPosOffset = 100; // How far from center frame to offset the alignment zone.

        //servo.scaleRange(0,.8);
        ser[0].setPosition(180/180.);

        wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1,1160);
        wm.logger = logger;
    }


    @Override
    public void loop() {
        updateInfo();
        switch (state) {
            //region State: Hanging
            case HANGING: {
                /*if (runtime.seconds() <= 10) {
                    mot[4].setPower(-1);
                } else if (getRuntime() > 11) {
                    //changeState(State.CUBE);
                    changeState(postHang);
                    mot[4].setPower(0);
                }*/
                changeState(State.CUBE);
                //changeState(postHang);
                break;
            }
            //endregion
            //region State: Token
            case TOKEN: {
                if (wm.getInches() > 50 && wm.getInches() < 60)
                { // Stop moving and change states
                    if (!direction) {
                        changeState(State.DEPOT);
                        wm.setPower(0, 0);
                    } else {
                        ser[0].setPosition(0);
                    }
                } else if (wm.getInches() > 45 && wm.getInches() < 55)
                { // Drop the token
                    if (!direction) {
                        ser[0].setPosition(0);
                    } else {
                        changeState(State.DEPOT);
                        wm.setPower(0, 0);
                    }
                } else if (wm.getInches() > 60) {
                    wm.setPower(-.4,-.4);
                    direction = true;
                } else { // Keep moving forward
                    wm.setPower(.4,.4);
                    break;
                }
                break;
            }
            //endregion
            //region State: Depot
            case DEPOT: {
                // We need to rotate to face the crater
                if (Math.abs(wm.getDegrees()) <= 160) {
                    wm.setPower(-.4,.4);
                }
                // We are facing the crater
                else if (Math.abs(wm.getDegrees()) > 19) {
                    changeState(State.TRANSIENT);
                }
                break;
            }
            //endregion
            //region State: Transient
            case TRANSIENT: {
                switch (stateHistory.get(1)) {
                    case DEPOT: {
                        /*if (wm.moveAnother(new Foot(9), stateIterations)) {
                            wm.setPower(1, 1);
                        } else {
                            wm.setPower(0,0);
                            changeState(State.CRATER);
                        }*/
                        logger.info("callAfter");
                        wm.setPower(1,1);
                        id = wm.callAfter(new Foot(9), id, new Runnable() {
                            @Override
                            public void run() {
                                wm.setPower(0,0);
                                changeState(State.CRATER);
                                logger.info("Changed state");
                            }
                        });
                        break;
                    }
                    case CRATER: {
                        // head to the depot
                        break;
                    }
                    default: {
                        throw new IllegalStateException("The state `TRANSIENT' cannot follow from the state " + stateHistory.get(1));
                    }
                }
                break;
            }
            //endregion
            //region State: Cube
            case CUBE: {
                if (detector.isFound()) {
                    if (detector.getAligned() != 0) {
                        wm.setPower(detector.getAligned() * .125, -detector.getAligned() * .125);
                    /*for (DcMotor m : mot) {
                        m.setPower(detector.getAligned() * .125);
                    }*/
                    } else {
                        int ix = 2;
                        int margin = 1;
                        int target = 30;
                        if ((detector.getDistances()[ix] + margin) / target > 1 && (detector.getDistances()[ix] - margin) / target > 1) {
                            wm.setPower(.125, .125);
                        /*mot[0].setPower(-.125);
                        mot[3].setPower(-.125);
                        mot[1].setPower(.125);
                        mot[2].setPower(.125);*/
                            telemetry.addLine("Forward!");
                        } else if ((detector.getDistances()[ix] + margin) / target < 1 && (detector.getDistances()[ix] - margin) / target < 1) {
                            wm.setPower(-.125, -.125);
                        /*mot[0].setPower(.125);
                        mot[3].setPower(.125);
                        mot[1].setPower(-.125);
                        mot[2].setPower(-.125);*/
                            telemetry.addLine("Back!");
                        } else {
                            wm.setPower(0, 0);
                        /*for (DcMotor m : mot) {
                            m.setPower(0);
                        }*/
                            telemetry.addLine("Stop!");
                            ser[1].setPosition(0 / 180.);
                            ser[0].setPosition(1);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            double dist = detector.getDistances()[ix];
                            wm.setPower(.4, .4);
                            /*mot[0].setPower(-.4);//125);
                            mot[3].setPower(-.4);//125);
                            mot[1].setPower(.4);//125);
                            mot[2].setPower(.4);//125);*/
                            long oldID = id;
                            while (oldID==id) {
                                id = wm.callAfter(new Centimeter(2*dist), id, null);
                            }

                            ser[0].setPosition(1);
                            ser[1].setPosition(0);
                            wm.setPower(0,0);
                            changeState(postHang);
                        }
                    }
                } else if (runtime.seconds() > 25) { // We don't see the cube
                    wm.setPower(.4, .4);
                /*mot[0].setPower(-.4);//125);
                mot[3].setPower(-.4);//125);
                mot[1].setPower(.4);//125);
                mot[2].setPower(.4);//125);*/
                } else { // We don't see the cube
                /*for (DcMotor m : mot) {
                    m.setPower(.125);
                }*/
                    if (wm.getPolPos()[1] * 1800 / Math.PI > 20 && !direction) {
                        wm.setPower(-.125, .125);
                    } else {
                        wm.setPower(.125, -.125);
                    }
                }
                break;
            }
            //endregion
            //region State: Crater
            case CRATER: {
                // We still need to drop off the token
                if (!stateHistory.contains(State.TOKEN)) {
                    if (stateIterations == 0) {
                        angleOffset = wm.getDegrees();
                        wm.setPower(.4,-.4);
                    }
                    if (Math.abs(wm.getDegrees())-angleOffset >= 90) {
                        wm.setPower(0,0);
                        changeState(State.TRANSIENT);
                    }
                } else {
                    changeState(State.OFF);
                }
                break;
            }
            //endregion
            //region State: Off
            case OFF: {
                if (stateIterations == 0) {
                    stop();
                }
                break;
            }
            //endregion
            //region State: !!!!
            default: {
                throw new IllegalStateException("Illegal state: " + state);
            }
            //endregion
        }
        stateIterations++;
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
        detector.disable();
        wm.setPower(0,0);
        ser[0].setPosition(1);
        ser[1].setPosition(0);
        //servo.setPosition(15/180);
        for (Handler h : logger.getHandlers()) {
            h.close();
        }
    }

}

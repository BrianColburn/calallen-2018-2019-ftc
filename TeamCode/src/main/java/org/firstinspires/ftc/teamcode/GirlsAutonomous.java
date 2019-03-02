package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


//@Autonomous(name="GirlsAutonomous", group ="Girls")
public class GirlsAutonomous extends AbstractAutonomous {

    //region Gold Detector and Motor variables
    private static final double ACHE = 20/135;
    //endregion

    private boolean attack = false;


    @Override
    public void init() {
        telemetry.addData("Status", "Girls Autonomous");
        super.init();

        //region Initialize motors and servos
        ser[0].setPosition(0);

        wm = new WheelManager(mot, 10/2, (2*Math.PI)/2.5, 37.5, 1, 1120);
        //endregion
    }


    @Override
    public void loop() {
        updateInfo();
        switch (state) {
            //region State: Hanging
            case HANGING: {
                changeState(postHang);
                break;
            }
            //endregion
            //region State: Token
            case TOKEN: {
                if (wm.getInches() > 35)
                { // Stop moving and change states
                    changeState(State.DEPOT);
                    wm.setPower(0,0);
                } else if (wm.getInches() > 30)
                { // Drop the token
                    ser[0].setPosition(1);
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
                if (wm.getDegrees() <= 135*ACHE) {
                    wm.setPower(-.4,.4);
                }
                // We are facing the crater
                else if (wm.getDegrees() > 19) {
                    changeState(State.TRANSIENT);
                }
                break;
            }
            //endregion
            //region State: Transient
            case TRANSIENT: {
                switch (stateHistory.get(1)) {
                    case DEPOT: {
                        if (wm.getPolPos()[0] < 350) {
                            wm.setPower(1, 1);
                        } else {
                            wm.setPower(0,0);
                            changeState(State.CRATER);
                        }
                        break;
                    }
                    case CRATER: {
                        // head to the depot
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                break;
            }
            //endregion
            //region State: Cube
            case CUBE: {
                if (detector.isFound()) { // We found a cube (give or take)
                    int ix = 2;
                    int margin = 1;
                    int target = 30;

                    if (detector.getAligned() != 0)
                    { // We need to line up our approach
                        telemetry.addData("Action", "turn " + detector.getAligned());
                        wm.setPower(detector.getAligned()*.125, -detector.getAligned()*.125);
                    } else if (
                                ((detector.getDistances()[ix] + margin) / target > 1 &&
                                    (detector.getDistances()[ix] - margin) / target > 1) ||
                                attack
                            )
                    { // The cube is at least `target' cm away
                        // Or we are charging at it
                        telemetry.addData("Action","Towards the Cube!");
                        wm.setPower(-.125,-.125); // Girl's camera is on the back, so we backup.
                        attack = true;
                    } else if (
                            ((detector.getDistances()[ix] + margin) / target < 1 &&
                                    (detector.getDistances()[ix] - margin) / target < 1) &&
                                    !attack
                            )
                    { // The cube is closer than `target' cm
                        // And we are not charging at it
                        telemetry.addData("Action", "Away!");
                        wm.setPower(.125,.125);
                    } else { // Boys, we gottem.
                        wm.setPower(0,0);
                        telemetry.addData("Action", "CHARGE!");
                        ser[0].setPosition(180 / 180.);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        wm.setPower(-.4,-.4);

                        //while (detector.isFound());
                        try {
                            Thread.sleep(1200);
                            ser[0].setPosition(0);
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // Lost the cube
                    if (attack) { // Probably lost the cube because we ran over it
                        telemetry.addData("Action", "Keep Calm, and CHARGE!!!");
                    } else { // Lost the cube for any other reason
                        telemetry.addData("Action", "Panic");
                        if (wm.getPolPos()[1]*1800/Math.PI > 20 && !direction) {
                            wm.setPower(.125,-.125);
                        } else {
                            direction = true;
                            wm.setPower(-.125,.125);
                        }
                    }
                }
                break;
            }
            //endregion
            //region State: Crater
            case CRATER: {
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
        wm.setPower(0,0);
        ser[0].setPosition((15)/180);
        detector.disable();
    }

}

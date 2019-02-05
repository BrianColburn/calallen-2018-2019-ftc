package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


@Autonomous(name="Boys Autonomous", group="Boys")

public class BoysAutonomous extends AbstractAutonomous
{
    @Override
    public void init() {
        telemetry.addData("Status", "Gold Align Example");
        super.init();

        detector.alignPosOffset = 100; // How far from center frame to offset the alignment zone.

        //servo.scaleRange(0,.8);
        servo.setPosition(180/180.);

        wm = new WheelManager(mot, 8.89/2, 15.24/4.445, 37.5, 1);
    }


    @Override
    public void loop() {
        updateInfo();
        switch (state) {
            //region State: Hanging
            case HANGING: {
                if (runtime.seconds() <= 10) {
                    mot[4].setPower(-1);
                } else if (getRuntime() > 11) {
                    changeState(State.CUBE);
                    mot[4].setPower(0);
                }
                break;
            }
            //endregion
            //region State: Token
            case TOKEN: {
                if (wm.getPolPos()[0] > 40)
                { // Stop moving and change states
                    changeState(State.DEPOT);
                    wm.setPower(0,0);
                } else if (wm.getPolPos()[0] > 35)
                { // Drop the token
                    servo.setPosition(1);
                } else { // Keep moving forward
                    wm.setPower(.4,.4);
                    break;
                }
                break;
            }
            //endregion
            //region State: Depot
            case DEPOT: {
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
                            servo.setPosition(0 / 180.);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            wm.setPower(.4, .4);
                            /*mot[0].setPower(-.4);//125);
                            mot[3].setPower(-.4);//125);
                            mot[1].setPower(.4);//125);
                            mot[2].setPower(.4);//125);*/

                            //while (detector.isFound());
                            try {
                                Thread.sleep(1200);
                                servo.setPosition(1);
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

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
        servo.setPosition(1);
        //servo.setPosition(15/180);
    }

}

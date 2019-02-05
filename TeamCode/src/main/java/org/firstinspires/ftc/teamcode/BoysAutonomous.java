/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

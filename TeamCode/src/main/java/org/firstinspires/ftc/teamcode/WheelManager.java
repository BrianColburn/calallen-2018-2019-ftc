package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import java.util.ArrayList;

/**
 * Created by Brian Colburn
 */
public class WheelManager {
    private DcMotor[] mot;
    private double radius;
    private double theta;
    private double dist;
    private double omega;
    private double left;
    private double right;
    private double time;
    private double scale;
    private double[] pos;
    private double axle;
    private int[] encoders;
    private int[] lastEncoding;
    private final int ticks;
    private int[] previousPositions;

    /**
     * The WheelManager class is a rudimentary dead-reckoning positioning system.
     * It uses the wheel speed to track the position of the robot.
     *
     * @param mot     an array of motors to control
     * @param radius  the radius of the wheels attached to the motors
     * @param omega   the radial velocity of the wheels at speed scale
     * @param scale   scaling factor
     */
    public WheelManager(DcMotor[] mot, double radius, double omega, double axle, double scale, final int ticks) {
        this.mot    = mot;
        this.encoders = new int[mot.length];
        this.lastEncoding = new int[encoders.length];
        this.radius = radius;
        this.theta  = 0;
        this.dist   = 0;
        this.omega  = omega;
        this.left   = 0;
        this.right  = 0;
        this.scale  = scale;
        this.time   = System.currentTimeMillis()/1000.;
        this.pos    = new double[] {0,0,0};
        this.axle   = axle;
        this.ticks  = ticks;
        this.previousPositions = new int[mot.length];
    }

    /**
     * Calculate the change in the orientation of the robot
     * @param t the duration of the rotation
     * @param D either -1, 0, or 1
     * @return the angle of the arc traced by the robot
     */
    private double w(double t, double D) {
        //return -D*omega*(radius/vradius)*t;
        //System.out.printf("Old theta: %.2f, D: %.2f, t: %.2f, new theta: %.2f%n",theta,D,t,theta + D*t);
        return D*t;
    }

    /**
     * calculate the change in the distance that the robot has traveled
     * @param t the duration of the movement
     * @param D either -1, 0, or 1
     * @return the distance traversed
     */
    private double d(double t, double D) {
        //System.out.printf("Old dist: %.2f, D: %.2f, t: %.2f, new dist: %.2f%n",dist,D,t,dist + D*t);
        if (ticks < 0) {
            // (unit-less) * ((length/time)/time) * (length) * (time)
            // (unit-less) * (length/time) * (length)
            return D * omega * radius * t;
        } else {
            // (unit-less) * (length) / (time) * (length)
            // `dS = r/2 (dRotsR + dRotsL)`
            // `dTheta = r/d (dRotsR - dRotsL)`
            return radius/2 * (((double)mot[1].getCurrentPosition() - previousPositions[1])/ticks + ((double)mot[3].getCurrentPosition() - previousPositions[3])/ticks);
        }
    }

    public void setPower(double l, double r) {
        if (l != left || r != right) {
            for (int i = 0; i < 4; i++) if (mot[i] != null) mot[i].setPower(0);
            pos = getCartPos();
            double[] dt = getPolPos();
            dist = dt[0];
            theta = dt[1];
            time = System.currentTimeMillis() / 1000.;
            left = l * scale;
            right = r * scale;
            for (int i = 0; i < 4; i++) {
                if (mot[i] != null) {
                    previousPositions[i] = mot[i].getCurrentPosition();
                    mot[i].setPower(i==1||i==2?left:right);
                }
                /*mot[0].setPower(right);
                mot[1].setPower(left);
                mot[2].setPower(left);
                mot[3].setPower(right);*/
            }
        }
    }

    public double[] getICCPos() {
        return new double[] {
                pos[0] - axle/2 * (right + left)/(right - left) * Math.sin(theta),
                pos[1] + axle/2 * (right + left)/(right - left) * Math.cos(theta)
        };
    }

    public double getDegrees() {
        return getPolPos()[1]*1800/Math.PI;
    }

    public double getInches() {
        return 10/3.533482*getPolPos()[0];
    }

    public double getCM() {
        return 25.4/3.533482*getPolPos()[0];
    }

    public double[] getPolPos() {
        double newTime = System.currentTimeMillis()/1000.;

        return new double[] {
                dist  + d(newTime - time, (right + left)/scale),
                theta + w(newTime - time, (right - left)/(scale*axle))
        };
    }

    public double[] getCartPos() {
        double[] icc = getICCPos();
        double newTime = System.currentTimeMillis()/1000;
        double x = Double.isNaN(icc[0]) ? pos[0] : (pos[0]-icc[0]) * Math.cos(w(newTime - time, (right - left)/(scale*axle)))
                - (pos[1]-icc[1]) * Math.sin(w(newTime - time, (right - left)/(scale*axle)))
                + icc[0];
        double y = Double.isNaN(icc[0]) ? pos[1] : (pos[0]-icc[0]) * Math.sin(w(newTime - time, (right - left)/(scale*axle)))
                + (pos[1]-icc[1]) * Math.cos(w(newTime - time, (right - left)/(scale*axle)))
                + icc[1];
        System.out.printf("Old X: %.2f, New X: %.2f%n", pos[0], x);

        return new double[] {
                x,
                y,
                theta + w(newTime - time, (right - left)/(scale*axle))
        };
    }

    @Override
    public String toString() {
        double[] p = getCartPos();
        return String.format("(%.2f, %.2f, %.2f)", p[0], p[1], p[2]*1800/Math.PI);
    }

    public int[] getEncoders() {
        return encoders;
    }

    public void update() {
        for (int i = 0; i < mot.length; i++) {
            int pos = mot[i].getCurrentPosition();
            int lastPos = lastEncoding[i];
            encoders[i] += pos!=lastPos?pos:0;
            lastEncoding[i] = mot[i].getCurrentPosition();
        }
    }

    class WheelPair<T extends DcMotor> implements DcMotor {
        private T a;
        private T b;

        public WheelPair(T a, T b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public MotorConfigurationType getMotorType() {
            return a.getMotorType().equals(b.getMotorType()) ? a.getMotorType() : null;
        }

        @Override
        public void setMotorType(MotorConfigurationType motorType) {
            a.setMotorType(motorType);
            b.setMotorType(motorType);
        }

        @Override
        public DcMotorController getController() {
            return null;
        }

        @Override
        public int getPortNumber() {
            return 0;
        }

        @Override
        public void setZeroPowerBehavior(ZeroPowerBehavior zeroPowerBehavior) {
            a.setZeroPowerBehavior(zeroPowerBehavior);
            b.setZeroPowerBehavior(zeroPowerBehavior);
        }

        @Override
        public ZeroPowerBehavior getZeroPowerBehavior() {
            return null;
        }

        @Override
        public void setPowerFloat() {

        }

        @Override
        public boolean getPowerFloat() {
            return false;
        }

        @Override
        public void setTargetPosition(int position) {

        }

        @Override
        public int getTargetPosition() {
            return 0;
        }

        @Override
        public boolean isBusy() {
            return false;
        }

        @Override
        public int getCurrentPosition() {
            return 0;
        }

        @Override
        public void setMode(RunMode mode) {

        }

        @Override
        public RunMode getMode() {
            return null;
        }

        @Override
        public void setDirection(Direction direction) {
            a.setDirection(direction);
            b.setDirection(direction);
        }

        @Override
        public Direction getDirection() {
            return a.getDirection().equals(b.getDirection()) ? a.getDirection() : null;
        }

        @Override
        public void setPower(double power) {
            a.setPower(power);
            b.setPower(power);
        }

        @Override
        public double getPower() {
            return 0;
        }

        @Override
        public Manufacturer getManufacturer() {
            return null;
        }

        @Override
        public String getDeviceName() {
            return null;
        }

        @Override
        public String getConnectionInfo() {
            return null;
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void resetDeviceConfigurationForOpMode() {

        }

        @Override
        public void close() {

        }
    }
}

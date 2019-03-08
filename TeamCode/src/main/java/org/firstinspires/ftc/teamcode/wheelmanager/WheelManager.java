package org.firstinspires.ftc.teamcode.wheelmanager;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.units.Angle;
import org.firstinspires.ftc.teamcode.units.Distance;
import org.firstinspires.ftc.teamcode.units.Unit;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Random;
import java.util.logging.Logger;

import java8.util.J8Arrays;
import java8.util.Optional;

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
    private final int ticks;
    private int[] previousPositions;
    private double previousDist;
    Logger logger;
    private long lastID = -1;
    private Random rand = new Random();
    private double movementPower;
    private double rotationPower;
    private final Deque<WheelManagerSnapshot> snapshots = new ArrayDeque<>();
    public Optional<Telemetry> telemetry = Optional.empty();
    public LinearOpMode lom;

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
        this.movementPower = movementPower;
        this.rotationPower = rotationPower;
    }

    public WheelManager(Optional<DcMotor>[] mot, double v, double v1, double v2, int i, int i1) {
        this(new DcMotor[4], v, v1, v2, i, i1);
        for (int j = 0; j < 4; j++) {
            if (mot[j].isPresent()) {
                this.mot[j] = mot[j].get();
            } else {
                throw new NullPointerException("mot["+j+"] is null!");
            }
            System.out.println(Arrays.toString(this.mot));
        }
    }

    public void initializeMotors(int[] motsToReverse) {
        for (int i : motsToReverse) {
            mot[i].setDirection(DcMotorSimple.Direction.REVERSE);
        }

        for (DcMotor m : mot) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    /**
     * Calculate the change in the orientation of the robot
     * @return the angle of the arc traced by the robot
     */
    private double w(int[] previousPositions, int[] currentPositions) {
        //return -D*omega*(radius/vradius)*t;
        //System.out.printf("Old theta: %.2f, D: %.2f, t: %.2f, new theta: %.2f%n",theta,D,t,theta + D*t);
        // `dTheta = r/d (dRotsR - dRotsL)`
        return radius/axle * (((double)currentPositions[1] - previousPositions[1])/ticks - ((double)currentPositions[3] - previousPositions[3])/ticks);
    }

    /**
     * calculate the change in the distance that the robot has traveled
     * @return the distance traversed
     *
     * TODO: start using all 4 of the robot's encoders
     */
    private double d(int[] previousPositions, int[] currentPositions) {
        // (unit-less) * (length) / (time) * (length)
        // `dS = r/2 (dRotsR + dRotsL)`
        return radius/2 * (((double)currentPositions[1] - previousPositions[1])/ticks + ((double)currentPositions[3] - previousPositions[3])/ticks);
    }

    public void setPower(double l, double r) {
        if (l != left || r != right) {
            for (int i = 0; i < 4; i++) if (mot[i] != null) mot[i].setPower(0);
            pos = getCartPos();
            double[] dt = getPolPos();
            dist = dt[0];
            theta = dt[1];
            time = System.currentTimeMillis() / 1000.;
            snapshots.add(new WheelManagerSnapshot(time, J8Arrays.stream(mot).limit(4L).mapToInt(DcMotor::getCurrentPosition).toArray()));
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
            telemetry.ifPresent(t -> {
                t.addData("Mot Power", "%.2f,%.2f", left,right);
                t.addData( "Mot Position", "%d,%d,%d,%d",mot[0].getCurrentPosition(),mot[1].getCurrentPosition(),mot[2].getCurrentPosition(),mot[3].getCurrentPosition());
                t.update();
            });
        }
    }

    public int[] getEncoders() {
        return J8Arrays.stream(mot).mapToInt(DcMotor::getCurrentPosition).toArray();
    }

    public void moveAnother(Distance distanceToMove, double power) {
        previousDist = getCM();
        setPower(power, power);
        while (lom.opModeIsActive() && distanceToMove.toCM() >= getCM() - previousDist);
        setPower(0,0);
    }

    public void moveTo(Distance pos, double power) {
        double direction = Math.signum(pos.toCM()-getCM());
        setPower(direction*power,direction*power);
        while (lom.opModeIsActive() && (pos.toCM() - getCM()) > 0);
        setPower(0,0);
    }

    public void turnAnother(Angle angleToRotate, double power) {
        double previousAngle = Math.abs(getDegrees());
        double direction = Math.signum(angleToRotate.getDegrees());
        setPower(-direction*power,direction*power);
        while (lom.opModeIsActive() && Math.abs(angleToRotate.getDegrees()) >= Math.abs(getDegrees()) - previousAngle);
        setPower(0,0);
    }

    public void turnTo(Angle theta, double power) {
        double direction = Math.signum(theta.getDegrees()-getDegrees());
        setPower(-direction*power, direction*power);
        while (lom.opModeIsActive() && getDegrees() != theta.getDegrees());
        setPower(0,0);
    }

    public void processInstruction(Instruction instruction) {
        instruction.apply(this);
    }

    public void processUnitInstruction(Unit u) {
        if (u instanceof Angle) {
            turnAnother((Angle) u, rotationPower);
        } else if (u instanceof Distance) {
            moveAnother((Distance) u, movementPower);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public long  callAfter(Distance distanceToMove, long id, Runnable c) {
        if (lastID == -1 || lastID != id) {
            previousDist = getCM();
            lastID = id;
            logger.info(String.format("New call to callAfter, pDist: %.1f", previousDist));
        }
        if (distanceToMove.toCM() > getCM() - previousDist) {
            return id;
        } else {
            logger.info(String.format("Calling c at distToMove: %.1f, currentDist: %.1f, totalDist: %.1f", distanceToMove.toCM(), getCM()-previousDist, getCM()));
            if (c!=null) {
                c.run();
            }
            return rand.nextLong();
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

        return new double[]{
                dist + d(previousPositions, J8Arrays.stream(mot).filter(m -> m!=null).mapToInt(DcMotor::getCurrentPosition).toArray()),
                theta + w(previousPositions, J8Arrays.stream(mot).filter(m -> m!=null).mapToInt(DcMotor::getCurrentPosition).toArray())
        };
    }

    public double[] getCartPos() {
        double[] icc = getICCPos();
        double newTime = System.currentTimeMillis()/1000;
        double x = Double.isNaN(icc[0]) ? pos[0] : (pos[0]-icc[0]) * Math.cos(w(previousPositions, J8Arrays.stream(mot).limit(4L).mapToInt(DcMotor::getCurrentPosition).toArray()))
                - (pos[1]-icc[1]) * Math.sin(w(previousPositions, J8Arrays.stream(mot).limit(4L).mapToInt(DcMotor::getCurrentPosition).toArray()))
                + icc[0];
        double y = Double.isNaN(icc[0]) ? pos[1] : (pos[0]-icc[0]) * Math.sin(w(previousPositions, J8Arrays.stream(mot).limit(4L).mapToInt(DcMotor::getCurrentPosition).toArray()))
                + (pos[1]-icc[1]) * Math.cos(w(previousPositions, J8Arrays.stream(mot).limit(4L).mapToInt(DcMotor::getCurrentPosition).toArray()))
                + icc[1];
        System.out.printf("Old X: %.2f, New X: %.2f%n", pos[0], x);

        return new double[] {
                x,
                y,
                theta + w(previousPositions, J8Arrays.stream(mot).limit(4L).mapToInt(DcMotor::getCurrentPosition).toArray())
        };
    }

    @Override
    public String toString() {
        double[] p = getCartPos();
        return String.format("(%.2f, %.2f, %.2f)", p[0], p[1], p[2]*1800/Math.PI);
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

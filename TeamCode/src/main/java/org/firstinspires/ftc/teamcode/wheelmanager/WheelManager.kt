package org.firstinspires.ftc.teamcode.wheelmanager

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.util.ElapsedTime
import java8.util.Optional
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.Position
import org.firstinspires.ftc.teamcode.units.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Provides an interface for interacting with robots designed
 * like the diagram below. WheelManager allows programmers to
 * simplify motor related code and also implements dead-reckoning positioning.
 *
 * WheelManager should almost never be used for manual control of the robot,
 * as it introduces a non negligible amount of lag and drastically reduces
 * the responsiveness.
 *
 * See this paper for help with dead-reckoning:
 * https://globaljournals.org/GJRE_Volume14/1-Kinematics-Localization-and-Control.pdf
 *
 *                      axleLength
 *            +--------------------------+
 *            |                          |
 *            v                          v
 *
 *            +          front           +
 *            | 1  +----------------+  0 |
 *       +->  +----+                +----+
 *       |    |    |                |    |
 *       +->  +    |                |    +
 *  wheelRadius    |                |
 *                 |                |
 *                 |                |
 *            +    |                |    +
 *            | 2  |                |  3 |
 *            +----+                +----+
 *            |    +----------------+    |
 *            +                          +
 *
 * @param mot           the list of motors to manage
 *                      can be Collection<DcMotor?>, Array<DcMotor?>,
 *                      or Array<Optional<DcMotor>>
 * @param wheelRadius   the radius of the robot's wheels
 * @param axleLength    the distance between the center of two opposite wheels
 * @param encoderTicks  how many ticks are in one revolution
 * @param parentOpMode  the opmode that instantiated this class
 *
 * @author Brian C
 */
class WheelManager private constructor(val wheelRadius: Double,
                   val axleLength: Double,
                   val encoderTicks: Int,
                   val parentOpMode: LinearOpMode?,
                   val isManual: Boolean) {
    private val mot = ArrayList<DcMotor>()

    constructor (mot: Collection<DcMotor?>,
                 wheelRadius: Double,
                 axleLength: Double,
                 encoderTicks: Int,
                 parentOpMode: LinearOpMode?,
                 isManual: Boolean):
            this(wheelRadius, axleLength, encoderTicks, parentOpMode, isManual) {
        val err = ArrayList<Int>()
        mot.forEachIndexed { i, m ->  if (m is DcMotor) {this.mot.add(m)} else {err.add(i)}}
        if (err.isNotEmpty()) throw IllegalArgumentException("Null motor(s): $err")
    }

    constructor(mot: Array<DcMotor?>, wheelRadius: Double, axleLength: Double, encoderTicks: Int, parentOpMode: LinearOpMode?, isManual: Boolean): this(wheelRadius, axleLength, encoderTicks, parentOpMode, isManual) {
        val err = ArrayList<Int>()
        mot.forEachIndexed { i, m ->  if (m is DcMotor) {this.mot.add(m)} else {err.add(i)}}
        if (err.size > 0) throw IllegalArgumentException("Null motor(s): $err")
    }

    constructor(mot: Array<Optional<DcMotor>>, wheelRadius: Double, axleLength: Double, encoderTicks: Int, parentOpMode: LinearOpMode?, isManual: Boolean): this(wheelRadius, axleLength, encoderTicks, parentOpMode, isManual) {
        if (mot.take(4).any { it.isEmpty }) {
            throw IllegalArgumentException("Null motors: ${mot.mapIndexed { index, _ -> index }.filter { mot[it].isEmpty }}")
        }
        this.mot.addAll(mot.take(4).map { it.get() })
    }

    // Power to the left and right motors
    private var left  = 0.0
    private var right = 0.0

    // Time since instantiation
    private val time: ElapsedTime = ElapsedTime()

    // Stack of snapshots for all previous calls to setPower
    private val snapshots: ArrayDeque<WheelManagerSnapshot> = ArrayDeque()
    private var dist = 0.0
    private var theta = 0.0
    private var pos = Position()

    /**
     * @return current heading in degrees.
     */
    val degrees get() = Degree(polarPos [1] * 1800 / Math.PI)
    val inches get() = Inches(10 / 3.533482 * polarPos[0])
    val cm get() = Centimeter(25.4 / 3.533482 * polarPos[0])
    val encoders get() =
        intArrayOf(mot[0].currentPosition, mot[1].currentPosition, mot[2].currentPosition, mot[3].currentPosition)

    init {
        snapshots.add(WheelManagerSnapshot(time.seconds(), intArrayOf(0,0,0,0), Centimeter(0.0), Degree(0.0)))
    }

    fun initializeMotors(motsToReverse: IntArray?) {
        motsToReverse?.forEach { mot[it].direction = DcMotorSimple.Direction.REVERSE }
        for (m in mot) {
            m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }
        Thread.sleep(500)
        for (m in mot) {
            m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    fun setPower(l: Double, r: Double) {
        if (l != left || r != right) {
            if (!isManual) for (i in 0..3) mot[i].power = 0.0
            snapshots.add(WheelManagerSnapshot(time.seconds(), encoders, cm, degrees))
            val dt = polarPos
            dist = dt[0]
            theta = dt[1]
            pos = getCartPos().position
            time.reset()
            left = l
            right = r
            for (i in 0..3) {
                mot[i].power = if (i == 1 || i == 2) left else right
            }
            //this.parentOpMode?.telemetry?.addData("Mot Power", "%.2f,%.2f", left, right)
            //parentOpMode?.telemetry?.addData("Mot Positions", "%s", Arrays.toString(encoders))
            //parentOpMode?.telemetry?.update()
        }
    }

    val polarPos get() =
            doubleArrayOf(
                    dist + d(snapshots.last.encoders, encoders),
                    theta + w(snapshots.last.encoders, encoders)
            )

    /**
     * Calculate the change in the orientation of the robot.
     * @param previousPositions    The positions of the encoders at the start of the rotation.
     * @param currentPositions     The current positions of the encoders.
     * @return the angle of the arc traced by the robot
     */
    private fun w(previousPositions: IntArray, currentPositions: IntArray): Double {
        //return -D*omega*(radius/vradius)*t;
        //System.out.printf("Old theta: %.2f, D: %.2f, t: %.2f, new theta: %.2f%n",theta,D,t,theta + D*t);
        // `dTheta = r/d (dRotsR - dRotsL)`
        return wheelRadius / axleLength * ((currentPositions[1].toDouble() - previousPositions[1]) / encoderTicks - (currentPositions[3].toDouble() - previousPositions[3]) / encoderTicks)
    }

    /**
     * Calculate the distance the robot has traveled.
     * @param previousPositions    The positions of the encoders at the beginning of movement.
     * @param currentPositions     The current positions of the encoders.
     * @return the distance traversed
     *
     * TODO: start using all 4 of the robot's encoders
     */
    private fun d(previousPositions: IntArray, currentPositions: IntArray): Double {
        // (unit-less) * (length) / (time) * (length)
        // `dS = r/2 (dRotsR + dRotsL)`
        return wheelRadius / 2 * ((currentPositions[1].toDouble() - previousPositions[1]) / encoderTicks + (currentPositions[3].toDouble() - previousPositions[3]) / encoderTicks)
    }

    /**
     * Blocks until the robot has finished moving by `distanceToMove' units.
     *
     * @param distanceToMove amount of units to move. Will be converted to centimeters.
     * @param power          amount of power to supply to the wheels.
     */
    fun moveAnother(distanceToMove: Distance, power: Double) {
        setPower(power, power)
        while (parentOpMode!!.opModeIsActive() && distanceToMove.toCM() >= cm - snapshots.last.cm);
        setPower(0.0, 0.0)
    }

    /**
     * Blocks until the robot has finished moving to `pos' units.
     *
     * @param pos   amount of units to move to. Will be converted to centimeters.
     * @param power amount of power to supply to the wheels.
     */
    fun moveTo(pos: Distance, power: Double) {
        val direction = (pos.toCM() - cm).signum()
        setPower(direction * power, direction * power)
        while (parentOpMode!!.opModeIsActive() && pos.toCM() - cm > Centimeter(0.0));
        setPower(0.0, 0.0)
    }

    /**
     * Blocks until the robot has finished rotating by `angleToRotate' units.
     *
     * @param angleToRotate amount of units to rotate. Will be converted to degrees.
     * @param power         amount of power to supply to the wheels.
     */
    fun turnAnother(angleToRotate: Angle, power: Double) {
        val previousAngle = degrees.abs()
        val direction = Math.signum(angleToRotate.degrees)
        setPower(-direction * power, direction * power)
        while (parentOpMode!!.opModeIsActive() && Math.abs(angleToRotate.degrees) >= degrees.abs() - previousAngle);
        setPower(0.0, 0.0)
    }

    /**
     * Blocks until the robot has finished rotating to `theta' units.
     *
     * @param theta amount of units face. Will be converted to degrees.
     * @param power amount of power to supply to the wheels.
     */
    fun turnTo(theta: Angle, power: Double) {
        val direction = (Degree(theta.degrees) - degrees).signum()
        setPower(-direction * power, direction * power)
        while (parentOpMode!!.opModeIsActive() && degrees != theta);
        setPower(0.0, 0.0)
    }

    fun processInstruction(instruction: Instruction?) {
        instruction?.apply(this)
    }

    // TODO: Fix this
    fun getCartPos(): PositionOrientation {
        return PositionOrientation(
                pos + Position(DistanceUnit.CM,
                        Math.cos(theta) * 25.4 / 3.533482 * d(snapshots.last.encoders, encoders),
                        Math.sin(theta) * 25.4 / 3.533482 * d(snapshots.last.encoders, encoders),
                        0.0,
                        time.nanoseconds()),
                Degree( theta + w(snapshots.last.encoders, encoders)))
    }

}
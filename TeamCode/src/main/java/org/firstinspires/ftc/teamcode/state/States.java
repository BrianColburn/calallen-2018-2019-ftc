package org.firstinspires.ftc.teamcode.state;

import org.firstinspires.ftc.robotcore.external.Event;
import org.firstinspires.ftc.robotcore.external.State;

public class States {
    /**
     * The robot is off.
     */
    public static final State OFF = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };

    /**
     * The robot has not deployed yet.
     */
    public static final State HANGING = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };

    /**
     * The robot is dropping off the token.
     */
    public static final State TOKEN = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };

    /**
     * The robot is in the depot.
     */
    public static final State DEPOT = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };

    /**
     * The robot is in between two locations.
     */
    public static final State TRANSIENT = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };

    /**
     * The robot is parked in the crater.
     */
    public static final State CRATER = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };

    /**
     * The robot is looking for the cube.
     */
    public static final State CUBE = new State() {

        @Override
        public void onEnter(Event event) {

        }

        @Override
        public void onExit(Event event) {

        }
    };
}
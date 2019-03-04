package org.firstinspires.ftc.teamcode.state;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.State;
import org.firstinspires.ftc.robotcore.external.StateTransition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

public class ExtendedStateMachine extends org.firstinspires.ftc.robotcore.external.StateMachine {
    public Logger logger;
    public LinkedList<State> stateHistory = new LinkedList<>();
    public State currentState;
    public ElapsedTime stateDuration = new ElapsedTime();
    public long stateIterations;

    public ExtendedStateMachine() {
        super();
        stateHistory.push(States.OFF);
        changeState(States.OFF);
    }

    public void addState(State s) {
        addState(s, new ArrayList<StateTransition>());
    }

    public void addState(State s, ArrayList<StateTransition> transitions) {
        stateGraph.put(s, transitions);
    }


    public void changeState(State s) {
        logger.info(String.format("Changing from state %s to state %s, history: %s", currentState, s, stateHistory));
        currentState = s;
        stateHistory.push(currentState);
        stateIterations = 0;
        stateDuration.reset();
    }
}

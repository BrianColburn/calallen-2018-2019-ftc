package org.firstinspires.ftc.teamcode.wheelmanager;

public interface Instruction extends java8.util.function.UnaryOperator<WheelManager> {
    @Override
    WheelManager apply(WheelManager wm);
}

package org.firstinspires.ftc.teamcode.wheelmanager;

import org.firstinspires.ftc.teamcode.WheelManager;

public interface Instruction extends java8.util.function.UnaryOperator<WheelManager> {
    @Override
    WheelManager apply(WheelManager wm);
}

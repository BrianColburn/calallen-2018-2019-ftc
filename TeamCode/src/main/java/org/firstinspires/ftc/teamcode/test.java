package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.units.Foot;
import org.firstinspires.ftc.teamcode.wheelmanager.WheelManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import java8.util.J8Arrays;

public class test {
    public static void main(String... args) throws InterruptedException {
        System.out.println(new Foot(1).getValue());
    }
}

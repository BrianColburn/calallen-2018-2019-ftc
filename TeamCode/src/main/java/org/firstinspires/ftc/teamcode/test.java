package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class test {
    public static void main(String... args) throws InterruptedException {
        Class bc = BoysController.class;
        Class c = bc.getSuperclass();
        Method[] ms = bc.getDeclaredMethods();
        Field[] fs = bc.getDeclaredFields();
        System.out.println(c);
        for (Method m : ms) {
            System.out.println(m);
        }
        for (Field f : fs) {
            System.out.println(f);
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

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
        Class clazz = WheelManager.class;
        Class c = clazz.getSuperclass();
        Method[] ms = clazz.getDeclaredMethods();
        Field[] fs = clazz.getDeclaredFields();
        System.out.println(c);
        for (Method m : ms) {
            String r = m.getReturnType().getSimpleName();
            String params = J8Arrays.stream(m.getParameterTypes()).map(Class::getSimpleName).reduce("",(s1, s2) -> s1 +", "+s2);
            System.out.printf("%s : %s %s(%s)%n", clazz.getSimpleName(), r.equals("void")?"":r, m.getName(), params);
            //System.out.println(m);
            //if (!Modifier.isPrivate(m.getModifiers()))
            //    System.out.println(m.getName() + " :: " + J8Arrays.stream(m.getParameterTypes()).map(Class::getSimpleName).map(s -> s + " -> ").reduce("",String::concat) + m.getReturnType().getSimpleName());
        }
        for (Field f : fs) {
            if (!Modifier.isPrivate(f.getModifiers()))
                System.out.println(f.getName() + " :: " + f.getType().getSimpleName());
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class test {
    public static void main(String... args) throws InterruptedException {
        LinkedList<Integer> l = new LinkedList<>();
        l.push(1);
        l.push(2);
        System.out.println(l);
        /*WheelManager wm = new WheelManager(new DcMotor[4], 8.89/2, 15.24/4.445, 37.5, .2);
        System.out.println(wm);
        wm.setPower(1,1);
        Thread.sleep(1000);
        wm.setPower(0,0);
        System.out.println(wm);
        wm.setPower(1,-1);
        Thread.sleep(1000);
        wm.setPower(0,0);
        System.out.println(wm);
        wm.setPower(1,1);
        Thread.sleep(1000);
        wm.setPower(0,0);
        System.out.println(wm);*/
    }
}

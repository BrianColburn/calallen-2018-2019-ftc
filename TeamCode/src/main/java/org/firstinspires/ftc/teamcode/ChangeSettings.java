package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashMap;

@TeleOp(name="Toggle Settings", group="Settings")
public class ChangeSettings extends OpMode {
    public static final String SETTINGS = "/storage/emulated/0/robot-settings";
    HashMap<String, String> map = new HashMap<>();

    @Override
        public void init() {

        try {
            FileInputStream fis = new FileInputStream(SETTINGS); //fnf
            ObjectInputStream ois = new ObjectInputStream(fis); //sc, io
            map = (HashMap<String, String>) ois.readObject(); //cnf
            ois.close();
            fis.close();

            boolean doCube = Boolean.parseBoolean(map.get("doCube"));
            map.put("doCube", Boolean.valueOf(!doCube).toString());
            telemetry.addData("doCube", !doCube);


            FileOutputStream fos = new FileOutputStream(SETTINGS); //fnf
            ObjectOutputStream oos = new ObjectOutputStream(fos); //io

            oos.writeObject(map);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            try {
                FileOutputStream fos = new FileOutputStream(SETTINGS); //fnf
                ObjectOutputStream oos = new ObjectOutputStream(fos); //io

                map.put("doCube","false");

                oos.writeObject(map);
                oos.close();
                fos.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loop() {
        try {
            map = getSettings();
        } catch (IOException e) {
            telemetry.addData("IOE", Arrays.toString(e.getStackTrace()));
        } catch (ClassNotFoundException e) {
            telemetry.addData("CNFE", Arrays.toString(e.getStackTrace()));
        }

        telemetry.addData("Map", map);
    }

    public static HashMap<String, String> getSettings() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(SETTINGS); //fnf
        ObjectInputStream ois = new ObjectInputStream(fis); //sc, io
        HashMap<String, String> settings = (HashMap<String, String>) ois.readObject(); //cnf
        ois.close();
        fis.close();
        return settings;
    }
}

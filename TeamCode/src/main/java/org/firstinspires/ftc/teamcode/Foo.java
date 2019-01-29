package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import static android.content.ContentValues.TAG;

public class Foo implements SensorEventListener {

    SensorManager sensorManager;
    float[] gData = new float[3];           // Gravity or accelerometer
    float[] mData = new float[3];           // Magnetometer
    float[] orientation = new float[3];
    float[] Rmat = new float[9];
    float[] R2 = new float[9];
    float[] Imat = new float[9];
    boolean haveGrav = false;
    boolean haveAccel = false;
    boolean haveMag = false;

    Telemetry telemetry;

    public Foo(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void onCreate() {
        // Get the sensor manager from system services
        //sensorManager =
          //(SensorManager)getSystemService(Context.SENSOR_SERVICE);
    }

    public void onResume() {
        //super.onResume();
        // Register our listeners
        Sensor gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, asensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onSensorChanged(SensorEvent event) {
        float[] data;
        double DEG = 180/Math.PI;
        switch( event.sensor.getType() ) {
          case Sensor.TYPE_GRAVITY:
            gData[0] = event.values[0];
            gData[1] = event.values[1];
            gData[2] = event.values[2];
            haveGrav = true;
            break;
          case Sensor.TYPE_ACCELEROMETER:
            if (haveGrav) break;    // don't need it, we have better
            gData[0] = event.values[0];
            gData[1] = event.values[1];
            gData[2] = event.values[2];
            haveAccel = true;
            break;
          case Sensor.TYPE_MAGNETIC_FIELD:
            mData[0] = event.values[0];
            mData[1] = event.values[1];
            mData[2] = event.values[2];
            haveMag = true;
            break;
          default:
            return;
        }

        if ((haveGrav || haveAccel) && haveMag) {
            SensorManager.getRotationMatrix(Rmat, Imat, gData, mData);
            SensorManager.remapCoordinateSystem(Rmat,
                    SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, R2);
            // Orientation isn't as useful as a rotation matrix, but
            // we'll show it here anyway.
            SensorManager.getOrientation(R2, orientation);
            float incl = SensorManager.getInclination(Imat);
            Log.d(TAG, "mh: " + (int)(orientation[0]*DEG));
            Log.d(TAG, "pitch: " + (int)(orientation[1]*DEG));
            Log.d(TAG, "roll: " + (int)(orientation[2]*DEG));
            Log.d(TAG, "yaw: " + (int)(orientation[0]*DEG));
            Log.d(TAG, "inclination: " + (int)(incl*DEG));

            telemetry.addData("Yaw:", "(%.2f)", orientation[0]*DEG);
        }
      }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
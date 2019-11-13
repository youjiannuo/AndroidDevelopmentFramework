package com.yn.framework.system;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by youjiannuo on 17/1/6
 */

public class SenesorManagerUtil implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public SenesorManagerUtil(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    }

    public void onResume() {
        if (mSensorManager != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onPause() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        SystemUtil.printlnInfo("x = " + Math.toDegrees(event.values[0]) + "  y = " + Math.toDegrees(event.values[1]) + "  z = " + Math.toDegrees(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

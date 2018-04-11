package com.csware.divi_dead.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerHandler implements SensorEventListener {
	float	m_AccelX;
	float	m_AccelY;
	float	m_AccelZ;

	public AccelerometerHandler(Context p_Context) {
		SensorManager manager = (SensorManager) p_Context
				.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			Sensor accelerometer = manager.getSensorList(
					Sensor.TYPE_ACCELEROMETER).get(0);
			manager.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void onAccuracyChanged(Sensor p_Sensor, int p_Accuracy) {
		// nothing to do here
	}

	public void onSensorChanged(SensorEvent p_Event) {
		m_AccelX = p_Event.values[0];
		m_AccelY = p_Event.values[1];
		m_AccelZ = p_Event.values[2];
	}

	public float getAccelX() {
		return m_AccelX;
	}

	public float getAccelY() {
		return m_AccelY;
	}

	public float getAccelZ() {
		return m_AccelZ;
	}
}

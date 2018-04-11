package com.csware.divi_dead.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CompassHandler implements SensorEventListener {
	float	m_Yaw;
	float	m_Pitch;
	float	m_Roll;

	@SuppressWarnings("deprecation")
	public CompassHandler(Context p_Context) {
		SensorManager manager = (SensorManager) p_Context
				.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ORIENTATION).size() != 0) {
			Sensor compass = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			manager.registerListener(this, compass,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void onAccuracyChanged(Sensor p_Sensor, int p_Accuracy) {
		// nothing to do here
	}

	public void onSensorChanged(SensorEvent p_Event) {
		m_Yaw = p_Event.values[0];
		m_Pitch = p_Event.values[1];
		m_Roll = p_Event.values[2];
	}

	public float getYaw() {
		return m_Yaw;
	}

	public float getPitch() {
		return m_Pitch;
	}

	public float getRoll() {
		return m_Roll;
	}
}

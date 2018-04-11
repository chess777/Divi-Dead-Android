package com.csware.divi_dead.framework.impl;

import java.util.List;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

import com.csware.divi_dead.framework.Input;
import com.csware.divi_dead.framework.TouchHandler;

public class AndroidInput implements Input {
	AccelerometerHandler	m_AccelHandler;
	KeyboardHandler			m_KeyHandler;
	TouchHandler			m_TouchHandler;

	public AndroidInput(Context p_Context, View p_View, float p_ScaleX,
			float p_ScaleY) {
		m_AccelHandler = new AccelerometerHandler(p_Context);
		m_KeyHandler = new KeyboardHandler(p_View);
		if (VERSION.SDK_INT < 5) m_TouchHandler = new SingleTouchHandler(
				p_View, p_ScaleX, p_ScaleY);
		else m_TouchHandler = new MultiTouchHandler(p_View, p_ScaleX, p_ScaleY);
	}

	public boolean isKeyPressed(int p_KeyCode) {
		return m_KeyHandler.isKeyPressed(p_KeyCode);
	}

	public boolean isTouchDown(int p_Pointer) {
		return m_TouchHandler.isTouchDown(p_Pointer);
	}

	public int getTouchX(int p_Pointer) {
		return m_TouchHandler.getTouchX(p_Pointer);
	}

	public int getTouchY(int p_Pointer) {
		return m_TouchHandler.getTouchY(p_Pointer);
	}

	public float getAccelX() {
		return m_AccelHandler.getAccelX();
	}

	public float getAccelY() {
		return m_AccelHandler.getAccelY();
	}

	public float getAccelZ() {
		return m_AccelHandler.getAccelZ();
	}

	public List<TouchEvent> getTouchEvents() {
		return m_TouchHandler.getTouchEvents();
	}

	public List<KeyEvent> getKeyEvents() {
		return m_KeyHandler.getKeyEvents();
	}
}

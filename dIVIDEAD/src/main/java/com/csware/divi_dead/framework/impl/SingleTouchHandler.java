package com.csware.divi_dead.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;

import com.csware.divi_dead.framework.Pool;
import com.csware.divi_dead.framework.TouchHandler;
import com.csware.divi_dead.framework.Input.TouchEvent;
import com.csware.divi_dead.framework.Pool.PoolObjectFactory;

public class SingleTouchHandler implements TouchHandler {
	boolean				m_IsTouched;
	int					m_TouchX;
	int					m_TouchY;
	Pool<TouchEvent>	m_TouchEventPool;
	List<TouchEvent>	m_TouchEvents		= new ArrayList<TouchEvent>();
	List<TouchEvent>	m_TouchEventsBuffer	= new ArrayList<TouchEvent>();
	float				m_ScaleX;
	float				m_ScaleY;

	public SingleTouchHandler(View p_View, float p_ScaleX, float p_ScaleY) {
		PoolObjectFactory<TouchEvent> factory;

		factory = new PoolObjectFactory<TouchEvent>() {
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};
		m_TouchEventPool = new Pool<TouchEvent>(factory, 100);
		p_View.setOnTouchListener(this);

		this.m_ScaleX = p_ScaleX;
		this.m_ScaleY = p_ScaleY;
	}

	public boolean onTouch(View p_View, MotionEvent p_Event) {
		TouchEvent touchEvent;

		synchronized (this) {
			touchEvent = m_TouchEventPool.newObject();
			switch (p_Event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touchEvent.m_Type = TouchEvent.TOUCH_DOWN;
				m_IsTouched = true;
				break;
			case MotionEvent.ACTION_MOVE:
				touchEvent.m_Type = TouchEvent.TOUCH_DRAGGED;
				m_IsTouched = true;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				touchEvent.m_Type = TouchEvent.TOUCH_UP;
				m_IsTouched = false;
				break;
			}

			touchEvent.m_x = m_TouchX = (int) (p_Event.getX() * m_ScaleX);
			touchEvent.m_y = m_TouchY = (int) (p_Event.getY() * m_ScaleY);
			m_TouchEventsBuffer.add(touchEvent);

			return true;
		}
	}

	public boolean isTouchDown(int p_Pointer) {
		synchronized (this) {
			if (p_Pointer == 0) return m_IsTouched;
			else return false;
		}
	}

	public int getTouchX(int p_Pointer) {
		synchronized (this) {
			return m_TouchX;
		}
	}

	public int getTouchY(int p_Pointer) {
		synchronized (this) {
			return m_TouchY;
		}
	}

	public List<TouchEvent> getTouchEvents() {
		int len;
		int i;

		synchronized (this) {
			len = m_TouchEvents.size();
			for (i = 0; i < len; i++)
				m_TouchEventPool.free(m_TouchEvents.get(i));
			m_TouchEvents.clear();
			m_TouchEvents.addAll(m_TouchEventsBuffer);
			m_TouchEventsBuffer.clear();
			return m_TouchEvents;
		}
	}
}

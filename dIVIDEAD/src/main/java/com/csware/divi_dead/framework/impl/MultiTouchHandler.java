package com.csware.divi_dead.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.view.MotionEvent;
import android.view.View;

import com.csware.divi_dead.framework.Input.TouchEvent;
import com.csware.divi_dead.framework.Pool;
import com.csware.divi_dead.framework.TouchHandler;
import com.csware.divi_dead.framework.Pool.PoolObjectFactory;

@TargetApi(5)
public class MultiTouchHandler implements TouchHandler {
	private static final int	MAX_TOUCHPOINTS		= 10;

	boolean[]					m_IsTouched			= new boolean[MAX_TOUCHPOINTS];
	int[]						m_TouchX			= new int[MAX_TOUCHPOINTS];
	int[]						m_TouchY			= new int[MAX_TOUCHPOINTS];
	int[]						m_Id				= new int[MAX_TOUCHPOINTS];
	Pool<TouchEvent>			m_TouchEventPool;
	List<TouchEvent>			m_TouchEvents		= new ArrayList<TouchEvent>();
	List<TouchEvent>			m_TouchEventsBuffer	= new ArrayList<TouchEvent>();
	float						m_ScaleX;
	float						m_ScaleY;

	public MultiTouchHandler(View p_View, float p_ScaleX, float p_ScaleY) {
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
		int action;
		int pointerIndex;
		int pointerCount;
		int pointerId;
		TouchEvent touchEvent;

		synchronized (this) {
			action = p_Event.getAction() & MotionEvent.ACTION_MASK;
			pointerIndex = (p_Event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			pointerCount = p_Event.getPointerCount();

			for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
				if (i >= pointerCount) {
					m_IsTouched[i] = false;
					m_Id[i] = -1;
					continue;
				}
				pointerId = p_Event.getPointerId(i);
				if (p_Event.getAction() != MotionEvent.ACTION_MOVE
						&& i != pointerIndex) {
					// if it's an up/down/cancel/out event, mask the id to see
					// if we should process it for this touch
					// point
					continue;
				}
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					touchEvent = m_TouchEventPool.newObject();
					touchEvent.m_Type = TouchEvent.TOUCH_DOWN;
					touchEvent.m_Pointer = pointerId;
					touchEvent.m_x = m_TouchX[i] = (int) (p_Event.getX(i) * m_ScaleX);
					touchEvent.m_y = m_TouchY[i] = (int) (p_Event.getY(i) * m_ScaleY);
					m_IsTouched[i] = true;
					m_Id[i] = pointerId;
					m_TouchEventsBuffer.add(touchEvent);
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					touchEvent = m_TouchEventPool.newObject();
					touchEvent.m_Type = TouchEvent.TOUCH_UP;
					touchEvent.m_Pointer = pointerId;
					touchEvent.m_x = m_TouchX[i] = (int) (p_Event.getX(i) * m_ScaleX);
					touchEvent.m_y = m_TouchY[i] = (int) (p_Event.getY(i) * m_ScaleY);
					m_IsTouched[i] = false;
					m_Id[i] = -1;
					m_TouchEventsBuffer.add(touchEvent);
					break;

				case MotionEvent.ACTION_MOVE:
					touchEvent = m_TouchEventPool.newObject();
					touchEvent.m_Type = TouchEvent.TOUCH_DRAGGED;
					touchEvent.m_Pointer = pointerId;
					touchEvent.m_x = m_TouchX[i] = (int) (p_Event.getX(i) * m_ScaleX);
					touchEvent.m_y = m_TouchY[i] = (int) (p_Event.getY(i) * m_ScaleY);
					m_IsTouched[i] = true;
					m_Id[i] = pointerId;
					m_TouchEventsBuffer.add(touchEvent);
					break;
				}
			}
			return true;
		}
	}

	public boolean isTouchDown(int p_Pointer) {
		int index;

		synchronized (this) {
			index = getIndex(p_Pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS) return false;
			else return m_IsTouched[index];
		}
	}

	public int getTouchX(int p_Pointer) {
		int index;

		synchronized (this) {
			index = getIndex(p_Pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS) return 0;
			else return m_TouchX[index];
		}
	}

	public int getTouchY(int p_Pointer) {
		int index;

		synchronized (this) {
			index = getIndex(p_Pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS) return 0;
			else return m_TouchY[index];
		}
	}

	public List<TouchEvent> getTouchEvents() {
		int len;

		synchronized (this) {
			len = m_TouchEvents.size();
			for (int i = 0; i < len; i++)
				m_TouchEventPool.free(m_TouchEvents.get(i));
			m_TouchEvents.clear();
			m_TouchEvents.addAll(m_TouchEventsBuffer);
			m_TouchEventsBuffer.clear();
			return m_TouchEvents;
		}
	}

	// returns the index for a given pointerId or -1 if no index.
	private int getIndex(int p_PointerId) {
		int i;

		for (i = 0; i < MAX_TOUCHPOINTS; i++) {
			if (m_Id[i] == p_PointerId) {
				return i;
			}
		}
		return -1;
	}
}

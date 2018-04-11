package com.csware.divi_dead.framework;

import java.util.List;

import android.view.View.OnTouchListener;

import com.csware.divi_dead.framework.Input.TouchEvent;

public interface TouchHandler extends OnTouchListener {
	public boolean isTouchDown(int p_Pointer);

	public int getTouchX(int p_Pointer);

	public int getTouchY(int p_Pointer);

	public List<TouchEvent> getTouchEvents();
}

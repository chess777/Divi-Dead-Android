package com.csware.divi_dead.framework.impl;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnKeyListener;

import com.csware.divi_dead.framework.Input.KeyEvent;
import com.csware.divi_dead.framework.Pool;
import com.csware.divi_dead.framework.Pool.PoolObjectFactory;

public class KeyboardHandler implements OnKeyListener {
	boolean[]		m_PressedKeys		= new boolean[128];
	Pool<KeyEvent>	m_KeyEventPool;
	List<KeyEvent>	m_KeyEventsBuffer	= new ArrayList<KeyEvent>();
	List<KeyEvent>	m_KeyEvents		= new ArrayList<KeyEvent>();

	public KeyboardHandler(View p_View) {
		PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
			public KeyEvent createObject() {
				return new KeyEvent();
			}
		};
		m_KeyEventPool = new Pool<KeyEvent>(factory, 100);
		p_View.setOnKeyListener(this);
		p_View.setFocusableInTouchMode(true);
		p_View.requestFocus();
	}

	public boolean onKey(View p_View, int p_KeyCode, android.view.KeyEvent p_Event) {
		if (p_Event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE) return false;

		KeyEvent keyEvent;
		
		synchronized (this) {
			keyEvent = m_KeyEventPool.newObject();
			keyEvent.m_KeyCode = p_KeyCode;
			keyEvent.m_KeyChar = (char) p_Event.getUnicodeChar();
			if (p_Event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
				keyEvent.m_Type = KeyEvent.KEY_DOWN;
				if (p_KeyCode > 0 && p_KeyCode < 127) m_PressedKeys[p_KeyCode] = true;
			}
			if (p_Event.getAction() == android.view.KeyEvent.ACTION_UP) {
				keyEvent.m_Type = KeyEvent.KEY_UP;
				if (p_KeyCode > 0 && p_KeyCode < 127) m_PressedKeys[p_KeyCode] = false;
			}
			
			m_KeyEventsBuffer.add(keyEvent);
		}
		return false;
	}

	public boolean isKeyPressed(int p_KeyCode) {
		if (p_KeyCode < 0 || p_KeyCode > 127) return false;
		return m_PressedKeys[p_KeyCode];
	}

	public List<KeyEvent> getKeyEvents() {
		synchronized (this) {
			int len = m_KeyEvents.size();
			for (int i = 0; i < len; i++) {
				m_KeyEventPool.free(m_KeyEvents.get(i));
			}
			m_KeyEvents.clear();
			m_KeyEvents.addAll(m_KeyEventsBuffer);
			m_KeyEventsBuffer.clear();
			return m_KeyEvents;
		}
	}
}

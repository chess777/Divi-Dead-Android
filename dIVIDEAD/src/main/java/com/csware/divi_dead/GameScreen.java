package com.csware.divi_dead;

import java.util.List;

import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

import com.csware.divi_dead.framework.Game;
import com.csware.divi_dead.framework.Input.KeyEvent;
import com.csware.divi_dead.framework.Input.TouchEvent;
import com.csware.divi_dead.framework.Screen;
import com.csware.divi_dead.framework.Video;
import com.csware.divi_dead.framework.Video.OnCompletionListener;
import com.csware.divi_dead.framework.Video.OnReadyToPlayListener;

public class GameScreen extends Screen {

	private final String	m_TAG				= "GameScreen";
	@SuppressWarnings("unused")
	private float			m_TimePassed		= 0.0f;
	private int				m_CurrentStep		= 0;
	private final int		m_LAST_STEP			= 3;
	private boolean			m_SomethingDone		= false;
	
	public GameScreen(Game p_Game) {
		super(p_Game);
	}

	@Override
	public void update(float p_DeltaTime) {

		List<TouchEvent> touchEvents;
		List<KeyEvent> keyEvents;
		TouchEvent touchEvent;
		KeyEvent keyEevent;
		int len;
		int i;

		m_TimePassed += p_DeltaTime;

		// Process touch screen events
		touchEvents = m_Game.getInput().getTouchEvents();
		len = touchEvents.size();
		for (i = 0; i < len; i++) {
			touchEvent = touchEvents.get(i);
			if (touchEvent.m_Type == TouchEvent.TOUCH_UP) {
				
			}
		}

		// Process keyboard events
		keyEvents = m_Game.getInput().getKeyEvents();
		len = keyEvents.size();
		for (i = 0; i < len; i++) {
			keyEevent = keyEvents.get(i);
			if (keyEevent.m_Type == KeyEvent.KEY_UP) {
				if (keyEevent.m_KeyCode == android.view.KeyEvent.KEYCODE_ENTER
						|| keyEevent.m_KeyCode == android.view.KeyEvent.KEYCODE_ESCAPE) {
					
				}
			}
		}

		// TODO: add mouse support for API 14+

		switch (m_CurrentStep) {
		
		case 0:
			Log.v(m_TAG, "");

			m_SomethingDone = true;
			break;
			
		case 1:
			
			break;
			
		case 2:
			
			break;

		
		}

		if (m_SomethingDone) {
			m_CurrentStep++;
			m_SomethingDone = false;
		}
	}

	@Override
	public void present(float p_DeltaTime) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}

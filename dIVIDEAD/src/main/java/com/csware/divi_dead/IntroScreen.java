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

public class IntroScreen extends Screen implements OnReadyToPlayListener,
		OnCompletionListener {

	private final String	m_TAG					= "IntroScreen";
	private final int		m_TARGET_VIDEO_WIDTH	= 480;
	private final int		m_TARGET_VIDEO_HEIGHT	= 240;
	private final int		m_TARGET_VIDEO_HEIGHT2	= 264;
	@SuppressWarnings("unused")
	private float			m_TimePassed			= 0.0f;
	private int				m_CurrentStep			= 0;
	private final int		m_LAST_STEP				= 3;
	private boolean			m_SomethingDone			= false;
	private Video			m_Video					= null;
	private boolean			m_VideoIsReady			= false;
	private boolean			m_PlaybackFinished		= false;
	private boolean			m_InterruptPlayback		= false;
	private long			m_CurrentPosition		= 0;

	public IntroScreen(Game p_Game) {
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
		for (i = 0; i < len && !m_InterruptPlayback; i++) {
			touchEvent = touchEvents.get(i);
			if (touchEvent.m_Type == TouchEvent.TOUCH_UP) {
				m_InterruptPlayback = true;
			}
		}

		// Process keyboard events
		keyEvents = m_Game.getInput().getKeyEvents();
		len = keyEvents.size();
		for (i = 0; i < len && !m_InterruptPlayback; i++) {
			keyEevent = keyEvents.get(i);
			if (keyEevent.m_Type == KeyEvent.KEY_UP) {
				if (keyEevent.m_KeyCode == android.view.KeyEvent.KEYCODE_ENTER
						|| keyEevent.m_KeyCode == android.view.KeyEvent.KEYCODE_ESCAPE) {
					m_InterruptPlayback = true;
				}
			}
		}

		// TODO: add mouse support for API 14+

		switch (m_CurrentStep) {

		case 0:
			Log.v(m_TAG, "Loading intro video");

			m_Game.getRenderView().disableRedraw();
			m_Video = m_Game.getMultimedia().newVideo();
			m_Video.setOnCompletionListener(this);
			m_Video.setOnReadyToPlayListener(this);

			m_Video.setVideoSource(Assets.m_GameFolder + "OPEN.AVI");

			m_SomethingDone = true;
			break;

		case 1:
			if (m_VideoIsReady) {
				Log.v(m_TAG, "Starting intro playback");

				int scaledVideoWidth;
				int scaledVideoHeight;
				int scaledVideoHeight2;
				int videoPosX;
				int videoPosY;

				scaledVideoWidth = (int) (m_TARGET_VIDEO_WIDTH * Assets.Configuration.Graphics.m_ScaledScreenRatio);
				scaledVideoHeight = (int) (m_TARGET_VIDEO_HEIGHT * Assets.Configuration.Graphics.m_ScaledScreenRatio);
				scaledVideoHeight2 = (int) (m_TARGET_VIDEO_HEIGHT2 * Assets.Configuration.Graphics.m_ScaledScreenRatio);
				videoPosX = Assets.Configuration.Graphics.m_ScaledScreenPosX
						+ (Assets.Configuration.Graphics.m_ScaledScreenWidth - scaledVideoWidth)
						/ 2;
				videoPosY = Assets.Configuration.Graphics.m_ScaledScreenPosY
						+ (Assets.Configuration.Graphics.m_ScaledScreenHeight - scaledVideoHeight)
						/ 2;

				m_Video.setVideoPosition(videoPosX, videoPosY,
						scaledVideoWidth, scaledVideoHeight2);

				m_Video.setVolume(1.0f);
				Log.v(m_TAG, "Seeking to " + m_CurrentPosition);
				m_Video.play();
				m_Video.seekTo(m_CurrentPosition);

				m_SomethingDone = true;
			}

			break;

		case 2:
			if (m_PlaybackFinished || m_InterruptPlayback) {
				Log.v(m_TAG, "Intro playback finished");

				dispose();

				m_SomethingDone = true;
			}

			break;

		case 3:
			SystemClock.sleep(200);

			m_Game.setScreen(new GameScreen(m_Game));

			m_SomethingDone = true;

			break;

		}

		if (m_SomethingDone) {
			m_CurrentStep++;
			m_SomethingDone = false;
		}
	}

	@Override
	public void present(float p_DeltaTime) {
		if (m_CurrentStep >= m_LAST_STEP) {
			m_Game.getGraphics().clear(Color.argb(255, 0, 0, 0));
		}
	}

	@Override
	public void pause() {
		if (m_Video != null) {
			m_CurrentPosition = m_Video.getCurrentPosition();
			Log.v(m_TAG, "Pausing at position " + m_CurrentPosition);
			m_Video.dispose();
		}
	}

	@Override
	public void resume() {
		if (m_CurrentStep < m_LAST_STEP) {
			m_CurrentStep = 0;
		}
	}

	@Override
	public void dispose() {
		if (m_Video != null) {
			m_Video.dispose();
			m_Video.setVideoPosition(0, 0, m_Game.getGraphics().getWidth(),
					m_Game.getGraphics().getHeight());
			m_Video = null;
		}

		m_Game.getRenderView().enableRedraw();
	}

	@Override
	public void onReady() {
		m_VideoIsReady = true;
	}

	@Override
	public void onCompletion() {
		m_PlaybackFinished = true;
	}

}

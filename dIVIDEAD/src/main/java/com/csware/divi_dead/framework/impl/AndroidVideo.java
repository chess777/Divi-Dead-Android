package com.csware.divi_dead.framework.impl;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout.LayoutParams;

import com.csware.divi_dead.framework.Video;

public class AndroidVideo implements Video, OnCompletionListener,
		OnPreparedListener, OnVideoSizeChangedListener, OnErrorListener {

	private static final String		m_TAG				= "AndroidVideo";
	private int						m_VideoWidth		= 0;
	private int						m_VideoHeight		= 0;
	private boolean					m_IsVideoSizeKnown	= false;
	private boolean					m_IsVideoPrepared	= false;
	private boolean					m_IsPaused			= false;
	private boolean					m_IsPlaying			= false;
	private MediaPlayer				m_MediaPlayer;
	private SurfaceView				m_SurfaceView;
	Context							m_Context;
	private OnReadyToPlayListener	m_OnReadyToPlayListener;
	private OnCompletionListener	m_OnCompletionListener;

	AndroidVideo(Context p_Context, SurfaceView p_SurfaceView) {
		try {
			// Create a new media player and set the listeners
			m_Context = p_Context;
			m_SurfaceView = p_SurfaceView;
			m_MediaPlayer = new MediaPlayer(m_Context);
			m_MediaPlayer.setDisplay(m_SurfaceView.getHolder());
			m_MediaPlayer.setScreenOnWhilePlaying(true);
			m_MediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
			m_MediaPlayer.setOnCompletionListener(this);
			m_MediaPlayer.setOnPreparedListener(this);
			m_MediaPlayer.setOnVideoSizeChangedListener(this);
			m_MediaPlayer.setOnErrorListener(this);
		}
		catch (Exception e) {
			Log.e(m_TAG, "Error: " + e.getMessage(), e);
			throw new RuntimeException(m_TAG + " - Error: " + e.getMessage());
		}
	}

	public synchronized void setVideoSource(String p_FileName) {
		try {
			m_MediaPlayer.setDataSource(p_FileName);
			m_MediaPlayer.prepare();
		}
		catch (Exception e) {
			Log.e(m_TAG, "Error: " + e.getMessage(), e);
			throw new RuntimeException(m_TAG + " - Error: " + e.getMessage());
		}
	}

	@Override
	public synchronized void play() {
		if (m_MediaPlayer != null) {
			if (m_IsPlaying) {
				return;
			}
			try {
				if (!(m_IsVideoPrepared && m_IsVideoSizeKnown)) {
					m_MediaPlayer.prepare();
				}
				m_MediaPlayer.start();
				m_IsPlaying = true;
			}
			catch (Exception e) {
				Log.e(m_TAG, "Error: " + e.getMessage(), e);
				throw new RuntimeException(m_TAG + " - Error: "
						+ e.getMessage());
			}
		}
	}

	@Override
	public synchronized void stop() {
		if (m_MediaPlayer != null) {
			m_MediaPlayer.stop();
			m_VideoWidth = 0;
			m_VideoHeight = 0;
			m_IsVideoPrepared = false;
			m_IsVideoSizeKnown = false;
			m_IsPlaying = false;
		}
	}

	@Override
	public synchronized void pause() {
		if (m_MediaPlayer != null) {
			synchronized (this) {
				if (m_IsPlaying) {
					m_MediaPlayer.pause();
					m_IsPaused = true;
					m_IsPlaying = false;
				}
			}
		}
	}

	@Override
	public int getWidth() {
		return m_VideoWidth;
	}

	@Override
	public int getHeight() {
		return m_VideoHeight;
	}

	@Override
	public synchronized long getCurrentPosition() {
		if (m_MediaPlayer != null && m_IsPlaying) {
			return m_MediaPlayer.getCurrentPosition();
		}

		return 0;
	}

	@Override
	public void seekTo(long p_Position) {
		if (m_MediaPlayer != null) {
			m_MediaPlayer.seekTo(p_Position);
		}
	}

	@Override
	public void setVolume(float p_Volume) {
		if (m_MediaPlayer != null) {
			m_MediaPlayer.setVolume(p_Volume, p_Volume);
		}
	}

	@Override
	public void setVideoPosition(int p_x, int p_y, int p_Width, int p_Height) {
		if (m_Context != null) {
			LayoutParams lp;
			Message msg;

			lp = new LayoutParams(p_Width, p_Height);
			lp.setMargins(p_x, p_y, p_Width, p_Height);
			msg = Message.obtain();
			msg.what = 0;
			msg.obj = lp;
			((AndroidGame) m_Context).m_MainHandler
					.sendMessageAtFrontOfQueue(msg);
		}
	}

	@Override
	public void setOnReadyToPlayListener(OnReadyToPlayListener p_Listener) {
		m_OnReadyToPlayListener = p_Listener;
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener p_Listener) {
		m_OnCompletionListener = p_Listener;
	}

	@Override
	public synchronized boolean isPlaying() {
		if (m_MediaPlayer != null) {
			return m_IsPlaying;
		}

		return false;
	}

	@Override
	public boolean isStopped() {
		return !(m_IsVideoPrepared && m_IsVideoSizeKnown);
	}

	@Override
	public boolean isPaused() {
		return m_IsPaused;
	}

	@Override
	public synchronized void dispose() {
		if (m_MediaPlayer != null) {
			m_VideoWidth = 0;
			m_VideoHeight = 0;
			m_IsVideoPrepared = false;
			m_IsVideoSizeKnown = false;

			if (m_IsPlaying) {
				m_MediaPlayer.stop();
			}
			m_MediaPlayer.release();
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer p_MediaPlayer, int p_Width,
			int p_Height) {
		if (p_Width == 0 || p_Height == 0) {
			Log.e(m_TAG, "Invalid video width(" + p_Width + ") or height("
					+ p_Height + ")");
			throw new RuntimeException(m_TAG + " - Invalid video width("
					+ p_Width + ") or height(" + p_Height + ")");
		}
		synchronized (this) {
			m_IsVideoSizeKnown = true;
			m_VideoWidth = p_Width;
			m_VideoHeight = p_Height;
		}

		if (m_IsVideoPrepared && m_IsVideoSizeKnown) {
			onReady();
		}
	}

	@Override
	public synchronized void onPrepared(MediaPlayer p_MediaPlayer) {
		m_IsVideoPrepared = true;
		
		if (m_IsVideoPrepared && m_IsVideoSizeKnown) {
			onReady();
		}
	}

	@Override
	public synchronized void onCompletion(MediaPlayer p_MediaPlayer) {
		m_IsPlaying = false;
		if (m_OnCompletionListener != null) {
			m_OnCompletionListener.onCompletion();
		}
	}

	@Override
	public boolean onError(MediaPlayer p_MediaPlayer, int p_What, int p_Extra) {
		return false;
	}

	private void onReady() {
		if (m_OnReadyToPlayListener != null) {
			m_OnReadyToPlayListener.onReady();
		}
	}

}

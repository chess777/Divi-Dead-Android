package com.csware.divi_dead.framework.impl;

import java.io.FileDescriptor;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.csware.divi_dead.framework.Music;

public class AndroidMusic implements Music, OnCompletionListener {

	private static final String	m_TAG			= "AndroidMusic";
	private MediaPlayer			m_MediaPlayer;
	private boolean				m_IsPrepared	= false;

	public AndroidMusic(String p_FileName) {
		m_MediaPlayer = new MediaPlayer();
		try {
			m_MediaPlayer.setDataSource(p_FileName);
			m_MediaPlayer.prepare();
			m_IsPrepared = true;
			m_MediaPlayer.setOnCompletionListener(this);
		}
		catch (Exception e) {
			Log.e(m_TAG, "Error: " + e.getMessage());
			throw new RuntimeException(m_TAG + " - Error: " + e.getMessage());
		}
	}

	public AndroidMusic(FileDescriptor p_FileDesc, long p_Offset, long p_Length) {
		m_MediaPlayer = new MediaPlayer();
		try {
			m_MediaPlayer.setDataSource(p_FileDesc, p_Offset, p_Length);
			m_MediaPlayer.prepare();
			m_IsPrepared = true;
			m_MediaPlayer.setOnCompletionListener(this);
		}
		catch (Exception e) {
			Log.e(m_TAG, "Error: " + e.getMessage());
			throw new RuntimeException(m_TAG + " - Error: " + e.getMessage());
		}
	}

	public void dispose() {
		if (m_MediaPlayer.isPlaying()) m_MediaPlayer.stop();
		m_MediaPlayer.release();
	}

	public boolean isLooping() {
		return m_MediaPlayer.isLooping();
	}

	public boolean isPlaying() {
		return m_MediaPlayer.isPlaying();
	}

	public boolean isStopped() {
		return !m_IsPrepared;
	}

	public void pause() {
		if (m_MediaPlayer.isPlaying()) m_MediaPlayer.pause();
	}

	public void play() {
		if (m_MediaPlayer.isPlaying()) return;
		try {
			synchronized (this) {
				if (!m_IsPrepared) m_MediaPlayer.prepare();
				m_MediaPlayer.start();
			}
		}
		catch (IllegalStateException e) {
			Log.e(m_TAG, "Error: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e) {
			Log.e(m_TAG, "Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setLooping(boolean isLooping) {
		m_MediaPlayer.setLooping(isLooping);
	}

	public void setVolume(float volume) {
		m_MediaPlayer.setVolume(volume, volume);
	}

	public void stop() {
		m_MediaPlayer.stop();
		synchronized (this) {
			m_IsPrepared = false;
		}
	}

	public void onCompletion(MediaPlayer player) {
		synchronized (this) {
			m_IsPrepared = false;
		}
	}
}

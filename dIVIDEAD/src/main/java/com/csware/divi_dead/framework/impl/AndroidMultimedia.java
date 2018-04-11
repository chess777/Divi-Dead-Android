package com.csware.divi_dead.framework.impl;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.Vitamio;

import java.io.FileDescriptor;
import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.SurfaceView;

import com.csware.divi_dead.framework.Multimedia;
import com.csware.divi_dead.framework.Music;
import com.csware.divi_dead.framework.Sound;
import com.csware.divi_dead.framework.Video;

public class AndroidMultimedia implements Multimedia {

	private static final String	m_TAG	= "AndroidMultimedia";
	private AssetManager		m_Assets;
	private SoundPool			m_SoundPool;
	private Activity			m_Activity;
	private SurfaceView			m_SurfaceView;

	public AndroidMultimedia(Activity p_Activity, SurfaceView p_SurfaceView) {
		m_Activity = p_Activity;
		m_SurfaceView = p_SurfaceView;
		m_Activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.m_Assets = m_Activity.getAssets();
		this.m_SoundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		
		// check if Vitamio decompresses decoding package
		Vitamio.initialize(p_Activity);
		if (!LibsChecker.checkVitamioLibs(p_Activity)) {
			Log.e(m_TAG, "Error initialising video libraries");
			throw new RuntimeException(m_TAG + " - Error initialising video libraries");
		}
	}

	public Music newMusic(String p_FileName) {
		return new AndroidMusic(p_FileName);
	}

	public Music newMusic(FileDescriptor p_FileDesc, long p_Offset,
			long p_Length) {
		return new AndroidMusic(p_FileDesc, p_Offset, p_Length);
	}

	public Sound newSound(String p_FileName) {
		try {
			AssetFileDescriptor assetDescriptor = m_Assets.openFd(p_FileName);
			int soundId = m_SoundPool.load(assetDescriptor, 0);
			return new AndroidSound(m_SoundPool, soundId);
		}
		catch (IOException e) {
			Log.e(m_TAG, "Couldn't load sound '" + p_FileName + "', error: "
					+ e.getMessage());
			throw new RuntimeException(m_TAG + "Couldn't load sound '"
					+ p_FileName + "', error: " + e.getMessage());
		}
	}

	public Video newVideo() {
		return new AndroidVideo(m_Activity, m_SurfaceView);
	}

}
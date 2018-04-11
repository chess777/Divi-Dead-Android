package com.csware.divi_dead.framework.impl;

import android.media.SoundPool;

import com.csware.divi_dead.framework.Sound;

public class AndroidSound implements Sound {

	int			m_SoundId;
	SoundPool	m_SoundPool;

	public AndroidSound(SoundPool p_SoundPool, int p_SoundId) {
		this.m_SoundId = p_SoundId;
		this.m_SoundPool = p_SoundPool;
	}

	public void play(float p_Volume) {
		m_SoundPool.play(m_SoundId, p_Volume, p_Volume, 0, 0, 1);
	}

	public void dispose() {
		m_SoundPool.unload(m_SoundId);
	}
}

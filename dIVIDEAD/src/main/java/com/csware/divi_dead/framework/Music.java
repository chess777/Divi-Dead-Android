package com.csware.divi_dead.framework;

public interface Music {
	public void play();

	public void stop();

	public void pause();

	public void setLooping(boolean p_Looping);

	public void setVolume(float p_Volume);

	public boolean isPlaying();

	public boolean isStopped();

	public boolean isLooping();

	public void dispose();
}

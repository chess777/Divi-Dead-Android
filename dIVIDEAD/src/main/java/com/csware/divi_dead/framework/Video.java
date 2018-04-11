package com.csware.divi_dead.framework;

public interface Video {

	public interface OnReadyToPlayListener {
		public void onReady();
	}

	public interface OnCompletionListener {
		public void onCompletion();
	}

	public void setVideoSource(String p_FileName);
	
	public void play();

	public void stop();

	public void pause();

	public int getWidth();

	public int getHeight();
	
	public long getCurrentPosition();
	
	public void seekTo(long p_Position);

	public void setVolume(float p_Volume);

	public void setVideoPosition(int p_x, int p_y, int p_Width, int p_Height);

	public void setOnReadyToPlayListener(OnReadyToPlayListener p_Listener);

	public void setOnCompletionListener(OnCompletionListener p_Listener);

	public boolean isPlaying();

	public boolean isStopped();

	public boolean isPaused();

	public void dispose();
}

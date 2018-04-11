package com.csware.divi_dead.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import com.csware.divi_dead.Assets;
import com.csware.divi_dead.framework.Multimedia;
import com.csware.divi_dead.framework.FileIO;
import com.csware.divi_dead.framework.Game;
import com.csware.divi_dead.framework.Graphics;
import com.csware.divi_dead.framework.Input;
import com.csware.divi_dead.framework.Screen;

public abstract class AndroidGame extends Activity implements Game,
		Handler.Callback {
	AndroidFastRenderView	m_RenderView;
	Graphics				m_Graphics;
	Multimedia				m_Multimedia;
	Input					m_Input;
	FileIO					m_FileIO;
	Screen					m_Screen;
	WakeLock				m_WakeLock;
	Handler					m_MainHandler;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle p_SavedInstanceState) {

		super.onCreate(p_SavedInstanceState);
		
		Bitmap frameBuffer;
		boolean isLandscape;
		float scaleX;
		float scaleY;
		float displayAr;
		PowerManager powerManager;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Set activity background to black
		this.getWindow().getDecorView()
				.setBackgroundColor(Color.argb(255, 0, 0, 0));

		isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		Assets.Configuration.Graphics.m_FrameBufferWidth = isLandscape ? getWindowManager()
				.getDefaultDisplay().getWidth() : getWindowManager()
				.getDefaultDisplay().getHeight();
		Assets.Configuration.Graphics.m_FrameBufferHeight = isLandscape ? getWindowManager()
				.getDefaultDisplay().getHeight() : getWindowManager()
				.getDefaultDisplay().getWidth();

		displayAr = (float) Assets.Configuration.Graphics.m_FrameBufferWidth
				/ Assets.Configuration.Graphics.m_FrameBufferHeight;
		Assets.Configuration.Graphics.m_DisplayAr = displayAr;
		Assets.Configuration.Graphics.m_ScaledScreenAr = (float) Assets.Configuration.Graphics.m_TARGET_SCREEN_WIDTH
				/ Assets.Configuration.Graphics.m_TARGET_SCREEN_HEIGHT;

		if (Assets.Configuration.Graphics.m_ScaledScreenAr < displayAr) { // Target
																			// display
																			// is
																			// wider
			// than the screen
			Assets.Configuration.Graphics.m_ScaledScreenHeight = Assets.Configuration.Graphics.m_FrameBufferHeight;
			Assets.Configuration.Graphics.m_ScaledScreenWidth = (int) ((float) Assets.Configuration.Graphics.m_ScaledScreenHeight * Assets.Configuration.Graphics.m_ScaledScreenAr);
		}
		else {
			Assets.Configuration.Graphics.m_ScaledScreenWidth = Assets.Configuration.Graphics.m_FrameBufferWidth;
			Assets.Configuration.Graphics.m_ScaledScreenHeight = (int) ((float) Assets.Configuration.Graphics.m_ScaledScreenWidth / Assets.Configuration.Graphics.m_ScaledScreenAr);
		}

		Assets.Configuration.Graphics.m_ScaledScreenPosX = (Assets.Configuration.Graphics.m_FrameBufferWidth - Assets.Configuration.Graphics.m_ScaledScreenWidth) / 2;
		Assets.Configuration.Graphics.m_ScaledScreenPosY = (Assets.Configuration.Graphics.m_FrameBufferHeight - Assets.Configuration.Graphics.m_ScaledScreenHeight) / 2;

		Assets.Configuration.Graphics.m_ScaledScreenRatio = (float) Assets.Configuration.Graphics.m_ScaledScreenWidth
				/ Assets.Configuration.Graphics.m_TARGET_SCREEN_WIDTH;

		frameBuffer = Bitmap.createBitmap(
				Assets.Configuration.Graphics.m_FrameBufferWidth,
				Assets.Configuration.Graphics.m_FrameBufferHeight,
				Config.ARGB_8888);

		scaleX = (float) Assets.Configuration.Graphics.m_FrameBufferWidth
				/ getWindowManager().getDefaultDisplay().getWidth();
		scaleY = (float) Assets.Configuration.Graphics.m_FrameBufferHeight
				/ getWindowManager().getDefaultDisplay().getHeight();

		m_MainHandler = new Handler(this);
		m_RenderView = new AndroidFastRenderView(this, frameBuffer);
		m_Graphics = new AndroidGraphics(getAssets(), frameBuffer);
		m_FileIO = new AndroidFileIO(this);
		//m_Multimedia = new AndroidMultimedia(this, m_RenderView);
		m_Input = new AndroidInput(this, m_RenderView, scaleX, scaleY);
		m_Screen = getStartScreen();
		setContentView(m_RenderView);

		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		m_WakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"AndroidGame");

	}

	@Override
	public void onResume() {
		super.onResume();

		m_WakeLock.acquire();
		m_Screen.resume();
		m_RenderView.resume();
	}

	@Override
	public void onPause() {
		super.onPause();

		m_WakeLock.release();
		m_RenderView.pause();
		m_Screen.pause();

		if (isFinishing()) m_Screen.dispose();
	}

	public Input getInput() {
		return m_Input;
	}

	public FileIO getFileIO() {
		return m_FileIO;
	}

	public Graphics getGraphics() {
		return m_Graphics;
	}

	public Multimedia getMultimedia() {
		return m_Multimedia;
	}

	public void setScreen(Screen p_Screen) {
		if (p_Screen == null) throw new IllegalArgumentException(
				"Screen must not be null");

		this.m_Screen.pause();
		this.m_Screen.dispose();
		p_Screen.resume();
		p_Screen.update(0);
		this.m_Screen = p_Screen;
	}

	public Screen getCurrentScreen() {
		return m_Screen;
	}

	public AndroidFastRenderView getRenderView() {
		return m_RenderView;
	}

	@Override
	public boolean handleMessage(Message p_Message) {
		switch (p_Message.what) {
		case 0: // Set layout parameters
			LayoutParams lp;

			lp = (LayoutParams) p_Message.obj;
			m_RenderView.setLayoutParams(lp);
			return true;
		}

		return false;
	}
	
	@Override
	public void initializeMultimedia(){
		m_Multimedia = new AndroidMultimedia(this, m_RenderView);
	}

}

package com.csware.divi_dead;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint.FontMetrics;
import android.util.Log;

import com.csware.divi_dead.framework.Game;
import com.csware.divi_dead.framework.Graphics;
import com.csware.divi_dead.framework.Graphics.PixmapFormat;
import com.csware.divi_dead.framework.impl.AndroidPixmap;
import com.csware.divi_dead.framework.impl.DL1Arhive;
import com.csware.divi_dead.framework.Screen;

public class LoadingScreen extends Screen {

	private final String	m_TAG			= "LoadingScreen";
	private float			m_TimePassed	= 0.0f;
	private int				m_CurrentStep	= 0;
	private final int		m_LAST_STEP		= 5;
	private boolean			m_SomethingDone	= false;
	BitmapFactory.Options	m_bfoOptions;

	public LoadingScreen(Game p_Game) {
		super(p_Game);

		Assets.m_Logo = m_Game.getGraphics().newAssetsPixmap("Logo.png",
				PixmapFormat.ARGB8888);
		Assets.m_GameFolder = p_Game.getFileIO().getExternalStoragePath()
				+ "Divi-Dead" + File.separator;
	}

	@Override
	public void update(float p_DeltaTime) {
		byte[] ba_Data;

		m_TimePassed += p_DeltaTime;

		if (m_TimePassed != 0) {
			switch (m_CurrentStep) {
			
			case 0:
				m_Game.initializeMultimedia();
				Assets.m_Chi = m_Game.getMultimedia().newSound("Chi.ogg");

				Assets.m_SgArhive = new DL1Arhive();
				Assets.m_SgArhive.loadArhive(Assets.m_GameFolder + "SG.DL1");
				m_SomethingDone = true;
				Log.v(m_TAG, "Loaded SG.DL1");
				break;

			case 1:
				Assets.m_WvArhive = new DL1Arhive();
				Assets.m_WvArhive.loadArhive(Assets.m_GameFolder + "WV.DL1");
				m_SomethingDone = true;
				Log.v(m_TAG, "Loaded WV.DL1");
				break;

			case 2:
				Assets.m_FList = new FList();
				Assets.m_FList.loadFList(Assets.m_SgArhive.getFileFromArhive(
						"FLIST", ""));
				m_SomethingDone = true;
				Log.v(m_TAG, "Loaded FLIST");
				break;

			case 3:
				ba_Data = Assets.m_SgArhive.getFileFromArhive("WAKU_P", "BMP");

				m_bfoOptions = new BitmapFactory.Options();
				m_bfoOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
				Assets.m_MenuGfx = new AndroidPixmap(
						BitmapFactory.decodeByteArray(ba_Data, 0,
								ba_Data.length, m_bfoOptions),
						PixmapFormat.ARGB8888);

				m_SomethingDone = true;
				Log.v(m_TAG, "Loaded WAKU_P");
				break;

			case 4:
				if (m_TimePassed > Assets.Configuration.Screens.m_LOADING_SCREEN_MIN_DELAY) {
					m_SomethingDone = true;
				}

				break;

			case 5:
				Log.v(m_TAG, "Loading done");
				m_Game.setScreen(new LogoScreen(m_Game));

				break;

			}

			if (m_SomethingDone) {
				m_CurrentStep++;
				m_SomethingDone = false;
			}
		}

	}

	@Override
	public void present(float p_deltaTime) {
		if (m_CurrentStep < m_LAST_STEP) {

			Graphics g;
			FontMetrics fm;
			String str;
			float strLength;
			float strHeight;
			float x, y;
			int w, h;

			g = m_Game.getGraphics();
			g.clear(Color.argb(255, 0, 0, 0));

			w = (int) ((float) Assets.m_Logo.getWidth() * Assets.Configuration.Graphics.m_ScaledScreenRatio);
			h = (int) ((float) Assets.m_Logo.getHeight() * Assets.Configuration.Graphics.m_ScaledScreenRatio);
			g.drawPixmap(Assets.m_Logo, (g.getWidth() - w) / 2,
					(g.getHeight() - h) / 2, w, h, 0, 0,
					Assets.m_Logo.getWidth(), Assets.m_Logo.getHeight());

			g.setTextSize(Assets.Configuration.Graphics.m_ScaledScreenRatio * 24);
			g.setColor(Color.argb(255, 255, 255, 255));

			str = Assets.Configuration.Screens.m_LOADING_SCREEN_STATUS_TEXT;
			strLength = g.measureText(str, 0, str.length());
			strHeight = g.getLineSpacing();
			fm = g.getFontMetrics();

			x = (Assets.Configuration.Graphics.m_FrameBufferWidth - strLength) / 2;
			y = Assets.Configuration.Graphics.m_FrameBufferHeight - strHeight
					* 2 + (-fm.ascent + fm.leading);

			g.drawText(str, 0, str.length() - 3
					+ ((int) (m_TimePassed / 0.25f) % 4), x, y);

		}
		else {
			m_Game.getGraphics().clear(Color.argb(255, 0, 0, 0));
		}
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

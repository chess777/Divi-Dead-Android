package com.csware.divi_dead;

import com.csware.divi_dead.framework.Arhive;
import com.csware.divi_dead.framework.Pixmap;
import com.csware.divi_dead.framework.Sound;

public class Assets {

	public static class Configuration {

		static public class Graphics {

			public static final int	m_TARGET_SCREEN_WIDTH	= 640;
			public static final int	m_TARGET_SCREEN_HEIGHT	= 480;

			public static int		m_FrameBufferWidth;
			public static int		m_FrameBufferHeight;
			public static int		m_ScaledScreenPosX;
			public static int		m_ScaledScreenPosY;
			public static int		m_ScaledScreenWidth;
			public static int		m_ScaledScreenHeight;
			public static float		m_DisplayAr;
			public static float		m_ScaledScreenAr;
			public static float		m_ScaledScreenRatio;

		}

		static public class Screens {

			public static final float	m_LOADING_SCREEN_MIN_DELAY		= 3.0f;
			public static final String	m_LOADING_SCREEN_STATUS_TEXT	= "Loading...";

		}

	}

	public static String	m_GameFolder;

	public static Pixmap	m_Logo;
	public static Pixmap	m_MenuGfx;

	public static Sound		m_Chi;

	public static Arhive	m_SgArhive;
	public static Arhive	m_WvArhive;

	public static FList		m_FList;

}

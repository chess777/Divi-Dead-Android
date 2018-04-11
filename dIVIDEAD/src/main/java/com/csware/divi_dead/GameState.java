package com.csware.divi_dead;

public class GameState {
	public String	m_ScriptName;
	public String	m_BgName;
	public String	m_FgName;
	public String	m_Reserved1;
	public String	m_Character1Name;
	public String	m_Character2Name;
	public String	m_FgOverlayName;
	public String	m_BgMusicName;
	public long		m_Reserved2;
	public long		m_CurrentPosition;
	public long		m_OldPosition;
	public short[]	m_Flags	= new short[2000];	// 0 - 999 temporary flags,
												// 1000 - 1999 persistent flags
}

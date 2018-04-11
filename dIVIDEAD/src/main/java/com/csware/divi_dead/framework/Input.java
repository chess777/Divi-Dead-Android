package com.csware.divi_dead.framework;

import java.util.List;

public interface Input {
	public static class KeyEvent {
		public static final int	KEY_DOWN	= 0;
		public static final int	KEY_UP		= 1;

		public int				m_Type;
		public int				m_KeyCode;
		public char				m_KeyChar;

		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (m_Type == KEY_DOWN) builder.append("key down, ");
			else builder.append("key up, ");
			builder.append(m_KeyCode);
			builder.append(",");
			builder.append(m_KeyChar);
			return builder.toString();
		}
	}

	public static class TouchEvent {
		public static final int	TOUCH_DOWN		= 0;
		public static final int	TOUCH_UP		= 1;
		public static final int	TOUCH_DRAGGED	= 2;

		public int				m_Type;
		public int				m_x, m_y;
		public int				m_Pointer;

		public String toString() {
			StringBuilder builder = new StringBuilder();
			if (m_Type == TOUCH_DOWN) builder.append("touch down, ");
			else
				if (m_Type == TOUCH_DRAGGED) builder.append("touch dragged, ");
				else builder.append("touch up, ");
			builder.append(m_Pointer);
			builder.append(",");
			builder.append(m_x);
			builder.append(",");
			builder.append(m_y);
			return builder.toString();
		}
	}

	public boolean isKeyPressed(int p_KeyCode);

	public boolean isTouchDown(int p_Pointer);

	public int getTouchX(int p_Pointer);

	public int getTouchY(int p_Pointer);

	public float getAccelX();

	public float getAccelY();

	public float getAccelZ();

	public List<KeyEvent> getKeyEvents();

	public List<TouchEvent> getTouchEvents();
}

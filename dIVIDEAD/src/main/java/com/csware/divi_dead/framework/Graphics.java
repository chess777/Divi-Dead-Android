package com.csware.divi_dead.framework;

import android.graphics.Paint.FontMetrics;

public interface Graphics {

	public static enum PixmapFormat {
		ARGB8888, ARGB4444, RGB565
	}

	public Pixmap newAssetsPixmap(String p_FileName, PixmapFormat p_Format);

	public void clear(int p_Color);

	public void drawPixel(int p_x, int p_y, int p_Color);

	public void drawLine(int p_x, int p_y, int p_x2, int p_y2, int p_Color);

	public void drawRect(int p_x, int p_y, int p_Width, int p_Height,
			int p_Color);

	public void drawPixmap(Pixmap p_Pixmap, int p_x, int p_y, int p_SrcX,
			int p_SrcY, int p_SrcWidth, int p_SrcHeight);

	public void drawPixmap(Pixmap p_Pixmap, int p_x, int p_y, int dstWidth,
			int dstHeight, int p_SrcX, int p_SrcY, int srcWidth, int srcHeight);

	public void drawPixmap(Pixmap p_Pixmap, int p_x, int p_y);

	public int getWidth();

	public int getHeight();

	public void setColor(int p_Color);

	public void setTextSize(float p_TextSize);

	public void drawText(String p_Text, int p_Start, int p_End, float p_x, float p_y);

	public void getTextWidth(String p_Text, float[] p_Widths);

	public float getLineSpacing();

	public FontMetrics getFontMetrics();

	public float measureText(String p_Text, int p_Start, int p_End);

	public int breakText(String p_Text, boolean p_MeasureForwards,
			float p_MaxWidth, float[] p_MeasuredWidth);

}

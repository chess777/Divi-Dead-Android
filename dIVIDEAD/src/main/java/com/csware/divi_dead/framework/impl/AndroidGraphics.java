package com.csware.divi_dead.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import com.csware.divi_dead.framework.Graphics;
import com.csware.divi_dead.framework.Pixmap;

public class AndroidGraphics implements Graphics {
	AssetManager	m_Assets;
	Bitmap			m_FrameBuffer;
	Canvas			m_Canvas;
	Paint			m_Paint;
	Rect			m_SrcRect	= new Rect();
	Rect			m_DstRect	= new Rect();

	public AndroidGraphics(AssetManager p_Assets, Bitmap p_FrameBuffer) {
		this.m_Assets = p_Assets;
		this.m_FrameBuffer = p_FrameBuffer;
		this.m_Canvas = new Canvas(p_FrameBuffer);
		this.m_Paint = new Paint();

		m_Paint.setAntiAlias(true);
		m_Paint.setFilterBitmap(true);
		m_Paint.setDither(true);

		m_Paint.setTypeface(Typeface.SERIF);
		m_Paint.setSubpixelText(true);
		m_Paint.setTextAlign(Align.LEFT);
		m_Paint.setStrokeWidth(1.0f);
	}

	public Pixmap newAssetsPixmap(String p_FileName, PixmapFormat p_Format) {
		Config config = null;
		InputStream in = null;
		Bitmap bitmap = null;

		if (p_Format == PixmapFormat.RGB565) config = Config.RGB_565;
		else
			if (p_Format == PixmapFormat.ARGB4444) config = Config.ARGB_4444;
			else config = Config.ARGB_8888;

		Options options = new Options();
		options.inPreferredConfig = config;

		try {
			in = m_Assets.open(p_FileName);
			bitmap = BitmapFactory.decodeStream(in);
			if (bitmap == null) throw new RuntimeException(
					"Couldn't load bitmap from asset '" + p_FileName + "'");
		}
		catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset '"
					+ p_FileName + "'");
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException e) {
				}
			}
		}

		if (bitmap.getConfig() == Config.RGB_565) p_Format = PixmapFormat.RGB565;
		else
			if (bitmap.getConfig() == Config.ARGB_4444) p_Format = PixmapFormat.ARGB4444;
			else p_Format = PixmapFormat.ARGB8888;

		return new AndroidPixmap(bitmap, p_Format);
	}

	public void clear(int p_Color) {
		m_Canvas.drawRGB((p_Color & 0xff0000) >> 16, (p_Color & 0xff00) >> 8,
				(p_Color & 0xff));
	}

	public void drawPixel(int p_x, int p_y, int p_Color) {
		m_Paint.setColor(p_Color);
		m_Canvas.drawPoint(p_x, p_y, m_Paint);
	}

	public void drawLine(int p_x, int p_y, int p_x2, int p_y2, int p_Color) {
		m_Paint.setColor(p_Color);
		m_Canvas.drawLine(p_x, p_y, p_x2, p_y2, m_Paint);
	}

	public void drawRect(int p_x, int p_y, int p_Width, int p_Height,
			int p_Color) {
		m_Paint.setColor(p_Color);
		m_Paint.setStyle(Style.FILL);
		m_Canvas.drawRect(p_x, p_y, p_x + p_Width - 1, p_y + p_Width - 1,
				m_Paint);
	}

	public void drawPixmap(Pixmap p_Pixmap, int p_x, int p_y, int p_SrcX,
			int p_SrcY, int srcWidth, int srcHeight) {
		m_SrcRect.left = p_SrcX;
		m_SrcRect.top = p_SrcY;
		m_SrcRect.right = p_SrcX + srcWidth - 1;
		m_SrcRect.bottom = p_SrcY + srcHeight - 1;

		m_DstRect.left = p_x;
		m_DstRect.top = p_y;
		m_DstRect.right = p_x + srcWidth - 1;
		m_DstRect.bottom = p_y + srcHeight - 1;

		m_Canvas.drawBitmap(((AndroidPixmap) p_Pixmap).m_Bitmap, m_SrcRect,
				m_DstRect, m_Paint);
	}

	public void drawPixmap(Pixmap p_Pixmap, int p_x, int p_y, int dstWidth,
			int dstHeight, int p_SrcX, int p_SrcY, int srcWidth, int srcHeight) {
		m_SrcRect.left = p_SrcX;
		m_SrcRect.top = p_SrcY;
		m_SrcRect.right = p_SrcX + srcWidth - 1;
		m_SrcRect.bottom = p_SrcY + srcHeight - 1;

		m_DstRect.left = p_x;
		m_DstRect.top = p_y;
		m_DstRect.right = p_x + dstWidth - 1;
		m_DstRect.bottom = p_y + dstHeight - 1;

		m_Canvas.drawBitmap(((AndroidPixmap) p_Pixmap).m_Bitmap, m_SrcRect,
				m_DstRect, m_Paint);
	}

	public void drawPixmap(Pixmap p_Pixmap, int p_x, int p_y) {
		m_Canvas.drawBitmap(((AndroidPixmap) p_Pixmap).m_Bitmap, p_x, p_y,
				m_Paint);
	}

	public int getWidth() {
		return m_FrameBuffer.getWidth();
	}

	public int getHeight() {
		return m_FrameBuffer.getHeight();
	}

	public void setColor(int p_Color) {
		m_Paint.setColor(p_Color);
	}

	public void setTextSize(float p_TextSize) {
		m_Paint.setTextSize(p_TextSize);
	}

	public void drawText(String pText, int p_Start, int p_End, float p_x,
			float p_y) {
		m_Canvas.drawText(pText, p_Start, p_End, p_x, p_y, m_Paint);
	}

	@Override
	public void getTextWidth(String p_Text, float[] p_Widths) {
		m_Paint.getTextWidths(p_Text, p_Widths);
	}

	@Override
	public float getLineSpacing() {
		return m_Paint.getFontSpacing();
	}

	@Override
	public float measureText(String p_Text, int p_Start, int p_End) {
		return m_Paint.measureText(p_Text, p_Start, p_End);
	}

	@Override
	public int breakText(String p_Text, boolean p_MeasureForwards,
			float p_MaxWidth, float[] p_MeasuredWidth) {
		return m_Paint.breakText(p_Text, p_MeasureForwards, p_MaxWidth,
				p_MeasuredWidth);
	}

	@Override
	public FontMetrics getFontMetrics() {
		return m_Paint.getFontMetrics();
	}

}

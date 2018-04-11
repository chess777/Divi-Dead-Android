package com.csware.divi_dead.framework.impl;

import android.graphics.Bitmap;

import com.csware.divi_dead.framework.Graphics.PixmapFormat;
import com.csware.divi_dead.framework.Pixmap;

public class AndroidPixmap implements Pixmap {
	Bitmap			m_Bitmap;
	PixmapFormat	m_Format;

	public AndroidPixmap(Bitmap p_Bitmap, PixmapFormat p_Format) {
		this.m_Bitmap = p_Bitmap;
		this.m_Format = p_Format;
	}

	public Bitmap getBitmap() {
		return m_Bitmap;
	}
	
	public int getWidth() {
		return m_Bitmap.getWidth();
	}

	public int getHeight() {
		return m_Bitmap.getHeight();
	}

	public PixmapFormat getFormat() {
		return m_Format;
	}

	public void dispose() {
		m_Bitmap.recycle();
	}
	
}

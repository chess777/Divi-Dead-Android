package com.csware.divi_dead.framework;

import android.graphics.Bitmap;

import com.csware.divi_dead.framework.Graphics.PixmapFormat;

public interface Pixmap {
	
	public Bitmap getBitmap();
	
	public int getWidth();
	
	public int getHeight();
	
	public PixmapFormat getFormat();
	
	public void dispose();
	
}

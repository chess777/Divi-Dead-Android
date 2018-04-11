package com.csware.divi_dead.framework.impl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

@SuppressLint("ViewConstructor")
public class AndroidFastRenderView extends SurfaceView implements Runnable {
	AndroidGame			m_Game;
	Bitmap				m_Framebuffer;
	Thread				m_RenderThread	= null;
	volatile boolean	m_Running		= false;
	private boolean		m_EnableRedraw	= true;

	public AndroidFastRenderView(AndroidGame p_Game, Bitmap p_FrameBuffer) {
		super(p_Game);

		this.m_Game = p_Game;
		this.m_Framebuffer = p_FrameBuffer;
	}

	public void resume() {
		m_Running = true;
		m_RenderThread = new Thread(this);
		m_RenderThread.start();
	}

	public void run() {
		Canvas canvas;
		Rect dstRect;
		long startTime;
		float deltaTime;

		dstRect = new Rect();
		startTime = System.nanoTime();
		while (m_Running) {
			if (!this.getHolder().getSurface().isValid()) continue;

			deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
			startTime = System.nanoTime();

			m_Game.getCurrentScreen().update(deltaTime);
			m_Game.getCurrentScreen().present(deltaTime);

			if (m_EnableRedraw) {
				canvas = this.getHolder().lockCanvas();
				if(canvas != null) {
					canvas.getClipBounds(dstRect);
					canvas.drawBitmap(m_Framebuffer, null, dstRect, null);
					this.getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	public void pause() {
		m_Running = false;
		while (true) {
			try {
				m_RenderThread.join();
				return;
			}
			catch (InterruptedException e) {
				// retry
			}
		}
	}

	public void enableRedraw() {
		m_EnableRedraw = true;
	}

	public void disableRedraw() {
		m_EnableRedraw = false;
	}

}

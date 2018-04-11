package com.csware.divi_dead.framework;

import com.csware.divi_dead.framework.impl.AndroidFastRenderView;

public interface Game {

	public Input getInput();

	public FileIO getFileIO();

	public Graphics getGraphics();

	public Multimedia getMultimedia();

	public AndroidFastRenderView getRenderView();

	public void setScreen(Screen p_Screen);

	public Screen getCurrentScreen();

	public Screen getStartScreen();

	public void initializeMultimedia();

}
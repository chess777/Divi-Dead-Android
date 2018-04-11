package com.csware.divi_dead;

import com.csware.divi_dead.framework.Screen;
import com.csware.divi_dead.framework.impl.AndroidGame;

public class DiviDeadGame extends AndroidGame {

	@Override
	public Screen getStartScreen() {
		return new LoadingScreen(this);
	}

}

package com.csware.divi_dead.framework;

public abstract class Screen {
	protected final Game	m_Game;

	public Screen(Game p_Game) {
		this.m_Game = p_Game;
	}

	public abstract void update(float p_DeltaTime);

	public abstract void present(float p_DeltaTime);

	public abstract void pause();

	public abstract void resume();

	public abstract void dispose();
}

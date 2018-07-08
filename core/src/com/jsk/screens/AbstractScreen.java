package com.jsk.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.jsk.utils.GamePreferences;

public abstract class AbstractScreen extends InputAdapter implements Screen {

	protected DirectedGame game;

	public abstract InputProcessor getInputProcessor();

	public AbstractScreen(DirectedGame game) {
		this.game = game;
	}

	protected void setScreen(AbstractScreen screen) {
		game.setScreen(screen);
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
		GamePreferences.instance.save();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}

package com.tfg.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;

public class AbstractScreen extends InputAdapter implements Screen{

	protected Game game;
	
	
	public AbstractScreen(Game game) {
		this.game = game;
	}
	
	protected void setScreen(Screen screen){
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
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	

}

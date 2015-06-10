package com.tfg.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.tfg.utils.Assets;
import com.tfg.utils.GamePreferences;

public abstract class AbstractScreen extends InputAdapter implements Screen{

//	protected Game game;
	protected DirectedGame game;
	
	
//	public AbstractScreen(Game game) {
//		this.game = game;
//	}
	
	
	public abstract InputProcessor getInputProcessor();
	
	public AbstractScreen(DirectedGame game) {
		this.game = game;
	}
	
	protected void setScreen(AbstractScreen screen){
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
		System.out.println("Saving progress!");
		GamePreferences.instance.save();
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		Assets.manager.dispose();
	}
}

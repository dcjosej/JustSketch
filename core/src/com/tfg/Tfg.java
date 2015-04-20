package com.tfg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.tfg.screens.GameScreen;
import com.tfg.screens.Level6Screen;
import com.tfg.screens.MenuScreen;
import com.tfg.utils.GameManager;

public class Tfg extends Game {

	@Override
	public void create() {
		GdxNativesLoader.load();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
//		if (GameManager.gameOver) {
//			GameManager.restartLevel();
//			setScreen(new GameScreen(this));
//		}
	}

}
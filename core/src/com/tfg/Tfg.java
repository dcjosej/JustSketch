package com.tfg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.tfg.screens.Level3PruebaScreen;
import com.tfg.utils.GameManager;

public class Tfg extends Game {

	@Override
	public void create() {
		GdxNativesLoader.load();
		setScreen(new Level3PruebaScreen());
	}

	@Override
	public void render() {
		super.render();

		if (GameManager.gameOver) {
			GameManager.restartLevel();
			setScreen(new Level3PruebaScreen());
		}
	}

}
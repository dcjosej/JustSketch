package com.tfg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.tfg.screens.GameScreen;
import com.tfg.screens.SelectLevelScreen;
import com.tfg.utils.Assets;
import com.tfg.utils.GamePreferences;

public class Tfg extends Game {

	@Override
	public void create() {
		GdxNativesLoader.load();
		GamePreferences.instance.load();
		
		Assets.loadLevel1Asset();
		while (!Assets.updateAssets()) {
		}
		setScreen(new SelectLevelScreen(this));
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
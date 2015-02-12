package com.tfg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.tfg.screens.Level1Screen;
import com.tfg.utils.GameManager;

public class Tfg extends Game{

	@Override
	public void create() {
		GdxNativesLoader.load();
		setScreen(new Level1Screen());
	}

	@Override
	public void render() {
		super.render();
		
		if(GameManager.gameOver){
			GameManager.restartLevel();
			setScreen(new Level1Screen());
		}
	}
	
	
}
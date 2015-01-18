package com.tfg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.tfg.screens.Level1Screen;

public class Tfg extends Game{

	@Override
	public void create() {
		GdxNativesLoader.load();
		setScreen(new Level1Screen());
	}
	
}

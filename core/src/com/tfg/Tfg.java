package com.tfg;

import com.tfg.screens.DirectedGame;
import com.tfg.screens.MenuScreen;
import com.tfg.utils.Assets;
import com.tfg.utils.GamePreferences;

public class Tfg extends DirectedGame {

	@Override
	public void create() {
//		GdxNativesLoader.load();
		GamePreferences.instance.load();
		
		Assets.loadAssets();
		while (!Assets.updateAssets()) {
		}
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}
}
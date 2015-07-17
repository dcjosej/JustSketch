package com.jsk;

import com.jsk.ads.ActionResolver;
import com.jsk.screens.DirectedGame;
import com.jsk.screens.MenuScreen;
import com.jsk.utils.Assets;
import com.jsk.utils.GamePreferences;

public class Tfg extends DirectedGame {

	public static ActionResolver actionResolver = null;
	
	public Tfg(ActionResolver actionResolver){
		Tfg.actionResolver = actionResolver;
	}
	
	public Tfg(){
	}
	
	@Override
	public void create() {
		// GdxNativesLoader.load();
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
	
	@Override
	public void resume(){
		System.out.println("-------------- RESUME ON SUPER MASTER CLASS!!!! -------------------");
	}
}
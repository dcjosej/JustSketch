package com.tfg.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {
	
	
	public static final GamePreferences instance = new GamePreferences();
	
	private Preferences prefs;
	public int numLevelsActive;
	
	private GamePreferences(){
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}
	
	public void load(){
		numLevelsActive = prefs.getInteger("numLevelsActive", 1);
	}
	
	public void save(int level, int score){
		prefs.putInteger("bestScore-" + level, score);
		prefs.flush();
	}
	
	
	public int getBestScore(int level){
		int res = prefs.getInteger("bestScore-" + level, 0);
		return res;
	}

	public void save() {
		prefs.putInteger("numLevelsActive", numLevelsActive);
		prefs.flush();
	}
	
}

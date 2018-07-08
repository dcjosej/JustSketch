package com.jsk.utils;

public class GameManager {
	
	private static GameManager instance = null;
	
	public boolean gameOver = false;
	public GameState gameState = GameState.PLAYING_LEVEL;
	public int currentLevel = 5;
	public boolean isPaused = false;
	public int numAttempts = 0;
	
	
	public static GameManager getInstance(){
		if(instance == null){
			instance = new GameManager();
		}
		
		return instance;
	}
	
	public void restartLevel(){
		gameOver = false;
	}
}
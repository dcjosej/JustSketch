package com.jsk.utils;

public class GameManager {
	public static boolean gameOver = false;
	public static GameState gameState = GameState.PLAYING_LEVEL;
	public static int currentLevel = 5;
	public static boolean isPaused = false;
	public static int numAttempts = 0;
	
	public static void restartLevel(){
		gameOver = false;
	}
}
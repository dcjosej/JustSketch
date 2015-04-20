package com.tfg.utils;

public class GameManager {
	public static boolean gameOver = false;
	public static GameState gameState = GameState.PLAYING_LEVEL;
	public static int currentLevel = 2;
	public static boolean isPaused = false;
	
	public static void restartLevel(){
		gameOver = false;
	}
}
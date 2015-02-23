package com.tfg.utils;

public class GameManager {
	public static boolean gameOver = false;
	public static GameState gameState = GameState.PLAYING_LEVEL;
	
	public static void restartLevel(){
		gameOver = false;
	}
}

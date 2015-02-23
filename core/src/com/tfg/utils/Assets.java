package com.tfg.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
	public static AssetManager manager = new AssetManager();
	
	public static void loadLevel1Asset(){
		manager.load(Constants.STROKE_TEXTURE, Texture.class);
		manager.load(Constants.PLATFORM1_TEXTURE, Texture.class);
		manager.load(Constants.PLATFORM2_TEXTURE, Texture.class);
		manager.load(Constants.BALL_TEXTURE, Texture.class);
		manager.load(Constants.FLAG_TEXTURE, Texture.class);
		manager.load(Constants.BACKGROUND_TEXTURE, Texture.class);
	}
	
	public static boolean updateAssets(){
		return manager.update();
	}
	
	public static Texture getTexture(String fileName){
		return manager.get(fileName, Texture.class);
	}
	
	
}

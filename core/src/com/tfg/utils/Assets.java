package com.tfg.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static AssetManager manager = new AssetManager();

	// TODO: Cambiar nombre del este metodo
	public static void loadLevel1Asset() {
		// manager.load(Constants.STROKE_TEXTURE, Texture.class);
		// manager.load(Constants.PLATFORM1_TEXTURE, Texture.class);
		// manager.load(Constants.PLATFORM2_TEXTURE, Texture.class);
		// manager.load(Constants.PLATFORM3_TEXTURE, Texture.class);
		// manager.load(Constants.BALL_TEXTURE, Texture.class);
		// manager.load(Constants.FLAG_TEXTURE, Texture.class);
		// manager.load(Constants.BACKGROUND_TEXTURE, Texture.class);
		// manager.load(Constants.BOUNCE_PLATFORM, Texture.class);
		//
		// //LEVEL 3
		// manager.load(Constants.LEVEL3_BLUE_FLAG, Texture.class);
		// manager.load(Constants.LEVEL3_SPIKES, Texture.class);
		// manager.load(Constants.LEVEL3_STEEL_DOOR, Texture.class);
		// manager.load(Constants.LEVEL3_WALL, Texture.class);
		// manager.load(Constants.LEVEL3_PLATFORM2, Texture.class);
		// manager.load(Constants.LEVEL3_BUTTON_UP, Texture.class);
		// manager.load(Constants.LEVEL3_BUTTON_DOWN, Texture.class);
		manager.load(Constants.LEVEL_ATLAS, TextureAtlas.class);

		// ----------- SOUNDS AND MUSIC ----------------------
		manager.load(Constants.LEVEL2_BACKGROUND_MUSIC, Music.class);
		manager.load(Constants.JUMP_EFFECT, Sound.class);
		manager.load(Constants.DRAW_EFFECT, Sound.class);
		manager.load(Constants.EXPLOSION_EFFECT, Sound.class);
	}

	public static boolean updateAssets() {
		return manager.update();
	}

	public static Texture getTexture(String fileName) {
		return manager.get(fileName, Texture.class);
	}

	public static TextureRegion getTextureRegion(String region) {
		return manager.get(Constants.LEVEL_ATLAS, TextureAtlas.class)
				.findRegion(region);
	}

	public static Music getMusic(String level2BackgroundMusic) {
		return manager.get(level2BackgroundMusic, Music.class);
	}

	public static Sound getSound(String jumpEffect) {
		return manager.get(jumpEffect, Sound.class);
	}
}
package com.tfg.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {
	public static AssetManager manager = new AssetManager();

	public static void loadAssets() {

		// ------------ SKINS AND ATLAS -------------------------
		manager.load(Constants.LEVEL_ATLAS, TextureAtlas.class);
		manager.load(Constants.GUI_ATLAS, TextureAtlas.class);
		manager.load(Constants.GUI_SKIN, Skin.class,
				new SkinLoader.SkinParameter("images/gui.pack"));

		// ----------- SOUNDS AND MUSIC ----------------------
		manager.load(Constants.BACKGROUND_MUSIC, Music.class);
		manager.load(Constants.JUMP_EFFECT, Sound.class);
		manager.load(Constants.DRAW_EFFECT, Sound.class);
		manager.load(Constants.EXPLOSION_EFFECT, Sound.class);

		// ------------ FONTS ---------------------------------
		manager.load(Constants.GUI_FONT_40, BitmapFont.class);
		manager.load(Constants.GUI_FONT_44, BitmapFont.class);
		manager.load(Constants.GUI_FONT_60, BitmapFont.class);
		manager.load(Constants.GUI_FONT_80, BitmapFont.class);
	}

	public static boolean updateAssets() {
		return manager.update();
	}

	public static BitmapFont getBitmapFont(String fontFile) {
		return manager.get(fontFile, BitmapFont.class);
	}

	public static Skin getSkinGui() {
		return manager.get(Constants.GUI_SKIN, Skin.class);
	}

	public static TextureAtlas getGUITextureAtlas() {
		return manager.get(Constants.GUI_ATLAS, TextureAtlas.class);
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
package com.jsk.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	public static final int APP_WIDTH = 1920;
	public static final int APP_HEIGHT = 1080;
	public static final float UNIT_SCALE = 1 / 32f;
	public static final int NUM_LEVELS = 6;
	public static final int MAX_STROKES = 9;

	//-------------------- PHYSICS -----------------------------
	public static final Vector2 WORLD_GRAVITY = new Vector2(0, -15f);
	public static final float  JUMP_BALL_VELOCITY_Y = 11f;
	

	public static final float DST_TOLERANCE = 0.5f;
	public static final float DST_TOLERANCE_ANDROID = 0.8f;
	
	public static final int MAX_POINTS = 150;
	public static final float MAX_DISTANCE = 20f;
	public static final int NUM_ITERATIONS = 1;
	public static final float THICKNESS = 0.08f;
	public static final int MAX_POINTS_DELETE = 100;

	public static final String STROKE_TEXTURE = "pencil_texture.jpg";

	public static final float BALL_LINEAR_VELOCITY = 0f;

	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	// Location of description file for skins
	public static final String SKIN_UI = "images/ui.json";
	public static final String LEVEL_ATLAS = "images/levelAtlas.pack";
	public static final String GUI_ATLAS = "images/gui.pack";
	
	public static final Color TINT_COLOR = Color.DARK_GRAY;
	
	//------------------ SOUNDS AND MUSIC -----------------------
	public static final String BACKGROUND_MUSIC = "music/DST-ALightIntro.mp3";
	public static final String JUMP_EFFECT = "sfx/jump2.wav";
	public static final String DRAW_EFFECT = "sfx/draw3.wav";
	public static final String EXPLOSION_EFFECT = "sfx/explosion1.wav";
	
	//------------------ FONTS -----------------------
	public static final String GUI_FONT_40 = "fonts/djgross-40.fnt";
	public static final String GUI_FONT_44 = "fonts/djgross-44.fnt";
	public static final String GUI_FONT_60 = "fonts/djgross-60.fnt";
	public static final String GUI_FONT_80 = "fonts/djgross-80.fnt";
	
	//-------------------- PREFERENCES ------------------------------
	public static final String PREFERENCES = "my-preferences";
	
	//------------------ DEBUG ----------------------------------
	public static final float DEBUG_REBUILD_INTERVAL = 4f;
	public static final String GUI_SKIN = "images/ui.json";
}
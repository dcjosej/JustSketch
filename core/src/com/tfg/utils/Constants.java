package com.tfg.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	public static final int APP_WIDTH = 1920;
	public static final int APP_HEIGHT = 1080;
	public static final float UNIT_SCALE = 1 / 32f;
	public static final int NUM_LEVELS = 10;

	public static Vector2 WORLD_GRAVITY = new Vector2(0, -15f);

	public static final float GROUND_X = 0;
	public static final float GROUND_Y = 0;
	public static final float GROUND_WIDTH = 50f;
	public static final float GROUND_HEIGHT = 4f;
	public static final float GROUND_DENSITY = 0f;

	public static final float DST_TOLERANCE = 0.3f;
	public static final int MAX_POINTS = 300;
	public static final int NUM_ITERATIONS = 1;
	public static final float THICKNESS = 0.08f;

	public static final String STROKE_TEXTURE = "pencil_texture.jpg";

	/* TODO LIMPIAR TEXTURAS */
	public static final String PLATFORM1_TEXTURE = "platform3.png";
	public static final String PLATFORM2_TEXTURE = "NormalPlatform.png";
	public static final String PLATFORM3_TEXTURE = "_0000_Layer-4.png";
	public static final String BOUNCE_PLATFORM = "BouncePlatform.png";
	public static final String BALL_TEXTURE = "ballDef.png";
	public static final String FLAG_TEXTURE = "_0002_DefFlag.png";
	public static final String BACKGROUND_TEXTURE = "background1.png";

	public static final float BALL_LINEAR_VELOCITY = 0f;

	// TODO: CAMBIAR URGENTE TODOS LOS NOMBRES!!!!!!
	public static final String LEVEL3_BACKGROUND = "maps/level3/Level3_Background.png";
	public static final String LEVEL3_BLUE_FLAG = "maps/level3/Level3_flag.png";
	public static final String LEVEL3_SP_PLATFORM_1 = "maps/level3/Level3_platform1.png";
	public static final String LEVEL3_SP_PLATFORM_2 = "maps/level3/Level3_platform2.png";
	public static final String LEVEL3_SPIKES = "maps/level3/Level3_Spikes.png";
	public static final String LEVEL3_STEEL_DOOR = "maps/level3/Level3_SteelDoor.png";
	public static final String LEVEL3_WALL = "maps/level3/Level3_platform_wall.png";
	public static final String LEVEL3_PLATFORM2 = "maps/level3/Level3_Platform4.png";
	public static final String LEVEL3_BUTTON_UP = "maps/level3/Level3_ButtonUp.png";
	public static final String LEVEL3_BUTTON_DOWN = "maps/level3/Level3_ButtonDown.png";

	//
	
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	// Location of description file for skins
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	public static final String SKIN_UI = "images/ui.json";
	public static final String LEVEL_ATLAS = "images/levelAtlas.pack";
	public static final String GUI_ATLAS = "images/gui.pack";
	
	
	public static final Color TINT_COLOR = Color.DARK_GRAY;
	
	//------------------ SOUNDS AND MUSIC -----------------------
	public static final String LEVEL2_BACKGROUND_MUSIC = "music/DST-ALightIntro.mp3";
	public static final String JUMP_EFFECT = "sfx/jump2.wav";
	public static final String DRAW_EFFECT = "sfx/draw3.wav";
	public static final String EXPLOSION_EFFECT = "sfx/explosion1.wav";
	
	
	//-------------------- PREFERENCES ------------------------------
	public static final String PREFERENCES = "my-preferences";
	
	//------------------ DEBUG ----------------------------------
	public static final float DEBUG_REBUILD_INTERVAL = 4f;
	
}

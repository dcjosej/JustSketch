package com.tfg.utils;

import com.badlogic.gdx.math.Vector2;


public class Constants {
	public static int APP_WIDTH = 800;
	public static int APP_HEIGHT = 480;
	public static int WORLD_TO_SCREEN = 32; /*TODO DELETE THIS FIELD*/
	
	public static Vector2 WORLD_GRAVITY = new Vector2(0, -10f);
	
    public static final float GROUND_X = 0;
    public static final float GROUND_Y = 0;
    public static final float GROUND_WIDTH = 25f;
    public static final float GROUND_HEIGHT = 2f;
    public static final float GROUND_DENSITY = 0f;
    
    public static final float DST_TOLERANCE = 0.2f;
    public static final int MAX_POINTS = 100;
    public static final int NUM_ITERATIONS = 1;
	public static final float THICKNESS = 0.02f;
	public static final String STROKE_TEXTURE = "pencil_texture.jpg";
	
	public static final float BALL_LINEAR_VELOCITY = 1.4f;
}	

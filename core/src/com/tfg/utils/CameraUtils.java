package com.tfg.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraUtils {
	
	public static Vector2 unproject(int screenX, int screenY, Camera camera){
		Vector3 point = camera.unproject(new Vector3(screenX, screenY, 0f));
		Vector2 res = new Vector2(point.x, point.y);
		return res;
	}
}

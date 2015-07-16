package com.jsk.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraHelper {
	
	private OrthographicCamera camera;
	
	public boolean translateRight = false;
	public boolean translateLeft = false;
	
	public CameraHelper(Camera camera){
		this.camera = (OrthographicCamera) camera;
	}
	
	public void setCamera(Camera camera){
		this.camera = (OrthographicCamera) camera;
	}
	
	public static Vector2 unproject(int screenX, int screenY, Camera camera){
		Vector3 point = camera.unproject(new Vector3(screenX, screenY, 0f));
		Vector2 res = new Vector2(point.x, point.y);
		return res;
	}
	
	public void update(float delta){
		if(translateRight){
			camera.position.lerp(new Vector3(60, camera.position.y, camera.position.z), 2f * delta);
		}
		
		if(translateLeft){
			camera.position.lerp(new Vector3(30, camera.position.y, camera.position.z), 2f * delta);
		}
	}
}
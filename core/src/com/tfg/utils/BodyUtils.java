package com.tfg.utils;

import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.box2d.UserData;
import com.tfg.box2d.UserDataType;

public class BodyUtils {
	public static boolean isBall(Body body){
		UserData userData = (UserData)body.getUserData();
		return userData == null ? false : userData.getUserDataType() == UserDataType.BALL;
	}
	
	public static boolean isMortalObstacle(Body body){
		UserData userData = (UserData)body.getUserData();
		return userData.getUserDataType() == UserDataType.MORTAL_OBSTACLE;
	}
	
	
	public static boolean isBouncePlatform(Body body) {
		UserData userData = (UserData)body.getUserData();
		return userData.getUserDataType() == UserDataType.BOUNCE_PLATFORM;
	}
	
	
	public static boolean isFlag(Body body) {
		UserData userData = (UserData)body.getUserData();
		return userData.getUserDataType() == UserDataType.FLAG;
	}

	public static boolean isStroke(Body body) {
		UserData userData = (UserData)body.getUserData();
		return userData.getUserDataType() == UserDataType.STROKE;
	}
}
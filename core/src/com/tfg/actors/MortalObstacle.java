package com.tfg.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.box2d.UserDataType;

public class MortalObstacle extends GameActor{

	public MortalObstacle(Body body, Rectangle rectangle) {
		super(body, rectangle);
		this.userDataType = UserDataType.MORTAL_OBSTACLE;
	}
	
}

package com.tfg.actors;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.box2d.UserDataType;
import com.tfg.utils.GameManager;

public class Ball extends CircleGameActor{
	
	private UserDataType userDataType;
	
	public Ball(Body body, Circle circle) {
		super(body, circle);
		this.userDataType = UserDataType.BALL;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if(circle.y < -15f){
			GameManager.gameOver = true;
		}
	}
}

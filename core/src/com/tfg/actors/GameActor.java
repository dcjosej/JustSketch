package com.tfg.actors;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class GameActor extends SuperGameActor {
	protected Rectangle rectangle;

	public GameActor(Body body, Rectangle rectangle) {
		super(body);
		this.rectangle = rectangle;
	}

	protected void updateActor() {
		rectangle.x = body.getPosition().x - rectangle.width / 2;
		rectangle.y = body.getPosition().y - rectangle.height / 2;
	}
}
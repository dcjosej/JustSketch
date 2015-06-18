package com.tfg.actors;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;

public abstract class CircleGameActor extends SuperGameActor {
	protected Circle circle;

	public CircleGameActor(Body body, Circle circle) {
		super(body);
		this.circle = circle;
	}

	public Circle getCircle() {
		return circle;
	}

	protected void updateActor() {
		circle.x = body.getPosition().x;
		circle.y = body.getPosition().y;
	}
}
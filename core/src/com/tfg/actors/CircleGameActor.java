package com.tfg.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;

public abstract class CircleGameActor extends Actor {
	protected Body body;
	protected Circle circle;
	protected Color tint;

	public CircleGameActor(Body body, Circle circle) {
		this.body = body;
		this.circle = circle;
		this.tint = Color.GRAY;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		if (GameManager.isPaused) {
			tint = Constants.TINT_COLOR;
		} else{
			tint = Color.WHITE;
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateActor();
	}

	public Circle getCircle() {
		return circle;
	}

	public Body getBody() {
		return body;
	}

	private void updateActor() {
		circle.x = body.getPosition().x;
		circle.y = body.getPosition().y;
	}
}
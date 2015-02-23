package com.tfg.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tfg.utils.GameManager;
import com.tfg.utils.GameState;

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
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (GameManager.gameState == GameState.PLAYING_LEVEL) {
			tint = Color.WHITE;
		} else if (GameManager.gameState == GameState.WIN_LEVEL) {
			tint = Color.GRAY;
		}

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
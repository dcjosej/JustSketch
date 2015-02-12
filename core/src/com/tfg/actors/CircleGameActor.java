package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class CircleGameActor extends Actor {
	protected Body body;
	protected Circle circle;

	public CircleGameActor(Body body, Circle circle) {
		this.body = body;
		this.circle = circle;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		
		updateActor();
	}
	
	public Circle getCircle(){
		return circle;
	}
	
	public Body getBody(){
		return body;
	}

	private void updateActor() {
		circle.x = body.getPosition().x;
		circle.y = body.getPosition().y;
	}
}
package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tfg.box2d.UserDataType;

public abstract class GameActor extends Actor {
	protected Body body;
	protected Rectangle rectangle;
	protected UserDataType userDataType;

	public GameActor(Body body, Rectangle rectangle) {
		this.body = body;
		this.rectangle = rectangle;
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

	private void updateActor() {
		rectangle.x = body.getPosition().x - rectangle.width/2;
		rectangle.y = body.getPosition().y - rectangle.height/2;
	}
}
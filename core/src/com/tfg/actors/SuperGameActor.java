package com.tfg.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;

public abstract class SuperGameActor extends Actor{
	
	protected Body body;
	protected Color tint;
	protected Sprite sprite;
	
	public SuperGameActor(Body body) {
		this.body = body;
		this.tint = Color.WHITE;
	}
	
	protected abstract void updateActor();
	
	@Override
	public void act(float delta) {
		super.act(delta);

		if (GameManager.isPaused) {
			tint = Constants.TINT_COLOR;
		} else {
			tint = Color.WHITE;
		}
		updateActor();
	}
	
	public Body getBody() {
		return body;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
}
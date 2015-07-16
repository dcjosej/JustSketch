package com.jsk.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.jsk.utils.Assets;

public class BouncePlatform extends GameActor {

	public BouncePlatform(Body body, Rectangle rectangle) {
		super(body, rectangle);

		sprite = new Sprite(Assets.getTextureRegion("bouncePlatform"));
		sprite.setSize(this.rectangle.getWidth(), this.rectangle.getHeight());
		sprite.setPosition(this.rectangle.x, this.rectangle.y);
	}

	
	public void applyUpImpulse(Body body){
		body.applyForceToCenter(new Vector2(0, 30f), true);
	}
}

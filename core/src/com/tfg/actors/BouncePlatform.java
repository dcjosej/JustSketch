package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class BouncePlatform extends GameActor {

	private Sprite sprite;

	public BouncePlatform(Body body, Rectangle rectangle) {
		super(body, rectangle);

		sprite = new Sprite(Assets.getTextureRegion("bouncePlatform"));
		sprite.setSize(this.rectangle.getWidth(), this.rectangle.getHeight());
		sprite.setPosition(this.rectangle.x, this.rectangle.y);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
	
	/*TODO REVISAR GRAMATICA*/
	public void applyUpImpulse(Body body){
		body.applyForceToCenter(new Vector2(0, 30f), true);
	}
}

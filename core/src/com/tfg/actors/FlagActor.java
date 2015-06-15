package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.utils.Assets;

public class FlagActor extends GameActor{
	
	private Sprite sprite;

	public FlagActor(Body body, Rectangle rectangle, boolean flipY) {
		super(body, rectangle);

		sprite = new Sprite(Assets.getTextureRegion("flag"));
		sprite.setPosition(body.getPosition().x - rectangle.width/2, body.getPosition().y - rectangle.height/2);
		sprite.setSize(rectangle.width, rectangle.height);
		sprite.setFlip(false, flipY);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
	
}

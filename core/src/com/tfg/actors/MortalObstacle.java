package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class MortalObstacle extends GameActor{

	private Sprite sprite;
	
	public MortalObstacle(Body body, Rectangle rectangle, boolean flip) {
		super(body, rectangle);
		
		sprite = new Sprite(Assets.getTextureRegion("spikes"));
		sprite.setSize(rectangle.width, rectangle.height);
		sprite.flip(false, flip);
		sprite.setPosition(rectangle.x, rectangle.y);
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		sprite.setColor(this.tint);
		
		sprite.draw(batch);
	}
}
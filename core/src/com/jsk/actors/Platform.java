package com.jsk.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.jsk.utils.Assets;

public class Platform extends GameActor {

	private Platform(Body body, Rectangle rectangle, String texture) {
		super(body, rectangle);
		
		sprite = new Sprite(Assets.getTextureRegion(texture));
		sprite.setSize(this.rectangle.getWidth(), this.rectangle.getHeight());
		sprite.setPosition(this.rectangle.x, this.rectangle.y);
		sprite.setOrigin(0, 0);
	}
	
	public static Platform platformCreate(Body body, Rectangle rectangle, String texture) {
		Platform res = null;
		String texture_def = null;
		if(texture != null){
			texture_def = texture;
		}else{
			texture_def = "platform2";
		}
		res = new Platform(body, rectangle, texture_def);
		return res;
	}
}

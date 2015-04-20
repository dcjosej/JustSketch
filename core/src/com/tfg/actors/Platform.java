package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class Platform extends GameActor {

	private Sprite sprite;

	
	/*TODO SELECCIONAR SPRITE EN FUNCION DEL NIVEL*/
	private Platform(Body body, Rectangle rectangle, String texture) {
		super(body, rectangle);
//		sprite = new Sprite(Assets.getTexture(texture));
		sprite = new Sprite(Assets.getTextureRegion(texture));

		sprite.setSize(this.rectangle.getWidth(), this.rectangle.getHeight());
		sprite.setPosition(this.rectangle.x, this.rectangle.y);
		sprite.setOrigin(0, 0);
	}
	
	public static Platform PlatformCreate(Body body, Rectangle rectangle, String texture) {
		Platform res = null;
		String texture_def = null;
		if(texture != null){
			texture_def = texture;
		}else{
			texture_def = Constants.PLATFORM1_TEXTURE;
		}
		res = new Platform(body, rectangle, texture_def);
		return res;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
}

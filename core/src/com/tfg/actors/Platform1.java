package com.tfg.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class Platform1 extends GameActor {
	
	private Sprite sprite;
	
	public Platform1(Body body, Rectangle rectangle) {
		super(body, rectangle);
		sprite = new Sprite(Assets.getTexture(Constants.PLATFORM1_TEXTURE));
		
		/*TODO ESTA FORMA DE ESTABLECER EL ANCHO Y EL ALTO HAY ARREGLARLA*/
		sprite.setSize(this.rectangle.getWidth() * 3.75f, this.rectangle.getHeight() * 2.75f);
		sprite.setPosition(this.body.getPosition().x, this.body.getPosition().y);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
}

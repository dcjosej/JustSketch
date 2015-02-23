package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;
import com.tfg.utils.GameState;

public class Platform extends GameActor {

	private Sprite sprite;

	public Platform(Body body, Rectangle rectangle) {
		super(body, rectangle);
		sprite = new Sprite(Assets.getTexture(Constants.PLATFORM2_TEXTURE));

		/* TODO ESTA FORMA DE ESTABLECER EL ANCHO Y EL ALTO HAY ARREGLARLA */
		sprite.setSize(this.rectangle.getWidth(), this.rectangle.getHeight());

		sprite.setPosition(this.rectangle.x - this.rectangle.width / 2,
				this.rectangle.y - this.rectangle.height / 2);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
}

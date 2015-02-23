package com.tfg.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.tfg.box2d.UserDataType;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;
import com.tfg.utils.GameState;

public class Ball extends CircleGameActor {

	private UserDataType userDataType;
	private Sprite sprite;

	public Ball(Body body, Circle circle) {
		super(body, circle);
		this.userDataType = UserDataType.BALL;
		this.sprite = new Sprite(Assets.getTexture(Constants.BALL_TEXTURE));
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (circle.y < -15f) {
			GameManager.gameOver = true;
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		/* ARREGLAR ROTACION! */

		sprite.setPosition(body.getPosition().x - circle.radius,
				body.getPosition().y - circle.radius);
		sprite.setSize(circle.radius * 2, circle.radius * 2);
		sprite.setOrigin(circle.radius, circle.radius);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

		/* TODO ARREGLAR ESTO Y PONERLO TODO EN UNA SUPERCLASE */
		sprite.setColor(this.tint);

		sprite.draw(batch);
	}

}

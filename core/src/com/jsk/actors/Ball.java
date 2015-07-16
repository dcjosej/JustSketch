package com.jsk.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.jsk.utils.Assets;
import com.jsk.utils.GameManager;

public class Ball extends CircleGameActor {

	public Ball(Body body, Circle circle) {
		super(body, circle);
		this.sprite = new Sprite(Assets.getTextureRegion("ball"));
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
		sprite.setPosition(body.getPosition().x - circle.radius,
				body.getPosition().y - circle.radius);
		sprite.setSize(circle.radius * 2, circle.radius * 2);
		sprite.setOrigin(circle.radius, circle.radius);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
		
		
		super.draw(batch, parentAlpha);
	}
	
	
	/*--------- FOR DEBUG ---------------*/
	public Rectangle getBounds(){
		Rectangle res = new Rectangle(getX(), getY(), getWidth(), getHeight());
		return res;
	}
	

}

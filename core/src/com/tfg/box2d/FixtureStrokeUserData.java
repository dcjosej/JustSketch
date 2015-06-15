package com.tfg.box2d;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class FixtureStrokeUserData{
	private Rectangle rectangle;
	private float angle;
	private Vector2 newPosition;
	private Sprite spriteStroke;

	public FixtureStrokeUserData(Rectangle rectangle, float angle) {
		this.rectangle = rectangle;
		this.angle = angle;
		newPosition = new Vector2();
		
		
		spriteStroke = new Sprite(Assets.getTextureRegion("pencil"));
		spriteStroke.setSize(rectangle.width, rectangle.height);
		spriteStroke.setPosition(rectangle.x - rectangle.width/2, rectangle.y);
		spriteStroke.setOrigin(0, 0);
		spriteStroke.setRotation(angle * MathUtils.radiansToDegrees);
	}
	
	public Sprite getSprite(){
		return spriteStroke;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public float getAngle() {
		return angle;
	}

	public void updateFixture(Body body, float delta) {
		Transform transform = body.getTransform();
		Vector2 position = new Vector2(rectangle.x, rectangle.y - rectangle.height / 2);

		newPosition = transform.mul(position.cpy());
				
		spriteStroke.setPosition(newPosition.x, newPosition.y);
		
		angle += body.getAngularVelocity() * delta;
		
//		spriteStroke.setRotation(angle * MathUtils.radiansToDegrees);
//		angle = body.getAngle();
	}
	
	public Vector2 getNewPosition(){
		return newPosition;
	}
}

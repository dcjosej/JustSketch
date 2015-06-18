package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tfg.box2d.FixtureStrokeUserData;
import com.tfg.box2d.StrokeUserData;

public class Stroke extends Actor {
	
	private Body body;
	
	public Stroke(Body body) {
		this.body = body;
		StrokeUserData sud = (StrokeUserData)this.body.getUserData();
		sud.setActorContainer(this);
		this.body.setUserData(sud);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		for (Fixture f : body.getFixtureList()) {
			FixtureStrokeUserData userData = (FixtureStrokeUserData) f
					.getUserData();
			userData.updateFixture(body, delta);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		for (Fixture f : body.getFixtureList()) {
			FixtureStrokeUserData userData = (FixtureStrokeUserData) f
					.getUserData();
			userData.getSprite().setRotation(userData.getAngle() * MathUtils.radiansToDegrees);
			userData.getSprite().draw(batch);
		}
	}
	
	public Body getBody(){
		return body;
	}
}
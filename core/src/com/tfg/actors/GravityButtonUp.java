package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tfg.utils.Assets;

public class GravityButtonUp extends CircleGameActor {

	private boolean active;

	private TextureRegion textureEnabled;
	private TextureRegion textureDisabled;
	private TextureRegion currentTexture;

	// -----------------------------
	private Ball ball;

	// Aunque sea un circulo, por comodidad seguiremos utilizando un Rectangulo
	public GravityButtonUp(Body body, Circle circle, Ball ball) {
		super(body, circle);

		active = false;
		this.textureEnabled = Assets.getTextureRegion("buttonUp-enabled");
		this.textureDisabled = Assets.getTextureRegion("buttonUp-disabled");

		this.currentTexture = textureDisabled;

		sprite = new Sprite(currentTexture);
		sprite.setSize(circle.radius * 2, circle.radius * 2);

		sprite.setPosition(circle.x, circle.y);
		this.ball = ball;

		setTouchable(Touchable.enabled);
		setWidth(sprite.getWidth());
		setHeight(sprite.getHeight());
		setBounds(circle.x, circle.y, getWidth(), getHeight());

		addListener(new ButtonListener());
	}

	@Override
	public void act(float delta) {
		currentTexture = active ? textureEnabled : textureDisabled;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		
		sprite.setRegion(currentTexture);
		super.draw(batch, parentAlpha);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getActive() {
		return active;
	}

	private class ButtonListener extends InputListener {
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			System.out.println("Activando gravedad hacia arriba!");
			ball.getBody().setGravityScale(-1);
			return true;
		}
	}

}
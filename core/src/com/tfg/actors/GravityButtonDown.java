package com.tfg.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tfg.utils.Assets;
import com.tfg.utils.Constants;

public class GravityButtonDown extends CircleGameActor{

	private Sprite sprite;
	
	//-----------------------------
	private Ball ball;
	
	
	//Aunque sea un circulo, por comodidad seguiremos utilizando un Rectangulo
	public GravityButtonDown(Body body, Circle circle, Ball ball) {
		super(body, circle);
		
		
		sprite = new Sprite(Assets.getTextureRegion("buttonDown"));
		sprite.setSize(circle.radius * 2, circle.radius * 2);
		
		sprite.setPosition(circle.x, circle.y);
		this.ball = ball;
		
		
		setTouchable(Touchable.enabled);
		setWidth(sprite.getWidth());
		setHeight(sprite.getHeight());
		setBounds(circle.x, circle.y, getWidth(), getHeight());
		
		addListener(new ButtonListener());
	}
	
	private class ButtonListener extends InputListener{
		@Override
		public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
        	System.out.println("Gravedad hacia abajo!");
			ball.getBody().setGravityScale(1);
        	return true;
        }
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		sprite.setColor(this.tint);
		sprite.draw(batch);
	}
}
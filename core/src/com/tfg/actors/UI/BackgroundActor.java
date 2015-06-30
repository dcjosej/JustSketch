package com.tfg.actors.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.tfg.utils.GameManager;
import com.tfg.utils.GameState;

public class BackgroundActor extends Image{

	private Color tint;
	
	public BackgroundActor(Texture texture) {
		super(texture);
	}
	

	@Override
	public void act(float delta) {
		super.act(delta);
		
		if(GameManager.gameState == GameState.PLAYING_LEVEL){
			tint = Color.WHITE;
		}else if(GameManager.gameState == GameState.WIN_LEVEL){
			tint = Color.GRAY;
		}
		
		this.setColor(tint);
	}
}

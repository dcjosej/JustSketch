package com.jsk.box2d;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class StrokeUserData extends UserData{
	
	private Actor actorContainer;
	
	public StrokeUserData(){
		this.userDataType = UserDataType.STROKE;
		actorContainer = null;
	}
	
	public void setActorContainer(Actor actorContainer){
		this.actorContainer = actorContainer;
	}
	
	public Actor getActorContainer(){
		return actorContainer;
	}
}
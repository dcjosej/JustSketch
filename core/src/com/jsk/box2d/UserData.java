package com.jsk.box2d;

public abstract class UserData {
	protected UserDataType userDataType;
	
	public UserData(){}
	
	
	public UserDataType getUserDataType(){
		return userDataType;
	}
}

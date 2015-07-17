package com.jsk.utils;

import com.badlogic.gdx.InputAdapter;


//Input processor to avoid multitouch
public class CatchMultitouch extends InputAdapter{

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(pointer > 0 ){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(pointer > 0 ){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer > 0 ){
			return true;
		}else{
			return false;
		}
	}
}

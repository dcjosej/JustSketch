package com.jsk.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.jsk.Tfg;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.addIcon("ic_launcher_lg.png", FileType.Internal);
		config.addIcon("ic_launcher_md.png", FileType.Internal);
		config.addIcon("ic_launcher_sm.png", FileType.Internal);
		
		config.title = "Loveline Pen Ball";



		new LwjglApplication(new Tfg(), config);
		//Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width,
				//Gdx.graphics.getDisplayMode().height);
	}
}
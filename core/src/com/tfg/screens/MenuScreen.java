package com.tfg.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.tfg.utils.Constants;

public class MenuScreen extends AbstractScreen{

	public MenuScreen(Game game) {
		super(game);
	}

	private Stage stage;
	private Skin skinMenu;

	//---------- Menu --------------------------
	private Button btnMenuPlay;
	private Button btnMenuOptions;

	private void rebuildStage() {
		skinMenu = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		Table layerControls = buildLayerControls();

		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		stack.add(layerControls);
	}

	private Table buildLayerControls() {
		Table layer = new Table();
		layer.center().center();
		
		// + Play Button
		btnMenuPlay = new Button(skinMenu, "play");
		layer.add(btnMenuPlay).padBottom(80f).maxWidth(300).maxHeight(100);
		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		layer.row();
		
		// + Options Button
		btnMenuOptions = new Button(skinMenu, "options");
		layer.add(btnMenuOptions).maxWidth(300).maxHeight(100);
		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});
		return layer;
	}
	
	private void onPlayClicked() {
		setScreen(new GameScreen(game));
	}
	
	private void onOptionsClicked() {
		System.out.println("Options clicked!");
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
//		stage.setDebugAll(true);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		stage = new Stage(new FillViewport(Constants.APP_WIDTH,
				Constants.APP_HEIGHT));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}

	@Override
	public void hide() {
		stage.dispose();
		skinMenu.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}

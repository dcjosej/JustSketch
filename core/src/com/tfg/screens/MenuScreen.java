package com.tfg.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.tfg.utils.Constants;

public class MenuScreen extends AbstractScreen {

	public MenuScreen(Game game) {
		super(game);
	}

	private Stage stage;
	private Skin skinMenu;

	// ---------- Menu --------------------------
	private Button btnMenuPlay;
	private Button btnMenuControls;
	private Button btnMenuExit;
	private Button btnMenuOptions;

	// ------------- Debug ----------------------
	private boolean debugEnabled = true;
	private float debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
	private boolean drawDebug = true;

	private void rebuildStage() {

		System.out.println("Pintando de nuevo!");

		skinMenu = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		Table layerControls = buildLayerControls();
		Table layerBackground = buildLayerBackground();
		Table layerMountains = buildLayerMountains();
		Table layerObjects = buildLayerObjects();

		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerMountains);
		stack.add(layerObjects);
		stack.add(layerControls);

		// stack.add(layerPlay);
		// stack.add(layerOptions);
	}

	private Table buildLayerBackground() {
		Table layer = new Table();

		Image imgBackground = new Image(skinMenu, "background");
		layer.add(imgBackground);
		return layer;
	}

	private Table buildLayerMountains() {

		System.out.println("Constuyendo capa de objetos");

		Table layer = new Table();
		layer.setDebug(debugEnabled);

		layer.row().fill().expandX();

		// Mountain left
		Image imgMountainLeft = new Image(skinMenu, "mountainLeft");
		layer.add(imgMountainLeft).fill(false).left();
		imgMountainLeft.setPosition(0, 0);

		// Mountain right
		Image imgMountainRight = new Image(skinMenu, "mountainRight");
		layer.add(imgMountainRight).fill(false).right();
		imgMountainLeft.setPosition(Constants.APP_WIDTH, 0);

		return layer;
	}

	private Table buildLayerObjects() {
		Table layer = new Table();

		layer.row().fill().expandY();

		// Title
		Image imgTitle = new Image(skinMenu, "title");
		layer.add(imgTitle).fill(false).top().padTop(40f);

		layer.row();

		// Stroke mouse
		Image imgStrokeMouse = new Image(skinMenu, "strokeMouse");
		layer.add(imgStrokeMouse).bottom().padLeft(50f).padBottom(150f);

		return layer;

	}

	private Table buildLayerControls() {
		Table layer = new Table();
		
		Table layerPlayAndExit = new Table();
		
		layer.setDebug(true);
		
//		layer.bottom().left();
		
		layerPlayAndExit.row();

		// + Play Button
		btnMenuPlay = new Button(skinMenu, "play");

		layerPlayAndExit.add(btnMenuPlay).center().maxWidth(300).maxHeight(100);

		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});
		
		layerPlayAndExit.row();

		// + Exit Button
		btnMenuExit = new Button(skinMenu, "exit");
		layer.add(btnMenuExit).maxWidth(300).maxHeight(100);

		btnMenuExit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});

		layer.row();

		// + Controls Button
		btnMenuControls = new Button(skinMenu, "controls");
		layer.add(btnMenuControls).left().maxWidth(300).maxHeight(100);
		
		
		btnMenuControls.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});

		// + Options Button
		btnMenuOptions = new Button(skinMenu, "options");
		layer.add(btnMenuOptions).right().maxWidth(300).maxHeight(100);

		btnMenuOptions.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});

		return layer;

	}

	// private Table buildLayerOptions(){
	// // Table layer = new Table();
	// //
	// // // + Options Button
	// // btnMenuOptions = new Button(skinMenu, "options");
	// // layer.add(btnMenuOptions).maxWidth(300).maxHeight(100);
	// // btnMenuOptions.addListener(new ChangeListener() {
	// // @Override
	// // public void changed(ChangeEvent event, Actor actor) {
	// // onOptionsClicked();
	// // }
	// // });
	// // return layer;
	// }

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

		if (debugEnabled) {
			debugRebuildStage -= delta;
			// System.out.println(debugRebuildStage);
			if (debugRebuildStage <= 0) {
				debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}

		stage.act(delta);
		stage.draw();
		stage.setDebugAll(false);
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

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

}

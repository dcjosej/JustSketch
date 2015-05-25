package com.tfg.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tfg.utils.Constants;

public class MenuScreen extends AbstractScreen {

	public MenuScreen(Game game) {
		super(game);
	}

	private Stage stage;
	private Skin skinMenu;

	// ---------- Menu --------------------------
	private Stack stack;
	private Stack stackObjects;
	private Stack stackControls;

	private Button btnMenuPlay;
	private Button btnMenuControls;
	private Button btnMenuExit;
	private Button btnMenuOptions;
	private Button btnControlsBack;
	
	// ------------- Debug ----------------------
	private boolean debugEnabled = false;
	private float debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
	private boolean drawDebug = false;

	private void rebuildStage() {

		System.out.println("Pintando de nuevo!");

		skinMenu = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		Table layerPlayAndExit = buildLayerPlayAndExit();
		Table layerOptionsAndControls = buildLayerOptionsAndControls();
		Table layerBackground = buildLayerBackground();
		Table layerMountains = buildLayerMountains();
		Table layerObjects = buildLayerObjects();

		Table layerControls = buildLayerControls();

		stage.clear();

		stack = new Stack();
		stackObjects = new Stack();
		stackControls = new Stack();
		stackControls.setVisible(false);
		stage.addActor(stack);

		// ---------------------------- Load stack objects
		// ----------------------------
		stackObjects.add(layerMountains);
		stackObjects.add(layerObjects);
		stackObjects.add(layerPlayAndExit);
		stackObjects.add(layerOptionsAndControls);

		// ---------------------------- Load stack controls
		// ----------------------------
		stackControls.add(layerControls);

		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		stack.add(layerBackground);
		stack.add(stackObjects);
		stack.add(stackControls);
	}

	private Table buildLayerControls() {
		Table layer = new Table();

		Image imgControls = new Image(skinMenu, "controlsScreen");
		layer.add(imgControls).padBottom(200f);

		// + Back Button
		btnControlsBack = new Button(skinMenu, "back");
		
		layer.row();
		
		layer.add(btnControlsBack).bottom().center().maxWidth(300).maxHeight(100).padBottom(80f);

		btnControlsBack.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackClicked();
			}
		});

		return layer;
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
		imgMountainLeft.addAction(sequence(alpha(0f), fadeIn(1.5f)));

		// Mountain right
		Image imgMountainRight = new Image(skinMenu, "mountainRight");
		layer.add(imgMountainRight).fill(false).right();

		imgMountainRight.addAction(sequence(alpha(0f), fadeIn(1.5f)));

		return layer;
	}

	private Table buildLayerObjects() {
		Table layer = new Table();

		layer.row().fill().expandY();

		// Title
		Image imgTitle = new Image(skinMenu, "title");
		layer.add(imgTitle).fill(false).top().padTop(40f);

		imgTitle.addAction(sequence(alpha(0f), moveBy(0, 100f), alpha(1f),
				moveBy(0, -100f, 2f, Interpolation.elasticOut)));

		layer.row();

		// Stroke mouse
		Image imgStrokeMouse = new Image(skinMenu, "strokeMouse");
		layer.add(imgStrokeMouse).bottom().padLeft(50f).padBottom(150f);

		return layer;

	}

	// Buttons Play and Exit
	private Table buildLayerPlayAndExit() {
		Table layer = new Table();
		layer.setDebug(drawDebug);

		layer.center().center();
		layer.row();

		// + Play Button
		btnMenuPlay = new Button(skinMenu, "play");

		layer.add(btnMenuPlay).center().maxWidth(300).maxHeight(100);

		btnMenuPlay.addAction(sequence(alpha(0f), moveBy(-800f, 0), alpha(1f),
				moveBy(800f, 0, 0.5f, Interpolation.linear)));

		btnMenuPlay.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPlayClicked();
			}
		});

		layer.row();

		// + Exit Button
		btnMenuExit = new Button(skinMenu, "exit");
		layer.add(btnMenuExit).maxWidth(300).maxHeight(100);

		btnMenuExit.addAction(sequence(alpha(0f), moveBy(800f, 0), alpha(1f),
				moveBy(-800f, 0, 0.5f, Interpolation.linear)));

		btnMenuExit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onOptionsClicked();
			}
		});

		return layer;

	}

	private Table buildLayerOptionsAndControls() {

		Table layer = new Table();
		layer.setDebug(false);

		layer.bottom().left().padLeft(100f);

		layer.addAction(sequence(alpha(0f), moveBy(0, -100f), alpha(1f),
				moveBy(0, 100f, 1.5f, Interpolation.elasticOut)));

		// + Controls Button
		btnMenuControls = new Button(skinMenu, "controls");
		layer.add(btnMenuControls).top().left().maxWidth(300).maxHeight(100);

		btnMenuControls.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onControlsClicked();
			}
		});

		// + Options Button
		btnMenuOptions = new Button(skinMenu, "options");
		layer.add(btnMenuOptions).top().right().padLeft(800f).maxWidth(300)
				.maxHeight(100);

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
	
	private void onBackClicked(){
		stackObjects.addAction(sequence(visible(true), alpha(0), fadeIn(1f)));

		stackControls
				.addAction(sequence(fadeOut(0.6f), visible(false)));
	}

	private void onControlsClicked() {
		stackObjects.addAction(sequence(fadeOut(0.6f), visible(false)));

		stackControls
				.addAction(sequence(alpha(0), visible(true), fadeIn(1f)));
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
		stage = new Stage(new FitViewport(Constants.APP_WIDTH,
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

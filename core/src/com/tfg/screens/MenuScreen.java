package com.tfg.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.visible;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
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
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.tfg.transitions.ScreenTransition;
import com.tfg.transitions.ScreenTransitionFade;
import com.tfg.utils.Constants;

public class MenuScreen extends AbstractScreen {

	public MenuScreen(DirectedGame game) {
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
	private Button btnMenuCredits;
	private Button btnControlsBack;

	// ------------- Debug ----------------------
	private boolean debugEnabled = false;
	private float debugRebuildStage = Constants.DEBUG_REBUILD_INTERVAL;
	private boolean drawDebug = false;

	private void rebuildStage() {

		System.out.println("Pintando de nuevo!");

		skinMenu = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.GUI_ATLAS));

		Table layerPlayAndExit = buildLayerPlayAndExit();
		Table layerCreditsAndControls = buildLayerCreditsAndControls();
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
		stackObjects.add(layerCreditsAndControls);

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

		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Image keysControlsImage = new Image(skinMenu, "keysControlsImage");
			layer.add(keysControlsImage).padBottom(80f);

		} else if (Gdx.app.getType() == ApplicationType.Android) {
			Image pauseAndroid = new Image(skinMenu, "pauseAndroid");
			layer.add(pauseAndroid).padBottom(80f);
		}

		layer.row();

		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Image leftClickImage = new Image(skinMenu, "leftClickImage");
			layer.add(leftClickImage).padBottom(40f);

		} else if (Gdx.app.getType() == ApplicationType.Android) {
			Image touchToDraw = new Image(skinMenu, "touchToDraw");
			layer.add(touchToDraw).padBottom(40f);
		}

		layer.row();

		if (Gdx.app.getType() == ApplicationType.Desktop) {
			Image rightClickImage = new Image(skinMenu, "rightClickImage");
			layer.add(rightClickImage);
			layer.row();
		}

		

		Image exampleImage = new Image(skinMenu, "exampleImage");
		layer.add(exampleImage).padBottom(50f);

		layer.row();

		// + Back Button
		btnControlsBack = new Button(skinMenu, "back");

		layer.row();

		layer.add(btnControlsBack).bottom().center().maxWidth(200)
				.maxHeight(80).padBottom(30f);

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
				onExitClicked();
			}
		});

		return layer;

	}

	private Table buildLayerCreditsAndControls() {

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
		btnMenuCredits = new Button(skinMenu, "credits");
		layer.add(btnMenuCredits).top().right().padLeft(800f).maxWidth(300)
				.maxHeight(100);

		btnMenuCredits.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCreditsClicked();
			}
		});

		return layer;
	}

	private void onPlayClicked() {
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		game.setScreen(new SelectLevelScreen(game), transition);
	}

	private void onCreditsClicked() {
		ScreenTransition transition = ScreenTransitionFade.init(0.4f);
		game.setScreen(new CreditScreen(game), transition);
	}

	private void onBackClicked() {
		btnMenuControls.setDisabled(true);
		stackObjects.addAction(sequence(visible(true), alpha(0), fadeIn(0.4f),
				run(new Runnable() {
					public void run() {
						btnMenuControls.setDisabled(false);
					}
				})));

		stackControls.addAction(sequence(fadeOut(0.4f), visible(false)));
	}

	private void onControlsClicked() {

		stackObjects.addAction(sequence(fadeOut(0.4f), visible(false)));

		btnControlsBack.setDisabled(true);
		stackControls.addAction(sequence(alpha(0), visible(true), fadeIn(0.4f),
				run(new Runnable() {
					public void run() {
						btnControlsBack.setDisabled(false);
					}
				})));
	}

	private void onExitClicked() {
		Gdx.app.exit();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (debugEnabled) {
			debugRebuildStage -= delta;
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
		// Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}

	@Override
	public void hide() {
		stage.dispose();
		skinMenu.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}
}

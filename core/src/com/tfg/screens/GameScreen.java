package com.tfg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.tfg.actors.Ball;
import com.tfg.actors.BouncePlatform;
import com.tfg.actors.FlagActor;
import com.tfg.actors.GravityButtonDown;
import com.tfg.actors.GravityButtonUp;
import com.tfg.actors.MortalObstacle;
import com.tfg.actors.Platform;
import com.tfg.actors.Stroke;
import com.tfg.box2d.StrokeUserData;
import com.tfg.transitions.ScreenTransition;
import com.tfg.transitions.ScreenTransitionSlide;
import com.tfg.utils.Assets;
import com.tfg.utils.BodyUtils;
import com.tfg.utils.CameraHelper;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;
import com.tfg.utils.GamePreferences;
import com.tfg.utils.GameState;
import com.tfg.utils.WorldTiledUtils;
import com.tfg.utils.WorldUtils;

public class GameScreen extends AbstractScreen implements ContactListener {

	protected Stage stage;
	protected Stage UI;
	protected InputMultiplexer inputMultiplexer;

	// Effects
	private ParticleEffectPool ballExplosionPool;
	private Array<PooledEffect> effects = new Array<PooledEffect>();

	protected World world;
	private final float TIME_STEP = 1 / 300f;
	private float accumulator = 0f;

	private Box2DDebugRenderer box2Drenderer;
	private ShapeRenderer shapeRenderer;

	private TiledMap map;
	private OrthogonalTiledMapRenderer tiledMapRenderer;
	private int viewport_width;
	private int viewport_height;

	// Actors
	private Array<Stroke> strokes;
	private Ball ball;
	private GravityButtonUp gravityButtonUp;
	private GravityButtonDown gravityButtonDown;

	private boolean createStroke;

	private OrthographicCamera camera;
	private CameraHelper cameraHelper;

	// Stroke points
	private float acumDistance = 0.0f;
	private Array<Vector2> input;
	private Array<Vector2> inputToErase; // Input for delete strokes
	private boolean invalidPoints = false; // Checks if there is a invalid point
											// on a stroke

	// ----------- Textures for change mode (Write or Erase)
	// ---------------------------
	Image changeModeButton;
	private SpriteDrawable texturePencil;
	private SpriteDrawable textureEraser;

	// Physics body to delete
	private Array<Body> deleteBodies;

	private boolean drawDebug = false;

	private boolean rightButtonClicked = false;
	private boolean erase = false; // Var to check if we are on erasing mode

	private boolean resetLevel = false;

	// ----------- Menu ----------------------------
	private Skin skin;

	private Button btnNext;
	private Button btnBack;
	private Button btnExit;
	private Label numStrokesLabelPausePanel;
	private Label bestScoreLabel;

	// ------------ HUD -----------------------------
	private Stage HUD;

	private ProgressBar strokeBar;
	private Label numStrokesLabel;
	private int strokeCounter = 0;
	private float percentageStroke = 100;

	public GameScreen(DirectedGame game) {
		super(game);
	}

	private void rebuildStage() {

		stage.clear();
		resetLevel = false;
		GameManager.isPaused = false;
		strokeCounter = 0;

		// camera = (OrthographicCamera) stage.getCamera();

		GameManager.gameState = GameState.PLAYING_LEVEL;

		setUpParticleEffects();

		deleteBodies = new Array<Body>();

		world = WorldUtils.createWorld();
		world.setContactListener(this);
		// ground = WorldUtils.createGround(world);
		box2Drenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();

		input = new Array<Vector2>();
		inputToErase = new Array<Vector2>();
		strokes = new Array<Stroke>();

		cameraHelper = new CameraHelper(stage.getCamera());

		inputMultiplexer = new InputMultiplexer(UI, HUD, stage, this);
		Gdx.input.setCatchBackKey(true);
		// Gdx.input.setInputProcessor(inputMultiplexer);

		createStroke = false;

		// -------- Initializing textures for change mode
		// -------------------------
		textureEraser = new SpriteDrawable(new Sprite(Assets
				.getGUITextureAtlas().findRegion("eraser")));
		texturePencil = new SpriteDrawable(new Sprite(Assets
				.getGUITextureAtlas().findRegion("pencil")));

		skin = Assets.getSkinGui();
		initHUD();
		setUpGui();
		setupMapStaff();
	}

	private void initHUD() {
		HUD.clear();

		// -------- ProgressBar remaining paint -----------------------
		percentageStroke = 100;
		strokeBar = new ProgressBar(0, 100, 0.1f, false, skin, "strokeBar");

		strokeBar.setValue(percentageStroke);
		strokeBar.setSize(400f, strokeBar.getPrefHeight());
		strokeBar.setAnimateInterpolation(Interpolation.linear);
		strokeBar.setAnimateDuration(0.1f);

		HUD.addActor(strokeBar);
		strokeBar.setPosition(20, 1020);

		// ------- Number of strokes -------------------
		// LabelStyle labelStyle = new LabelStyle(Utils.getFont(44, "DJGROSS"),
		// Color.BLACK);
		LabelStyle labelStyle = new LabelStyle(
				Assets.getBitmapFont(Constants.GUI_FONT_60), Color.BLACK);
		numStrokesLabel = new Label("Strokes: " + strokeCounter, labelStyle);
		numStrokesLabel.setPosition(1570, 1010);

		HUD.addActor(numStrokesLabel);

		// ------- Change mode --------------------------
		changeModeButton = new Image(skin, "pencil");
		changeModeButton.setPosition(450, 1000);
		changeModeButton.setScale(0.9f);
		changeModeButton.addCaptureListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				changeModeClicked();
			}
		});

		HUD.addActor(changeModeButton);
	}

	private void changeModeClicked() {
		erase = !erase;
	}

	private void setUpGui() {

		UI.clear();

		Image pausePanel = new Image(skin, "pausePanel");
		pausePanel.setPosition(Constants.APP_WIDTH / 2 - pausePanel.getWidth()
				/ 2, Constants.APP_HEIGHT / 2 - pausePanel.getHeight() / 2);
		UI.addActor(pausePanel);

		// -------------------- + Retry button
		// ----------------------------------------------

		ImageButton retryButton = new ImageButton(skin, "retry");
		UI.addActor(retryButton);
		retryButton.setPosition(pausePanel.getX() + pausePanel.getWidth()
				- retryButton.getWidth() - 80,
				pausePanel.getY() - retryButton.getHeight() + 100);
		retryButton.addCaptureListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				resetLevel = true;
				super.clicked(event, x, y);
			}
		});

		// ---------------------------------------------------------------------------------

		Stack stack = new Stack();
		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		UI.addActor(stack);

		Table layerLevelMenu = buildLayerLevelMenu();

		stack.add(layerLevelMenu);
	}

	private void setUpParticleEffects() {
		ParticleEffect ballExplosion = new ParticleEffect();
		ballExplosion.load(Gdx.files.internal("effects/ballExplosion.effect"),
				Gdx.files.internal("effects"));
		ballExplosion.scaleEffect(0.05f);
		ballExplosionPool = new ParticleEffectPool(ballExplosion, 1, 2);
	}

	@Override
	public void render(float delta) {

		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Assets.updateAssets()) {
			if (!GameManager.isPaused) {

				stage.act(delta);

				strokeBar.setValue(percentageStroke);
				HUD.act(delta);
				changeModeButton.setDrawable(erase ? textureEraser
						: texturePencil);

				// Fixed timestep
				accumulator += delta;
				while (accumulator >= delta) {
					world.step(TIME_STEP, 6, 2);
					accumulator -= TIME_STEP;
				}

				if (createStroke) {
					createStroke();
				}

				if (ball.getY() < -ball.getHeight()
						|| ball.getY() > viewport_height) {
					resetLevel = true;
				}

				deleteStrokes();
				checkTranslate();
			}

			tiledMapRenderer.setView((OrthographicCamera) stage.getCamera());
			tiledMapRenderer.render();
			if (GameManager.isPaused) {
				tiledMapRenderer.getBatch().setColor(Constants.TINT_COLOR);
			} else {
				tiledMapRenderer.getBatch().setColor(Color.WHITE);
			}

			cameraHelper.update(delta); // To checking every frame if it's
										// neccesary make a camera
										// traslation
			stage.getBatch().begin();
			drawEffects(delta);
			stage.getBatch().end();

			stage.draw();
			HUD.draw();

			if (drawDebug) {
				drawDebug();
			}

			drawUserInput();

			if (gravityButtonDown != null && gravityButtonUp != null) {

				if (ball.getBody().getGravityScale() == 1.0f) {

					gravityButtonDown.setActive(true);
					gravityButtonUp.setActive(false);
				} else {
					gravityButtonDown.setActive(false);
					gravityButtonUp.setActive(true);
				}
			}

			if (GameManager.isPaused) {
				if (GameManager.gameState == GameState.WIN_LEVEL) {

					btnNext.setVisible(true);
					btnBack.setVisible(false);
				} else {
					btnNext.setVisible(false);
					btnBack.setVisible(true);
				}
				UI.act(delta);
				UI.draw();
				UI.setDebugAll(drawDebug);
			}
		}

		if (resetLevel) {
			rebuildStage();
		}

	}

	private void updatePreferences() {
		int lastBestScore = GamePreferences.instance
				.getBestScore(GameManager.currentLevel);
		if (strokeCounter < lastBestScore || lastBestScore == 0) {
			GamePreferences.instance.save(GameManager.currentLevel,
					strokeCounter);
		}

		GamePreferences.instance.numLevelsActive++;
	}

	private void checkTranslate() {

		int mapWidth = (int) map.getProperties().get("width");

		if (mapWidth > 60) {
			if (ball.getX() > 45) {
				cameraHelper.translateRight = true;
				cameraHelper.translateLeft = false;
			} else {
				cameraHelper.translateRight = false;
				cameraHelper.translateLeft = true;
			}
		}
	}

	private void drawEffects(float delta) {
		for (PooledEffect effect : effects) {
			effect.draw(stage.getBatch(), delta);
			if (effect.isComplete()) {
				effect.free();
				effects.removeValue(effect, true);
				resetLevel = true;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		UI.getViewport().update(width, height);
		this.camera = (OrthographicCamera) stage.getViewport().getCamera();
	}

	@Override
	public void show() {

		loadLevel();

		stage = new Stage(new FillViewport(viewport_width, viewport_height));
		UI = new Stage(new FillViewport(Constants.APP_WIDTH,
				Constants.APP_HEIGHT));

		HUD = new Stage(new FillViewport(Constants.APP_WIDTH,
				Constants.APP_HEIGHT));

		rebuildStage();
	}

	@Override
	public void hide() {
	}

	@Override
	public void resume() {
	}

	private void loadLevel() {

		Music backgroundMusic = Assets.getMusic(Constants.BACKGROUND_MUSIC);
		backgroundMusic.setVolume(0.4f);
		backgroundMusic.setLooping(true);
		backgroundMusic.play();

		map = new TmxMapLoader().load("maps/Level" + GameManager.currentLevel
				+ ".tmx");

		this.viewport_width = 60;
		this.viewport_height = 34;
		tiledMapRenderer = new OrthogonalTiledMapRenderer(map,
				Constants.UNIT_SCALE);
	}

	// ----------------------------------------
	private void deleteStrokes() {
		for (Body b : deleteBodies) {
			if (b.getUserData() instanceof StrokeUserData) {
				StrokeUserData sud = (StrokeUserData) b.getUserData();
				// world.destroyBody(b);
				sud.getActorContainer().remove();
			}
			b.setActive(false);
		}
		deleteBodies.clear();
	}

	private void setupMapStaff() {

		// La bola se encuentra en una sola capa
		MapObject ballObject = map.getLayers().get("Ball").getObjects().get(0);
		setUpBall(ballObject);

		for (MapObject m : map.getLayers().get("Colliders").getObjects()) {

			String objectType = (String) m.getProperties().get("type");

			switch (objectType) {
			case "PLATFORM":
				setUpPlatforms(m);
				break;
			case "BOUNCE_PLATFORM":
				setUpBouncePlatforms(m);
				break;
			case "FLAG":
				setUpFlags(m);
				break;
			case "SPIKES":
				setUpSpikes(m);
				break;
			case "BUTTON_UP":
				setUpGravityButtonUp(m);
				break;
			case "BUTTON_DOWN":
				setUpGravityButtonDown(m);
				break;
			}
		}

		for (MapObject m : map.getLayers().get("SpecialColliders").getObjects()) {

			String objectType = (String) m.getProperties().get("type");

			switch (objectType) {
			case "CHAIN_COLLIDER":
				setUpChainColliders(m);
				break;
			}
		}
	}

	private void setUpGravityButtonDown(MapObject m) {
		EllipseMapObject tiledCircleObject = (EllipseMapObject) m;

		Vector2 position = new Vector2(tiledCircleObject.getEllipse().x
				* Constants.UNIT_SCALE, tiledCircleObject.getEllipse().y
				* Constants.UNIT_SCALE);

		Circle tiledCircle = new Circle(new Vector2(
				tiledCircleObject.getEllipse().x,
				tiledCircleObject.getEllipse().y),
				tiledCircleObject.getEllipse().width / 2); // El ancho de la
															// elipse
															// corresponde al
															// diametro del
															// circulo

		Circle worldCircle = new Circle(tiledCircle.x * Constants.UNIT_SCALE,
				tiledCircle.y * Constants.UNIT_SCALE, tiledCircle.radius
						* Constants.UNIT_SCALE);

		Vector2 positionPhysicBody = new Vector2(position.x
				+ worldCircle.radius, position.y + worldCircle.radius);

		this.gravityButtonDown = new GravityButtonDown(
				WorldUtils.createButtonDown(world, positionPhysicBody,
						worldCircle), worldCircle, this.ball);

		stage.addActor(this.gravityButtonDown);
	}

	private void setUpGravityButtonUp(MapObject m) {
		EllipseMapObject tiledCircleObject = (EllipseMapObject) m;

		Vector2 position = new Vector2(tiledCircleObject.getEllipse().x
				* Constants.UNIT_SCALE, tiledCircleObject.getEllipse().y
				* Constants.UNIT_SCALE);

		Circle tiledCircle = new Circle(new Vector2(
				tiledCircleObject.getEllipse().x,
				tiledCircleObject.getEllipse().y),
				tiledCircleObject.getEllipse().width / 2); // El ancho de la
															// elipse
															// corresponde al
															// diametro del
															// circulo

		Circle worldCircle = new Circle(tiledCircle.x / 32f,
				tiledCircle.y / 32f, tiledCircle.radius / 32f);

		Vector2 positionPhysicBody = new Vector2(position.x
				+ worldCircle.radius, position.y + worldCircle.radius);

		this.gravityButtonUp = new GravityButtonUp(WorldUtils.createButtonUp(
				world, positionPhysicBody, worldCircle), worldCircle, this.ball);

		stage.addActor(this.gravityButtonUp);
	}

	private void setUpSpikes(MapObject m) {
		boolean flip = new Boolean((String) (m.getProperties().get("flipY")));
		Rectangle rectangle = WorldTiledUtils.getWorldRectangle(m);
		Vector2 position = new Vector2(rectangle.x, rectangle.y);

		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByRectangle(
				position, rectangle);

		MortalObstacle spikes = new MortalObstacle(
				WorldUtils.createMortalObstacle(world, positionPhysicBody,
						rectangle), rectangle, flip);
		stage.addActor(spikes);

	}

	private void setUpBouncePlatforms(MapObject m) {

		Rectangle rectangle = WorldTiledUtils.getWorldRectangle(m);
		Vector2 position = new Vector2(rectangle.x, rectangle.y);
		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByRectangle(
				position, rectangle);
		BouncePlatform bp = new BouncePlatform(
				WorldUtils.createBouncePlatformBody(world, positionPhysicBody,
						rectangle), rectangle);
		stage.addActor(bp);
	}

	private void setUpFlags(MapObject m) {
		boolean flipY = new Boolean((String) (m.getProperties().get("flipY")));

		Rectangle rectangle = WorldTiledUtils.getWorldRectangle(m);
		Vector2 position = new Vector2(rectangle.x, rectangle.y);
		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByRectangle(
				position, rectangle);

		FlagActor p = new FlagActor(WorldUtils.createFlag(world,
				positionPhysicBody, rectangle), rectangle, flipY);
		stage.addActor(p);

	}

	private void setUpChainColliders(MapObject m) {
		PolylineMapObject specialPlatformTiled = (PolylineMapObject) m;

		float[] tiledVertices = specialPlatformTiled.getPolyline()
				.getTransformedVertices();
		float[] worldVertices = new float[tiledVertices.length];

		for (int i = 0; i < tiledVertices.length; i++) {
			worldVertices[i] = tiledVertices[i] / 32f;
		}

		WorldUtils.createChainShape2(world, worldVertices);
	}

	private Table buildLayerLevelMenu() {
		Table layer = new Table();
		layer.setDebug(false);
		layer.center().center();
		layer.padTop(30f);

		// ------- Score Label ---------------------
		// LabelStyle labelStyle = new LabelStyle(Utils.getFont(44, "DJGROSS"),
		// Color.BLACK);
		LabelStyle labelStyle = new LabelStyle(
				Assets.getBitmapFont(Constants.GUI_FONT_60), Color.BLACK);
		numStrokesLabelPausePanel = new Label("Strokes: " + strokeCounter,
				labelStyle);
		layer.add(numStrokesLabelPausePanel).padBottom(30f);
		layer.row();

		// ------- Best score label -------------------
		// labelStyle = new LabelStyle(Utils.getFont(44, "DJGROSS"),
		// Color.BLACK);
		bestScoreLabel = new Label(
				"Best score: "
						+ GamePreferences.instance
								.getBestScore(GameManager.currentLevel),
				labelStyle);
		layer.add(bestScoreLabel).padBottom(50f);

		layer.row();

		// + Next Button
		btnNext = new Button(skin, "next");
		layer.add(btnNext).width(220f).height(80f).padBottom(50f);
		btnNext.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onNextClicked();
			}
		});
		layer.row();

		// + Back Button
		btnBack = new Button(skin, "back");
		layer.add(btnBack).width(150f).height(50f).padBottom(15f);
		btnBack.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackClicked();
			}
		});
		layer.row();

		// + Exit Button
		btnExit = new Button(skin, "exit");
		layer.add(btnExit).width(150f).height(50f);
		btnExit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onExitClicked();
			}
		});

		return layer;
	}

	public void onNextClicked() {

		GameManager.currentLevel++;

		AbstractScreen nextScreen = GameManager.currentLevel > Constants.NUM_LEVELS ? new CreditScreen(
				game) : new GameScreen(game);
		ScreenTransition transition = ScreenTransitionSlide.init(1f,
				ScreenTransitionSlide.UP, true, Interpolation.linear);
		game.setScreen(nextScreen, transition);

		// loadLevel();
		// rebuildStage();
	}

	public void onBackClicked() {
		GameManager.isPaused = false;
	}

	public void onExitClicked() {
		setScreen(new MenuScreen(game));
	}

	private void createStroke() {

		Stroke stroke = WorldUtils.processStroke(input, world);
		// strokes.add(stroke);
		if (stroke != null) {

			if (strokes.size >= Constants.MAX_STROKES) {
				removeStroke(strokes.removeIndex(0));
			}

			stage.addActor(stroke);
			strokes.add(stroke);
			updateStrokeCounter();
		}
		input.clear();
		createStroke = false;
	}

	private void removeStroke(Stroke stroke) {
		StrokeUserData sud = (StrokeUserData) stroke.getBody().getUserData();
		sud.getActorContainer().remove();
		world.destroyBody(stroke.getBody());
	}

	private void updateStrokeCounter() {
		strokeCounter++;

		numStrokesLabel.setText("Strokes: " + strokeCounter);
		numStrokesLabelPausePanel.setText("Strokes: " + strokeCounter);
	}

	private void drawUserInput() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		for (int i = 1; i < input.size; i++) {
			shapeRenderer.rectLine(input.get(i - 1), input.get(i),
					Constants.THICKNESS);
		}

		shapeRenderer.setColor(Color.BLUE);
		for (int i = 1; i < inputToErase.size; i++) {
			shapeRenderer.rectLine(inputToErase.get(i - 1),
					inputToErase.get(i), Constants.THICKNESS);
		}

		shapeRenderer.end();
	}

	private void drawDebug() {
		box2Drenderer.render(world, camera.combined);

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.rect(ball.getBounds().x, ball.getBounds().y,
				ball.getBounds().width, ball.getBounds().height);
		shapeRenderer.end();
	}

	private void setUpPlatforms(MapObject m) {
		String texture = (String) m.getProperties().get("texture");
		Rectangle worldRectangle = WorldTiledUtils.getWorldRectangle(m);
		Vector2 position = new Vector2(worldRectangle.x, worldRectangle.y);
		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByRectangle(
				position, worldRectangle);

		Platform p = Platform.platformCreate(WorldUtils.createPlatformBody(
				world, positionPhysicBody, worldRectangle), worldRectangle,
				texture);
		stage.addActor(p);
	}

	private void setUpBall(MapObject m) {
		Circle circle = WorldTiledUtils.getCircleWorld(m);
		Vector2 position = new Vector2(circle.x, circle.y);
		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByCircle(
				position, circle);
		this.ball = new Ball(WorldUtils.createBall(world, circle,
				positionPhysicBody), circle);
		stage.addActor(ball);
	}

	private void checkRemoveStrokes() {
		for (Stroke s : strokes) {
			for (Fixture f : s.getBody().getFixtureList()) {
				for (Vector2 point : inputToErase) {
					if (f.testPoint(point)) {
						// System.out.println("Punto que estoy comprobando: "
						// + point);
						world.destroyBody(s.getBody());
						s.remove();
						strokes.removeValue(s, true);
						break;
					}
				}
			}
		}
		inputToErase.clear();
	}

	@Override
	public boolean keyDown(int keycode) {

		// Only for debug purposes
		// if (Keys.Q == keycode) {
		// drawDebug = !drawDebug;
		// }

		if (Keys.R == keycode) {
			resetLevel = true;
		}

		if (Keys.P == keycode || Keys.ESCAPE == keycode || Keys.BACK == keycode) {
			updateStrokeCounterPausePanel();
			GameManager.isPaused = !GameManager.isPaused;
			stage.getBatch().setColor(Color.DARK_GRAY);
		}

		return super.keyDown(keycode);
	}

	private void updateStrokeCounterPausePanel() {
		numStrokesLabelPausePanel.setText("Strokes: " + strokeCounter);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		Vector3 point_aux = stage.getViewport().unproject(
				new Vector3(screenX, screenY, 0f));
		Vector2 point = new Vector2(point_aux.x, point_aux.y);

		if (Input.Buttons.RIGHT == button) {
			rightButtonClicked = true;
			erase = !erase;
		}

		// if (Input.Buttons.LEFT == button) {
		// rightButtonClicked = false;
		// }

		if (Input.Buttons.LEFT == button) {
			rightButtonClicked = false;
			Assets.getSound(Constants.DRAW_EFFECT).play();
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		Vector2 currentPoint = stage.getViewport().unproject(
				new Vector2(screenX, screenY));

		if (!rightButtonClicked) {
			if (!erase) {
				isValidPoint(currentPoint);
				if (input.size < Constants.MAX_POINTS && !this.invalidPoints) {
					input.add(currentPoint);
					percentageStroke = 100 - ((input.size * 1.0f / Constants.MAX_POINTS) * 100);
				}

				// if (acumDistance <= Constants.MAX_DISTANCE &&
				// !this.invalidPoints) {
				//
				// Vector2 lastPoint = input.size > 0 ? input.get(input.size -
				// 1) : currentPoint;
				// acumDistance += lastPoint.dst2(currentPoint);
				// System.out.println(acumDistance);
				// input.add(currentPoint);
				// percentageStroke = 100 - ((acumDistance * 1.0f /
				// Constants.MAX_DISTANCE) * 100);
				// }

			} else {
				if (inputToErase.size != 0) {
					Vector2 lastPoint = inputToErase.get(inputToErase.size - 1);
					float distanceToLastPoint = lastPoint.dst2(currentPoint);

					while (distanceToLastPoint > 0.03f) {
						Vector2 newPoint = lastPoint.interpolate(currentPoint,
								0.03f, Interpolation.linear);
						inputToErase.add(newPoint);
						lastPoint = newPoint.cpy();
						distanceToLastPoint = lastPoint.dst2(currentPoint);
					}
				}
				inputToErase.add(currentPoint);
			}
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Vector2 currentPoint = stage.getViewport().unproject(
				new Vector2(screenX, screenY));

		if (!erase && input.size != 0) {
			if (input.size < Constants.MAX_POINTS && !this.invalidPoints) {
				input.add(currentPoint);
			}

			createStroke = true;
		} else {
			inputToErase.add(currentPoint);
			checkRemoveStrokes();
		}

		percentageStroke = 100;
		rightButtonClicked = false;
		invalidPoints = false;
		acumDistance = 0;
		Assets.getSound(Constants.DRAW_EFFECT).stop();
		return super.touchUp(screenX, screenY, pointer, button);
	}

	private void isValidPoint(Vector2 point) {
		Array<Fixture> fixtures = new Array<Fixture>();
		world.getFixtures(fixtures);

		for (Fixture f : fixtures) {
			if (f.testPoint(point) && !f.isSensor() && f.getBody().isActive()
					&& !BodyUtils.isBall(f.getBody())) {
				this.invalidPoints = true;
				break;
			}
		}
	}

	// -------------- Contact listener ----------------------------
	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		try {
			if (BodyUtils.isBall(a) && BodyUtils.isMortalObstacle(b)
					|| BodyUtils.isBall(b) && BodyUtils.isMortalObstacle(a)) {

				Assets.getSound(Constants.EXPLOSION_EFFECT).play(1f);

				Body ballBody = BodyUtils.isBall(a) ? a : b;

				PooledEffect effect = ballExplosionPool.obtain();
				effect.setPosition(ballBody.getPosition().x,
						ballBody.getPosition().y);
				effects.add(effect);

				deleteBodies.add(ballBody);
				this.ball.remove();
			}

			if (BodyUtils.isBall(a) && BodyUtils.isFlag(b)
					|| BodyUtils.isBall(b) && BodyUtils.isFlag(a)) {
				tiledMapRenderer.getBatch().setColor(Color.GRAY);
				GameManager.gameState = GameState.WIN_LEVEL;
				GameManager.isPaused = true;
				// Update game preferences before pass the level.
				updatePreferences();
			}

			if (BodyUtils.isBouncePlatform(a) && BodyUtils.isBall(b)
					|| BodyUtils.isBouncePlatform(b) && BodyUtils.isBall(a)) {

				Assets.getSound(Constants.JUMP_EFFECT).play(0.2f);

				Body ball = BodyUtils.isBall(a) ? a : b;

				ball.setLinearVelocity(new Vector2(ball.getLinearVelocity().x,
						Constants.JUMP_BALL_VELOCITY_Y));
			}

			if (BodyUtils.isMortalObstacle(a) && BodyUtils.isStroke(b)
					|| BodyUtils.isMortalObstacle(b) && BodyUtils.isStroke(a)) {

				if (BodyUtils.isStroke(a)) {
					if (!deleteBodies.contains(a, true)) {
						deleteBodies.add(a);
					}

				} else {
					if (!deleteBodies.contains(b, true)) {
						deleteBodies.add(b);
					}
				}
			}

			if (BodyUtils.isStroke(a) && !BodyUtils.isMortalObstacle(b)
					|| BodyUtils.isStroke(b) && !BodyUtils.isMortalObstacle(a)) {

				Body strokeBody = BodyUtils.isStroke(a) ? a : b;
				Body otherBody = BodyUtils.isStroke(a) ? b : a;
				strokeBody.getPosition().set(strokeBody.getPosition().x,
						otherBody.getPosition().y);
			}

		} catch (Exception e) {
		}
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public InputProcessor getInputProcessor() {
		return inputMultiplexer;
	}
}

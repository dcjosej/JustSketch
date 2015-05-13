package com.tfg.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import com.tfg.utils.Assets;
import com.tfg.utils.BodyUtils;
import com.tfg.utils.CameraHelper;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;
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

	private Label textFinishLevel;

	private float[] strokePoints;
	private boolean createStroke;
	private boolean isDrawing = false;
	

	private OrthographicCamera camera;
	private CameraHelper cameraHelper;

	// Stroke points
	private Array<Vector2> input;

	// Physics body to delete
	private Array<Body> deleteBodies;

	private boolean drawDebug = false;

	private boolean rightButtonClicked = false;

	private boolean resetLevel = false;

	private FrameBuffer frameBuffer;

	// ----------- Menu ----------------------------
	private Skin skin;

	private Button btnNext;
	private Button btnBack;
	private Button btnExit;

	private boolean isPaused;

	public GameScreen(Game game) {
		super(game);
	}

	private void rebuildStage() {

		stage.clear();

		frameBuffer = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), false);

		resetLevel = false;
		GameManager.isPaused = false;

		camera = (OrthographicCamera) stage.getCamera();

		/* TODO ORGANIZAR ESTE METODO Y MODULARLO */
		GameManager.gameState = GameState.PLAYING_LEVEL;

		setUpParticleEffects();

		deleteBodies = new Array<Body>();

		world = WorldUtils.createWorld();
		world.setContactListener(this);
		// ground = WorldUtils.createGround(world);
		box2Drenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();

		input = new Array<Vector2>();
		strokes = new Array<Stroke>();

		cameraHelper = new CameraHelper(stage.getCamera());

		inputMultiplexer = new InputMultiplexer(UI, stage, this);
		Gdx.input.setInputProcessor(inputMultiplexer);

		createStroke = false;

		Assets.loadLevel1Asset();

		while (!Assets.updateAssets()) {
		} /* TODO ARREGLAR ESTO */

		setUpGui();
		setupMapStaff();
	}

	private void setUpParticleEffects() {
		ParticleEffect ballExplosion = new ParticleEffect();
		ballExplosion.load(Gdx.files.internal("Effects/ballExplosion.effect"),
				Gdx.files.internal("Effects"));
		ballExplosion.scaleEffect(0.05f);
		ballExplosionPool = new ParticleEffectPool(ballExplosion, 1, 2);
	}

	@Override
	public void render(float delta) {

		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Assets.updateAssets()) {
			if (!GameManager.isPaused) {

				
				System.out.println("Actualizando actores");
				
				stage.act(delta);
				
				// Fixed timestep
				accumulator += delta;
				while (accumulator >= delta) {
					world.step(TIME_STEP, 6, 2);
					accumulator -= TIME_STEP;
				}
				
				if (createStroke) {
					createStroke();
				}
				
				deleteStrokes();

				// TODO:Generalizar esto
				checkTranslate();
			}

			tiledMapRenderer.setView((OrthographicCamera) stage.getCamera());
			tiledMapRenderer.render();
			if (GameManager.isPaused) {
				tiledMapRenderer.getSpriteBatch().setColor(Constants.TINT_COLOR);
			} else{
				tiledMapRenderer.getSpriteBatch().setColor(Color.WHITE);
			}
			
			
			
			cameraHelper.update(delta); // To checking every frame if it's
										// neccesary make a camera
										// traslation
			stage.getBatch().begin();
			drawEffects(delta);
			stage.getBatch().end();

			stage.draw();

			if (drawDebug) {
				drawDebug();
			}

			drawUserInput();
			
			if(GameManager.isPaused){
				if(GameManager.gameState == GameState.WIN_LEVEL){
					btnNext.setVisible(true);
					btnBack.setVisible(false);
				}else{
					btnNext.setVisible(false);
					btnBack.setVisible(true);
				}
				UI.act(delta);
				UI.draw();
				UI.setDebugAll(drawDebug);
			}
		}
		
		if (resetLevel) {
			// GameManager.gameOver = true;
			rebuildStage();
		}
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
		Assets.loadLevel1Asset();
		while(!Assets.updateAssets()){
			System.out.println(Assets.manager.getProgress());
		}
		
		loadLevel();
		stage = new Stage(new FillViewport(viewport_width, viewport_height));
		UI = new Stage(new FillViewport(Constants.APP_WIDTH,
				Constants.APP_HEIGHT));
		rebuildStage();
	}

	@Override
	public void hide() {
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

	private void loadLevel() {
		switch (GameManager.currentLevel) {
		case 1:
			map = new TmxMapLoader().load("maps/level1/Level1.tmx");
			break;
		case 2:
			Music backgroundMusic = Assets.getMusic(Constants.LEVEL2_BACKGROUND_MUSIC);
			backgroundMusic.setVolume(0.3f);
			backgroundMusic.setLooping(true);
			backgroundMusic.play();
			map = new TmxMapLoader().load("maps/level2/Level2.tmx");
			break;
		case 3:
			map = new TmxMapLoader().load("maps/level3/Level3.tmx");
			break;
		}
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

			/* TODO URGENTE!! CAMBIAR POR METODOS LOS CASES */
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

		GravityButtonDown gbu = new GravityButtonDown(
				WorldUtils.createButtonDown(world, positionPhysicBody,
						worldCircle), worldCircle, this.ball);
		stage.addActor(gbu);
	}

	// TODO: Todos los metodos son casi iguales, hay que buscar alguna manera de
	// refactorizar esto
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

		GravityButtonUp gbu = new GravityButtonUp(WorldUtils.createButtonUp(
				world, positionPhysicBody, worldCircle), worldCircle, this.ball);
		stage.addActor(gbu);
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

		Rectangle rectangle = WorldTiledUtils.getWorldRectangle(m);
		Vector2 position = new Vector2(rectangle.x, rectangle.y);
		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByRectangle(
				position, rectangle);

		FlagActor p = new FlagActor(WorldUtils.createFlag(world,
				positionPhysicBody, rectangle), rectangle);
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

		WorldUtils.createChainShape(world, worldVertices);
	}

	private void setUpGui() {

		UI.clear();
		skin = new Skin(Gdx.files.internal(Constants.SKIN_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		Stack stack = new Stack();
		stack.setSize(Constants.APP_WIDTH, Constants.APP_HEIGHT);
		UI.addActor(stack);

		Table layerLevelMenu = buildLayerLevelMenu();
		stack.add(layerLevelMenu);
	}

	private Table buildLayerLevelMenu() {
		Table layer = new Table();
		layer.center().center();

		// + Next Button
		btnNext = new Button(skin, "next");
		layer.add(btnNext).maxWidth(300f).maxHeight(100f);
		btnNext.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onNextClicked();
			}
		});
		btnNext.setVisible(false);
		layer.row();

		// + Back Button
		btnBack = new Button(skin, "back");
		layer.add(btnBack).maxWidth(300f).maxHeight(100f);
		btnBack.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackClicked();
			}
		});
		layer.row();
		
		// + Exit Button
		btnExit = new Button(skin, "exit");
		layer.add(btnExit).maxWidth(300f).maxHeight(100f);
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
		loadLevel();
		rebuildStage();
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
			stage.addActor(stroke);
			strokes.add(stroke);
		}
		input.clear();
		createStroke = false;
	}

	private void drawUserInput() {
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		for (int i = 1; i < input.size; i++) {
			shapeRenderer.line(input.get(i - 1), input.get(i));
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

		Platform p = Platform.PlatformCreate(WorldUtils.createPlatformBody(
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

	private void checkRemoveStrokes(Vector2 point) {
		for (Stroke s : strokes) {
			for (Fixture f : s.getBody().getFixtureList()) {
				if (f.testPoint(point)) {
					world.destroyBody(s.getBody());
					s.remove();
					strokes.removeValue(s, true);
					break;
				}
			}
		}
	}

	@Override
	public boolean keyDown(int keycode) {

		if (Keys.Q == keycode) {
			drawDebug = !drawDebug;
		}

		if (Keys.R == keycode) {
			resetLevel = true;
		}

		if (Keys.P == keycode) {
			GameManager.isPaused = !GameManager.isPaused;
			stage.getBatch().setColor(Color.DARK_GRAY);
		}

		return super.keyDown(keycode);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		
		Vector3 point_aux = stage.getViewport().unproject(
				new Vector3(screenX, screenY, 0f));
		Vector2 point = new Vector2(point_aux.x, point_aux.y);

		if (Input.Buttons.RIGHT == button) {
			checkRemoveStrokes(point);
			rightButtonClicked = true;
		}
		
		
		if(!rightButtonClicked){
			Assets.getSound(Constants.DRAW_EFFECT).play();
		}
		
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!rightButtonClicked) {
			Vector2 currentPoint = stage.getViewport().unproject(
					new Vector2(screenX, screenY));
			if (input.size < Constants.MAX_POINTS) {
				input.add(currentPoint);
			}
		}
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!rightButtonClicked) {
			Vector2 currentPoint = stage.getViewport().unproject(
					new Vector2(screenX, screenY));
			if (input.size < Constants.MAX_POINTS) {
				input.add(currentPoint);
			}
			createStroke = true;
		}
		rightButtonClicked = false;
		Assets.getSound(Constants.DRAW_EFFECT).stop();
		return super.touchUp(screenX, screenY, pointer, button);
	}

	// -------------- Contact listener ----------------------------
	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		try {
			if (BodyUtils.isBall(a) && BodyUtils.isMortalObstacle(b)
					|| BodyUtils.isBall(b) && BodyUtils.isMortalObstacle(a)) {

				
				Assets.getSound(Constants.EXPLOSION_EFFECT).play();
				
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
				System.out.println("SIGUIENTE NIVEEEEL!");
				tiledMapRenderer.getSpriteBatch().setColor(Color.GRAY);
				GameManager.gameState = GameState.WIN_LEVEL;
				GameManager.isPaused = true;
			}

			if (BodyUtils.isBouncePlatform(a) && BodyUtils.isBall(b)
					|| BodyUtils.isBouncePlatform(b) && BodyUtils.isBall(a)) {

				
				Assets.getSound(Constants.JUMP_EFFECT).play();
				Body ball = BodyUtils.isBall(a) ? a : b;

				// TODO: Crear constante para la velocidad Y del salto
				ball.setLinearVelocity(new Vector2(ball.getLinearVelocity().x,
						11f));
			}

			if (BodyUtils.isMortalObstacle(a) && BodyUtils.isStroke(b)
					|| BodyUtils.isMortalObstacle(b) && BodyUtils.isStroke(a)) {

				// TODO: Intentar guardar en el UserData una referencia al actor
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
}

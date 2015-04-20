package com.tfg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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

public class Level6Screen extends InputAdapter implements Screen,
		ContactListener {

	// Effects
	private ParticleEffectPool ballExplosionPool;
	private Array<PooledEffect> effects = new Array<PooledEffect>();

	private Stage stage;
	private Stage UI;
	private InputMultiplexer inputMultiplexer;

	private static final int VIEWPORT_WIDTH = 60;
	private static final int VIEWPORT_HEIGHT = 34;
	private Viewport viewport;

	private World world;
	// private Body ground;

	private final float TIME_STEP = 1 / 300f;
	private float accumulator = 0f;

	private OrthographicCamera camera;
	private CameraHelper cameraHelper;

	private Box2DDebugRenderer box2Drenderer;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;

	/*---------LEVEL 2 MAP---------------*/
	private TiledMap map = new TmxMapLoader().load("Level3-II/Level3_II.tmx");
	private OrthogonalTiledMapRenderer tiledMapRenderer = new OrthogonalTiledMapRenderer(
			map, Constants.UNIT_SCALE);

	/*----- ACTORS -----------------*/
	private Array<Stroke> strokes;
	private Array<Platform> platforms;
	private Array<MortalObstacle> mortalObstacles;
	private Ball ball;

	private Label textFinishLevel;

	float[] strokePoints;

	boolean createStroke;

	// Stroke points
	private Array<Vector2> input;

	// Physics body to delete
	private Array<Body> deleteBodies;

	private boolean drawDebug = false;

	private boolean rightButtonClicked = false;

	private boolean resetLevel = false;

	public Level6Screen() {
	}

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		deleteStrokes();

		if (ball.getX() > 45) {
			cameraHelper.translateRight = true;
			cameraHelper.translateLeft = false;
		} else {
			cameraHelper.translateRight = false;
			cameraHelper.translateLeft = true;
		}

		// WE SHOW THE LEVEL WHEN ALL ASSETS HAS BEEN LOADED
		if (Assets.updateAssets()) {

			batch.setProjectionMatrix(camera.combined);
			tiledMapRenderer.setView(camera);
			tiledMapRenderer.render();
			cameraHelper.update(delta);

			// TODO: Extender stage para no iniciar el batch dos veces
			stage.getBatch().begin();
			drawEffects(delta);
			stage.getBatch().end();

			UI.draw();
			stage.draw();

			// renderer.render(world, camera.combined);

			if (drawDebug) {
				drawDebug();
			}

			drawUserInput();

			if (createStroke) {
				createStroke();
			}

			// Fixed timestep
			accumulator += delta;

			while (accumulator >= delta) {
				world.step(TIME_STEP, 6, 2);
				accumulator -= TIME_STEP;
			}

			stage.act(delta);
			UI.act(delta);
			// camera.translate(ball.getBody().getLinearVelocity().x * delta,
			// 0);
			// camera.update();

			if (GameManager.gameOver) {
				// resetLevel();
				// GameManager.restartLevel();
			}

			if (GameManager.gameState == GameState.WIN_LEVEL) {
				textFinishLevel.setVisible(true);
			} else if (GameManager.gameState == GameState.PLAYING_LEVEL) {
				textFinishLevel.setVisible(false);
			}
		}

		if (resetLevel) {
			GameManager.gameOver = true;
		}
	}

	private void setupCamera() {
		viewport = new FillViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		camera = (OrthographicCamera) viewport.getCamera();
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

	private void resetLevel() {

		resetLevel = false;

		/* TODO ORGANIZAR ESTE METODO Y MODULARLO */
		GameManager.gameState = GameState.PLAYING_LEVEL;

		// ---------- INITIALIZE PARTICLE POOL ----------------------
		setUpParticleEffects();

		deleteBodies = new Array<Body>();

		world = WorldUtils.createWorld();
		world.setContactListener(this);
		// ground = WorldUtils.createGround(world);
		box2Drenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();

		input = new Array<Vector2>();
		strokes = new Array<Stroke>();

		setupCamera();
		batch = new SpriteBatch();

		stage = new Stage(viewport, batch);

		cameraHelper = new CameraHelper(stage.getCamera());

		UI = new Stage();

		inputMultiplexer = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(inputMultiplexer);

		createStroke = false;

		Assets.loadLevel1Asset();
		platforms = new Array<Platform>();

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
			case "CHECKPOINT_FLAG":
				// setUpCheckpointFlag(m);
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

	// TODO: Estudiar si es necesario seguir utilizando objetos Rectangulo ya
	// que no se pueden rotar.
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
		LabelStyle ls = new LabelStyle();
		ls.font = new BitmapFont(Gdx.files.internal("font1.fnt"));
		ls.fontColor = Color.ORANGE;
		this.textFinishLevel = new Label("GREAT!", ls);
		textFinishLevel.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2);
		textFinishLevel.setVisible(false);
		UI.addActor(textFinishLevel);
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
		String attr = (String) m.getProperties().get("texture");
		String texture = mapTexture(attr);
		Rectangle worldRectangle = WorldTiledUtils.getWorldRectangle(m);
		Vector2 position = new Vector2(worldRectangle.x, worldRectangle.y);
		Vector2 positionPhysicBody = WorldUtils.getPhysicPositionByRectangle(
				position, worldRectangle);

		Platform p = Platform.PlatformCreate(WorldUtils.createPlatformBody(
				world, positionPhysicBody, worldRectangle), worldRectangle,
				texture);
		stage.addActor(p);
	}

	// Select the correct texture regarding "texture" property in the tiled map.
	private String mapTexture(String textureProperty) {
		String res = null;
		switch (textureProperty) {
		case "platform_wall":
			res = Constants.LEVEL3_WALL;
			break;
		case "platform2":
			res = Constants.LEVEL3_PLATFORM2;
			break;
		}
		return res;
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

	// --------------- Process input ---------------------------------

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
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		this.cameraHelper.setCamera(stage.getViewport().getCamera());
		this.camera = (OrthographicCamera) stage.getViewport().getCamera();
	}

	@Override
	public void show() {
		resetLevel();
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

	// --------------- Process input ---------------------------------

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		Vector3 point_aux = viewport
				.unproject(new Vector3(screenX, screenY, 0f));
		Vector2 point = new Vector2(point_aux.x, point_aux.y);

		if (Input.Buttons.RIGHT == button) {
			checkRemoveStrokes(point);
			rightButtonClicked = true;
		}

		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!rightButtonClicked) {
			Vector2 currentPoint = viewport.unproject(new Vector2(screenX,
					screenY));
			// Vector2 currentPoint = CameraHelper.unproject(screenX, screenY,
			// camera);
			if (input.size < Constants.MAX_POINTS) {
				input.add(currentPoint);
			}
		}
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!rightButtonClicked) {
			Vector2 currentPoint = viewport.unproject(new Vector2(screenX,
					screenY));
			// Vector2 currentPoint = CameraHelper.unproject(screenX, screenY,
			// camera);
			if (input.size < Constants.MAX_POINTS) {
				input.add(currentPoint);
			}
			createStroke = true;
		}
		rightButtonClicked = false;
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean keyDown(int keycode) {

		if (Keys.Q == keycode) {
			drawDebug = !drawDebug;
		}

		if (Keys.R == keycode) {
			resetLevel = true;
		}

		return super.keyDown(keycode);
	}

	/* ============ BEGIN CONTAC LISTENER METHODS ============================= */
	@Override
	public void beginContact(Contact contact) {
		Body a = contact.getFixtureA().getBody();
		Body b = contact.getFixtureB().getBody();

		try {
			if (BodyUtils.isBall(a) && BodyUtils.isMortalObstacle(b)
					|| BodyUtils.isBall(b) && BodyUtils.isMortalObstacle(a)) {

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
			}

			if (BodyUtils.isBouncePlatform(a) && BodyUtils.isBall(b)
					|| BodyUtils.isBouncePlatform(b) && BodyUtils.isBall(a)) {

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

	/* ============ END CONTAC LISTENER METHODS ============================= */
}
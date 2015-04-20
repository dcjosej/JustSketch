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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tfg.actors.Ball;
import com.tfg.actors.BouncePlatform;
import com.tfg.actors.FlagActor;
import com.tfg.actors.MortalObstacle;
import com.tfg.actors.Platform;
import com.tfg.actors.Stroke;
import com.tfg.actors.UI.BackgroundActor;
import com.tfg.utils.Assets;
import com.tfg.utils.BodyUtils;
import com.tfg.utils.CameraUtils;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;
import com.tfg.utils.GameState;
import com.tfg.utils.WorldUtils;

public class Level5Screen extends InputAdapter implements Screen,
		ContactListener {

	private Stage stage;
	private Stage UI;
	private InputMultiplexer inputMultiplexer;

	private static final int VIEWPORT_WIDTH = 60;
	private static final int VIEWPORT_HEIGHT = 34;

	private World world;
	// private Body ground;

	private final float TIME_STEP = 1 / 300f;
	private float accumulator = 0f;

	private OrthographicCamera camera;
	private Box2DDebugRenderer box2Drenderer;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Texture strokeTexture;

	/*---------LEVEL 2 MAP---------------*/
	private TiledMap map = new TmxMapLoader().load("Level2.tmx");
	private OrthogonalTiledMapRenderer tiledMapRenderer = new OrthogonalTiledMapRenderer(
			map, 1 / 32f);

	/*----- ACTORS -----------------*/
	private Array<Stroke> strokes;
	private Array<Platform> platforms;
	private Array<MortalObstacle> mortalObstacles;
	private Ball ball;

	private BackgroundActor backgrundImage; // Image actor for level 1
											// background.
	private Label textFinishLevel;

	float[] strokePoints;

	boolean createStroke;

	// Stroke points
	private Array<Vector2> input;

	private boolean drawDebug = false;

	/* TODO REVISAR LA VARIABLE GRAVEDAD ¿PODRIA ESTAR MEJOR EN OTRO LUGAR? */
	private float gravityDir = -1;

	private boolean rightButtonClicked = false;

	public Level5Screen() {
	}

	private void setupCamera() {
		camera = new OrthographicCamera();
		camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0f);
		camera.update();
	}

	@Override
	public void render(float delta) {
		Gdx.graphics.getGL20().glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// WE SHOW THE LEVEL WHEN ALL ASSETS HAS BEEN LOADED
		if (Assets.updateAssets()) {

			camera.update();
			batch.setProjectionMatrix(camera.combined);
			tiledMapRenderer.setView(camera);
			tiledMapRenderer.render();

			UI.draw();
			stage.draw();

			// renderer.render(world, camera.combined);

			if (drawDebug) {
				drawDebug();
			}

			drawUserInput();

			if (strokePoints != null) {
				drawStroke();
			}

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
	}

	private void resetLevel() {

		/* TODO ORGANIZAR ESTE METODO Y MODULARLO */
		GameManager.gameState = GameState.PLAYING_LEVEL;

		world = WorldUtils.createWorld();
		world.setContactListener(this);
		// ground = WorldUtils.createGround(world);
		box2Drenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();

		input = new Array<Vector2>();
		strokes = new Array<Stroke>();

		setupCamera();
		batch = new SpriteBatch();

		stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT),
				batch);

		UI = new Stage();

		inputMultiplexer = new InputMultiplexer(stage, this);
		Gdx.input.setInputProcessor(inputMultiplexer);
		strokeTexture = new Texture(Gdx.files.internal("texture.jpg"));

		createStroke = false;

		Assets.loadLevel1Asset();
		platforms = new Array<Platform>();
		gravityDir = -1;

		while (!Assets.updateAssets()) {
		} /* TODO ARREGLAR ESTO */

		// setUpBackground();
		// setUpPlatforms();
		// setUpBall();
		// setUpObstacles();
		// setUpFlag();
		setUpGui();
		setupMapStaff();

	}

	private void setupMapStaff() {

		/* TODO CAMBIAR LOS /32 POR UNA CONSTANTE */

		for (MapObject m : map.getLayers().get("Colliders").getObjects()) {

			String objectType = (String) m.getProperties().get("type");

			/* TODO URGENTE!! CAMBIAR POR METODOS LOS CASES */
			switch (objectType) {
			case "CHAINCOLLIDER":
				setUpChainColliders(m);
				break;

			case "PLATFORM":
				setUpPlatforms(m);
				break;

			case "BOUNCE_PLATFORM":
				setUpBouncePlatforms(m);
				break;

			case "FLAG":
				setUpFlags(m);
				break;
			case "BALL":
				setUpBall(m);
				break;
			}
		}
	}

	private void setUpBouncePlatforms(MapObject m) {
		RectangleMapObject tiledPlatform = (RectangleMapObject) m;
		System.out.println(tiledPlatform.getRectangle().x / 32f);

		Vector2 position = new Vector2(tiledPlatform.getRectangle().x / 32f,
				tiledPlatform.getRectangle().y / 32f);

		Rectangle tiledRectangle = tiledPlatform.getRectangle();
		Rectangle worldRectangle = new Rectangle(tiledRectangle.x / 32f,
				tiledRectangle.y / 32f, tiledRectangle.width / 32f,
				tiledRectangle.height / 32f);

		Vector2 positionPhysicBody = new Vector2(position.x
				+ worldRectangle.width / 2, position.y + worldRectangle.height
				/ 2);

		BouncePlatform bp = new BouncePlatform(
				WorldUtils.createBouncePlatformBody(world, positionPhysicBody,
						worldRectangle), worldRectangle);
		stage.addActor(bp);
	}

	private void setUpFlags(MapObject m) {
		RectangleMapObject tiledPlatform = (RectangleMapObject) m;

		Vector2 position = new Vector2(tiledPlatform.getRectangle().x / 32f,
				tiledPlatform.getRectangle().y / 32f);

		Rectangle tiledRectangle = tiledPlatform.getRectangle();
		Rectangle worldRectangle = new Rectangle(tiledRectangle.x / 32f,
				tiledRectangle.y / 32f, tiledRectangle.width / 32f,
				tiledRectangle.height / 32f);

		Vector2 positionPhysicBody = new Vector2(position.x
				+ worldRectangle.width / 2, position.y + worldRectangle.height
				/ 2);

		FlagActor p = new FlagActor(WorldUtils.createFlag(world,
				positionPhysicBody, worldRectangle), worldRectangle);
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

	private void drawStroke() {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(strokeTexture, strokePoints, 0, 1);
		batch.end();
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
		RectangleMapObject tiledPlatform = (RectangleMapObject) m;
		System.out.println(tiledPlatform.getRectangle().x / 32f);

		Vector2 position = new Vector2(tiledPlatform.getRectangle().x / 32f,
				tiledPlatform.getRectangle().y / 32f);

		Rectangle tiledRectangle = tiledPlatform.getRectangle();
		Rectangle worldRectangle = new Rectangle(tiledRectangle.x / 32f,
				tiledRectangle.y / 32f, tiledRectangle.width / 32f,
				tiledRectangle.height / 32f);

		Vector2 positionPhysicBody = new Vector2(position.x
				+ worldRectangle.width / 2, position.y + worldRectangle.height
				/ 2);

		Platform p = new Platform(WorldUtils.createPlatformBody(world,
				positionPhysicBody, worldRectangle), worldRectangle);
		stage.addActor(p);
	}

	private void setUpBall(MapObject m) {
		EllipseMapObject ballTiled = (EllipseMapObject) m;
		Circle circleTiled = new Circle(new Vector2(ballTiled.getEllipse().x,
				ballTiled.getEllipse().y), ballTiled.getEllipse().width / 2);

		Vector2 position = new Vector2(circleTiled.x / 32f, circleTiled.y / 32f);

		Circle worldCircle = new Circle(position, circleTiled.radius / 32f);

		Vector2 positionPhysicBody = new Vector2(position.x
				+ worldCircle.radius, position.y + worldCircle.radius);

		/* TODO ¿PASAR TAMBIEN LA POSICION DEL CENTRO? */
		this.ball = new Ball(WorldUtils.createBall(world, worldCircle,
				positionPhysicBody), worldCircle);

		stage.addActor(ball);
	}

	@Override
	public void resize(int width, int height) {

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

		if (Input.Buttons.RIGHT == button) {
			Vector2 point = CameraUtils.unproject(screenX, screenY, camera);
			checkRemoveStrokes(point);
			rightButtonClicked = true;
		}

		return true;

	}

	private void checkRemoveStrokes(Vector2 point) {

		/* TODO CREAR POLIGONO PARA EL AREA TOUCHABLE */

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
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!rightButtonClicked) {
			Vector2 currentPoint = CameraUtils.unproject(screenX, screenY,
					camera);
			if (input.size < Constants.MAX_POINTS) {
				input.add(currentPoint);
			}
		}
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (!rightButtonClicked) {
			Vector2 currentPoint = CameraUtils.unproject(screenX, screenY,
					camera);
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
			this.resetLevel();
		}

		if (Keys.C == keycode) {
			ball.getBody().setGravityScale(gravityDir);

			gravityDir *= -1;
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
				System.out.println("OBSTACULO PELIGROSO!");
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
				
				ball.applyLinearImpulse(new Vector2(0, 30f), b.getWorldCenter(), false);
			}

		} catch (Exception e) {
		}
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
	}

	/* ============ END CONTAC LISTENER METHODS ============================= */
}
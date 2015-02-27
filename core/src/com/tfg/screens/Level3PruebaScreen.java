package com.tfg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
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
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tfg.actors.Ball;
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

public class Level3PruebaScreen extends InputAdapter implements Screen,
		ContactListener {

	private Stage stage;
	private Stage UI;

	private static final int VIEWPORT_WIDTH = 40;
	private static final int VIEWPORT_HEIGHT = 23;

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
	private TiledMap map = new TmxMapLoader().load("map2.tmx");
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

	public Level3PruebaScreen() {
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

		setupCamera();
		batch = new SpriteBatch();

		stage = new Stage(new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
				camera), batch);

		UI = new Stage();

		Gdx.input.setInputProcessor(this);
		strokeTexture = new Texture(Gdx.files.internal("texture.jpg"));

		createStroke = false;

		Assets.loadLevel1Asset();
		platforms = new Array<Platform>();

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
		System.out.println(map.getLayers().get(2).getName());
		for (MapObject m : map.getLayers().get(2).getObjects()) {
			RectangleMapObject tiledPlatform = (RectangleMapObject) m;
			System.out.println(tiledPlatform.getRectangle().x / 32f);

			Vector2 position = new Vector2(
					tiledPlatform.getRectangle().x / 32f,
					tiledPlatform.getRectangle().y / 32f);

			Rectangle tiledRectangle = tiledPlatform.getRectangle();
			Rectangle worldRectangle = new Rectangle(tiledRectangle.x / 32f,
					tiledRectangle.y / 32f, tiledRectangle.width / 32f,
					tiledRectangle.height / 32f);

			Vector2 positionPhysicBody = new Vector2(position.x
					+ worldRectangle.width / 2, position.y
					+ worldRectangle.height / 2);

			Platform p = new Platform(WorldUtils.createPlatformBody(world,
					positionPhysicBody, worldRectangle), worldRectangle);
			stage.addActor(p);

		}
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

	private void setUpBackground() {
		backgrundImage = new BackgroundActor(
				Assets.getTexture(Constants.BACKGROUND_TEXTURE));
		backgrundImage.setPosition(0, 0);
		UI.addActor(backgrundImage);
	}

	private void createStroke() {
		Stroke stroke = WorldUtils.processStroke(input, world);
		// strokes.add(stroke);
		if (stroke != null) {
			stage.addActor(stroke);
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
	}

	private void setUpPlatforms() {
		Vector2 positionP1 = new Vector2(13.5f, 0);
		Rectangle r1 = new Rectangle(positionP1.x, positionP1.y, 8, 2);
		Platform p1 = new Platform(WorldUtils.createPlatformBody(world,
				positionP1, r1), r1); /* TODO RELACIONAR EL RECTANGLE CON LO DEMAS */

		stage.addActor(p1);

		Vector2 positionP2 = new Vector2(15f, 11.5f);
		Rectangle r2 = new Rectangle(positionP2.x, positionP2.y, 2, 1);
		Platform p2 = new Platform(WorldUtils.createPlatformBody(world,
				positionP2, r2), r2); /* TODO RELACIONAR EL RECTANGLE CON LO DEMAS */

		stage.addActor(p2);

		Vector2 positionP3 = new Vector2(positionP1.x - 3.5f, positionP1.y
				+ r1.height / 2 + 0.5f);
		Rectangle r3 = new Rectangle(positionP3.x, positionP3.y, 0.5f, 1f);
		Platform p3 = new Platform(WorldUtils.createPlatformBody(world,
				positionP3, r3), r3); /* TODO RELACIONAR EL RECTANGLE CON LO DEMAS */

		stage.addActor(p3);

		Vector2 positionP4 = new Vector2(positionP1.x - 1f, positionP1.y
				+ r1.height / 2 + 0.5f);
		Rectangle r4 = new Rectangle(positionP4.x, positionP4.y, 0.5f, 1f);
		Platform p4 = new Platform(WorldUtils.createPlatformBody(world,
				positionP4, r4), r4); /* TODO RELACIONAR EL RECTANGLE CON LO DEMAS */

		stage.addActor(p4);
	}

	private void setUpFlag() {
		Vector2 position1 = new Vector2(23, 7);
		Rectangle r1 = new Rectangle(position1.x, position1.y, 1.3f, 2);

		FlagActor flag = new FlagActor(WorldUtils.createFlag(world, position1),
				r1);
		stage.addActor(flag);
	}

	private void setUpBall() {
		Vector2 positionBall = new Vector2(11, 3);
		this.ball = new Ball(WorldUtils.createBall(world, positionBall),
				new Circle(positionBall, 0.3f));
		stage.addActor(this.ball);
	}

	private void setUpObstacles() {
		mortalObstacles = new Array<MortalObstacle>();
		Vector2 positionM1 = new Vector2(20, 10);
		MortalObstacle m1 = new MortalObstacle(
				WorldUtils.createTestMortalObstacle(world, positionM1),
				new Rectangle(positionM1.x, positionM1.y, 2, 2));

		mortalObstacles.add(m1);
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
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		Vector2 currentPoint = CameraUtils.unproject(screenX, screenY, camera);
		if (input.size < Constants.MAX_POINTS) {
			input.add(currentPoint);
		}

		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

		Vector2 currentPoint = CameraUtils.unproject(screenX, screenY, camera);
		if (input.size < Constants.MAX_POINTS) {
			input.add(currentPoint);
		}
		createStroke = true;
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
				GameManager.gameState = GameState.WIN_LEVEL;
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
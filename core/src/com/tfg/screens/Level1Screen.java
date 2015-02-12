package com.tfg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.tfg.actors.Ball;
import com.tfg.actors.MortalObstacle;
import com.tfg.actors.Platform;
import com.tfg.actors.Stroke;
import com.tfg.box2d.UserData;
import com.tfg.box2d.UserDataType;
import com.tfg.utils.Assets;
import com.tfg.utils.BodyUtils;
import com.tfg.utils.CameraUtils;
import com.tfg.utils.Constants;
import com.tfg.utils.GameManager;
import com.tfg.utils.WorldUtils;

public class Level1Screen extends InputAdapter implements Screen,
		ContactListener {

	private Stage stage;

	private static final int VIEWPORT_WIDTH = 20;
	private static final int VIEWPORT_HEIGHT = 13;

	private World world;
	private Body ground;

	private final float TIME_STEP = 1 / 300f;
	private float accumulator = 0f;

	private OrthographicCamera camera;
	private Box2DDebugRenderer box2Drenderer;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Texture strokeTexture;

	/*----- ACTORS -----------------*/
	private Array<Stroke> strokes;
	private Array<Platform> platforms;
	private Array<MortalObstacle> mortalObstacles;
	private Ball ball;

	float[] strokePoints;

	boolean createStroke;

	// Stroke points
	private Array<Vector2> input;

	private boolean drawDebug = true;

	public Level1Screen() {
	}

	private void setupCamera() {
		camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0f);
		camera.update();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// WE SHOW THE LEVEL WHEN ALL ASSETS HAS BEEN LOADED
		if (Assets.updateAssets()) {

			batch.setProjectionMatrix(camera.combined);
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
			camera.translate(ball.getBody().getLinearVelocity().x * delta, ball
					.getBody().getLinearVelocity().y * delta);
			camera.update();

			if (GameManager.gameOver) {
				// resetLevel();
				// GameManager.restartLevel();
			}
		}
	}

	private void resetLevel() {
		world = WorldUtils.createWorld();
		world.setContactListener(this);
		ground = WorldUtils.createGround(world);
		box2Drenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();

		input = new Array<Vector2>();

		setupCamera();
		batch = new SpriteBatch();

		stage = new Stage(new FillViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT,
				camera), batch);

		Gdx.input.setInputProcessor(this);
		strokeTexture = new Texture(Gdx.files.internal("texture.jpg"));

		createStroke = false;

		Assets.loadLevel1Asset();
		platforms = new Array<Platform>();

		setUpPlatforms();
		setUpBall();
		setUpObstacles();

	}

	private void createStroke() {
		Stroke stroke = WorldUtils.processStroke(input, world);
		// strokes.add(stroke);
		stage.addActor(stroke);
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
		Vector2 positionP1 = new Vector2(4, 10);
		Rectangle r1 = new Rectangle(positionP1.x, positionP1.y, 4, 1);
		Platform p1 = new Platform(WorldUtils.createPlatformBody(world,
				positionP1), r1);

		stage.addActor(p1);

		Vector2 positionP2 = new Vector2(20, 6);
		Rectangle r2 = new Rectangle(positionP2.x, positionP2.y, 4, 1);
		Platform p2 = new Platform(WorldUtils.createPlatformBody(world,
				positionP2), r2);

		stage.addActor(p2);
	}

	private void setUpBall() {
		Vector2 positionBall = new Vector2(1, 8);
		this.ball = new Ball(WorldUtils.createBall(world, positionBall),
				new Circle(positionBall, 1));
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
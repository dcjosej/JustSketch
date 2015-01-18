package com.tfg.stages;

import net.dermetfan.gdx.math.GeometryUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.tfg.utils.CameraUtils;
import com.tfg.utils.Constants;
import com.tfg.utils.WorldUtils;

public class GameStage extends Stage {
	private static final int VIEWPORT_WIDTH = 20;
	private static final int VIEWPORT_HEIGHT = 13;

	private World world;
	private Body ground;

	private final float TIME_STEP = 1 / 300f;
	private float accumulator = 0f;

	private OrthographicCamera camera;
	private Box2DDebugRenderer renderer;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Texture strokeTexture;

	float[] strokePoints;

	// Stroke points
	private Array<Vector2> input;
	/* ATRIBUTOS DE PRUEBAS */
	// private Array<Vector2> smoothInput;
	private Array<Vector2> smoothInput;
	private Polygon[] triangles;
	
	
	public static FileHandle fh;
	
	/* FIN ATRIBUTOS DE PRUEBAS */
	private boolean painting;

	public GameStage() {
		
		fh = Gdx.files.local("myLog.log");
		
		world = WorldUtils.createWorld();
		ground = WorldUtils.createGround(world);
		renderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();

		input = new Array<Vector2>();
		painting = false;

		setupCamera();

		Gdx.input.setInputProcessor(this);
		strokeTexture = new Texture(Gdx.files.internal("texture.jpg"));
		batch = new SpriteBatch();
	}

	private void setupCamera() {
		camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		camera.position.set(camera.viewportWidth / 2,
				camera.viewportHeight / 2, 0f);
		camera.update();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// Fixed timestep
		accumulator += delta;

		while (accumulator >= delta) {
			world.step(TIME_STEP, 6, 2);
			accumulator -= TIME_STEP;
		}

		// TODO: Implement interpolation

		if (painting == false && input.size != 0) {
			try {
				smoothInput = WorldUtils.processStroke(input, world);
			} catch (Throwable oops) {
				oops.printStackTrace();
			}
			// TODO- CAMBIAR smoothPoint por el atributo strokePoints

			if (input.size > 8) {
				try {
					strokePoints = GeometryUtils.toFloatArray(smoothInput)
							.toArray();

					triangles = WorldUtils.triangulate(new Polygon(
							GeometryUtils.toFloatArray(smoothInput).toArray()));
					input.clear();
				} catch (Throwable oops) {

				}
			} else {
				input.clear();
			}
		}
	}

	@Override
	public void draw() {
		super.draw();
		renderer.render(world, camera.combined);

		drawUserInput();

		if (smoothInput != null) {
			drawDebug();
		}

		if (strokePoints != null) {
			drawStroke();
		}
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
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		for (int i = 1; i < smoothInput.size; i++) {
			shapeRenderer.line(smoothInput.get(i - 1), smoothInput.get(i));
		}
		if (triangles != null) {
			shapeRenderer.setColor(Color.GREEN);
			for (Polygon p : triangles) {
				float[] vertexs = p.getTransformedVertices();
				shapeRenderer.triangle(vertexs[0], vertexs[1], vertexs[2],
						vertexs[3], vertexs[4], vertexs[5]);
			}
		}

		shapeRenderer.end();
	}

	// --------------- Process input ---------------------------------

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		painting = true;
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

		painting = false;
		return super.touchUp(screenX, screenY, pointer, button);
	}
}
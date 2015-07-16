package com.jsk.utils;

import net.dermetfan.gdx.math.GeometryUtils;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.jsk.actors.Stroke;
import com.jsk.box2d.BallUserData;
import com.jsk.box2d.BouncePlatformUserData;
import com.jsk.box2d.ButtonDownUserData;
import com.jsk.box2d.ButtonUpUserData;
import com.jsk.box2d.FixtureStrokeUserData;
import com.jsk.box2d.FlagUserData;
import com.jsk.box2d.MortalObstacleUserData;
import com.jsk.box2d.PlatformUserData;
import com.jsk.box2d.StrokeUserData;

public class WorldUtils {

	/** a temporarily used array, returned by some methods */
	private static final FloatArray tmpFloatArray = new FloatArray();

	public static World createWorld() {
		World world = new World(Constants.WORLD_GRAVITY, true);
		return world;
	}

	// public static Body createGround(World world) {
	// BodyDef bodyDef = new BodyDef();
	// bodyDef.position
	// .set(new Vector2(Constants.GROUND_X, Constants.GROUND_Y));
	//
	// Body body = world.createBody(bodyDef);
	// PolygonShape shape = new PolygonShape();
	// shape.setAsBox(Constants.GROUND_WIDTH, Constants.GROUND_HEIGHT / 2);
	// body.createFixture(shape, Constants.GROUND_DENSITY);
	// shape.dispose();
	//
	// return body;
	// }

	/* PROCESS STROKE FUNCTIONS */

	public static Stroke processStroke(Array<Vector2> input, World world) {
		Array<Vector2> points = new Array<Vector2>();
		Stroke res = null;
		// checkCirclesAndRectangles(input);
		points = simplify(input);

		if (points.size > 2) {

			for (int i = 0; i < Constants.NUM_ITERATIONS; i++) {
				points = smooth(points);
			}

			// res = extrudePoints(res);
			res = createPhysicsBodies5(points, world);
		}
		return res;
	}

	// private static void createPhysicsBodies(Array<Vector2> input, World
	// world) {
	//
	// FloatArray floatArray = GeometryUtils.toFloatArray(input);
	// // floatArray.reverse();
	// float[] points = floatArray.toArray();
	//
	// Polygon pol = new Polygon(points);
	//
	// Polygon[] triangles = triangulate(pol);
	//
	// boolean canCreateBody = checkTriangles(triangles);
	//
	// BodyDef bodyDef = new BodyDef();
	// bodyDef.type = BodyType.DynamicBody;
	// // bodyDef.position.set(new Vector2(
	// // triangles[0].getTransformedVertices()[0],
	// // triangles[0].getTransformedVertices()[1]));
	//
	// Body body = world.createBody(bodyDef);
	//
	// for (Polygon p : triangles) {
	//
	// PolygonShape shape = new PolygonShape();
	// float[] vertices = p.getTransformedVertices();
	// Array<Vector2> vAux = GeometryUtils.toVector2Array(new FloatArray(
	// vertices));
	//
	// FloatArray aux = GeometryUtils.toFloatArray(vAux);
	// // aux.reverse();
	// shape.set(aux.toArray());
	//
	// body.createFixture(shape, 1.0f);
	// shape.dispose();
	// }
	// }

	private static Stroke createPhysicsBodies5(Array<Vector2> input, World world) {

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		Body body = world.createBody(bodyDef);
		for (int i = 0; i < input.size - 1; i++) {
			Vector2 point = input.get(i);
			Vector2 dir = input.get(i + 1).cpy().sub(point);
			float distance = dir.len();
			float angle = dir.angle() * MathUtils.degreesToRadians;

			// System.out.println("==========RECTANGLE: " + i);
			// System.out.println("Distance: " + distance);
			// System.out.println("Angle: " + angle);
			// System.out.println("Dir: " + dir);
			// System.out.println("Center: " + dir.cpy().scl(0.5f));

			PolygonShape shape = new PolygonShape();

			shape.setAsBox(distance / 2, Constants.THICKNESS / 2, dir.cpy()
					.scl(0.5f).add(point), angle);

			Rectangle rectangle = new Rectangle(point.x, point.y, distance,
					Constants.THICKNESS);
			
			
			
			
			
			
			
			
			
			FixtureStrokeUserData fixtureStroke = new FixtureStrokeUserData(
					rectangle, angle);

//			Box2DSprite fixtureStroke = new Box2DSprite(new Sprite(Assets.getTextureRegion("pencil")));
//			fixtureStroke.setSize(rectangle.width, rectangle.height);
////			spriteStroke.setPosition(rectangle.x - rectangle.width/2, rectangle.y);
//			fixtureStroke.setOrigin(0, 0);
////			spriteStroke.setRotation(angle * MathUtils.radiansToDegrees);
////			fixtureStroke.setOrigin(0, 0);
			
			
			
			
			
			
			
			
			
			// CircleShape shape = new CircleShape();
			// shape.setPosition(dir.cpy().add(point));
			// shape.setRadius(dir.len()/2);

			if (rectangle.area() >= 0.001f) {
				body.createFixture(shape, 1.0f).setUserData(fixtureStroke);
			}
			// body.createFixture(shape, 1.0f);
		}
		body.setUserData(new StrokeUserData());
		Stroke stroke = new Stroke(body);
		return stroke;

	}

	// private static void createPhysicsBodies4(Array<Vector2> input, World
	// world) {
	// FloatArray floatArray = GeometryUtils.toFloatArray(input);
	// float[] points = floatArray.toArray();
	//
	// Polygon pol = new Polygon(points);
	//
	// BodyDef bodyDef = new BodyDef();
	// bodyDef.type = BodyType.DynamicBody;
	// Body body = world.createBody(bodyDef);
	//
	// PolygonShape shape = new PolygonShape();
	// shape.set(GeometryUtils.toFloatArray(input).items);
	// body.createFixture(shape, 1.0f);
	// shape.dispose();
	// }
	//
	// private static void createPhysicsBodies3(Array<Vector2> input, World
	// world) {
	// FloatArray floatArray = GeometryUtils.toFloatArray(input);
	// float[] points = floatArray.toArray();
	//
	// BodyDef bodyDef = new BodyDef();
	// bodyDef.type = BodyType.DynamicBody;
	// Body body = world.createBody(bodyDef);
	//
	// ChainShape shape = new ChainShape();
	// shape.createChain(points);
	//
	// body.createFixture(shape, 1.0f);
	// shape.dispose();
	// }

	// private static void createPhysicsBodies2(Array<Vector2> input, World
	// world) {
	// FloatArray floatArray = GeometryUtils.toFloatArray(input);
	// // floatArray.reverse();
	// float[] points = floatArray.toArray();
	//
	// Polygon pol = new Polygon(points);
	//
	// // boolean isConvex = GeometryUtils.isConvex(new FloatArray(pol
	// // .getTransformedVertices()));
	//
	// BodyDef bodyDef = new BodyDef();
	// bodyDef.type = BodyType.DynamicBody;
	// Body body = world.createBody(bodyDef);
	//
	// // if (isConvex) {
	// // PolygonShape shape = new PolygonShape();
	// // float[] vertices = pol.getTransformedVertices();
	// // Array<Vector2> vAux = GeometryUtils.toVector2Array(new FloatArray(
	// // vertices));
	// //
	// // FloatArray aux = GeometryUtils.toFloatArray(vAux);
	// // // aux.reverse();
	// // shape.set(aux.toArray());
	// //
	// // body.createFixture(shape, 1.0f);
	// // shape.dispose();
	// // } else {
	// boolean isRectangle = false;
	// Polygon[] polygons = null;
	// try {
	// polygons = GeometryUtils.decompose(pol);
	// } catch (Throwable oops) {
	// isRectangle = true;
	// // System.out.println("CUADRADOOOOO!!!!!!");
	// }
	// // boolean canCreatePhysics = checkTriangles(poligons);
	//
	// if (!isRectangle) {
	// // bodyDef.position.set(new Vector2(
	// // triangles[0].getTransformedVertices()[0],
	// // triangles[0].getTransformedVertices()[1]));
	// if (checkPolygons(polygons)) {
	// for (Polygon p : polygons) {
	// writeLog("============= POLYGON BEGIN===========================\n");
	// writeLog("\n");
	// writeLog(Arrays.toString(p.getTransformedVertices()));
	// writeLog(p.area() + "");
	// writeLog("\n");
	// PolygonShape shape = new PolygonShape();
	// float[] vertices = p.getTransformedVertices();
	// Array<Vector2> vAux = GeometryUtils
	// .toVector2Array(new FloatArray(vertices));
	//
	// FloatArray aux = GeometryUtils.toFloatArray(vAux);
	// // aux.reverse();
	// shape.set(aux.toArray());
	//
	// body.createFixture(shape, 1.0f);
	// shape.dispose();
	// writeLog("============= POLYGON END ===========================\n");
	// }
	// }
	// } else {
	// PolygonShape shape = new PolygonShape();
	//
	// input.get(0).isOnLine(input.get(1), 0.4f);
	// float width = input.get(0).dst(input.get(input.size - 1)) * 30;
	// shape.setAsBox(width / 2, Constants.THICKNESS / 2, input.get(0), 0f);
	// body.createFixture(shape, 1.0f);
	// shape.dispose();
	// }
	// // }
	// }

	private static Array<Vector2> smooth(Array<Vector2> input) {
		Array<Vector2> res = new Array<Vector2>();
		res.ensureCapacity(input.size * 2);

		for (int i = 0; i < input.size - 1; i++) {
			Vector2 p0 = input.get(i);
			Vector2 p1 = input.get(i + 1);

			Vector2 Q = new Vector2(0.75f * p0.x + 0.25f * p1.x, 0.75f * p0.y
					+ 0.25f * p1.y);
			Vector2 R = new Vector2(0.25f * p0.x + 0.75f * p1.x, 0.25f * p0.y
					+ 0.75f * p1.y);
			res.add(Q);
			res.add(R);
		}
		return res;
	}

	private static Array<Vector2> simplify(Array<Vector2> input) {
		Array<Vector2> res = new Array<Vector2>();

		Vector2 lastPoint = input.get(0);
		Vector2 currentPoint = new Vector2();

		res.add(lastPoint);
		for (int i = 1; i < input.size; i++) {
			currentPoint = input.get(i);
			
			float dstTolerance = Gdx.app.getType() == ApplicationType.Android ? Constants.DST_TOLERANCE_ANDROID : Constants.DST_TOLERANCE;
			
			if (currentPoint.dst(lastPoint) > dstTolerance
					* dstTolerance) {
				res.add(currentPoint);
				lastPoint = currentPoint;
			}
		}

		if (!lastPoint.equals(currentPoint)) {
			res.add(currentPoint);
		}

		return res;
	}

	// public static Polygon[] triangulate(Polygon concave) {
	// @SuppressWarnings("unchecked")
	// Array<Vector2> polygonVertices = Pools.obtain(Array.class);
	// polygonVertices.clear();
	// tmpFloatArray.clear();
	// tmpFloatArray.addAll(concave.getTransformedVertices());
	// polygonVertices.addAll(GeometryUtils.toVector2Array(tmpFloatArray));
	// ShortArray indices = new EarClippingTriangulator()
	// .computeTriangles(tmpFloatArray);
	//
	// @SuppressWarnings("unchecked")
	// Array<Vector2> vertices = Pools.obtain(Array.class);
	// vertices.clear();
	// vertices.ensureCapacity(indices.size);
	// vertices.size = indices.size;
	// for (int i = 0; i < indices.size; i++)
	// vertices.set(i, polygonVertices.get(indices.get(i)));
	// Polygon[] polygons = GeometryUtils.toPolygonArray(vertices, 3);
	//
	// polygonVertices.clear();
	// vertices.clear();
	// Pools.free(polygonVertices);
	// Pools.free(vertices);
	// return polygons;
	// }

	/**
	 * Calculate the position of the physic body regarding his bounding box
	 * 
	 * @param position
	 * @param rectangle
	 * @return
	 */
	public static Vector2 getPhysicPositionByRectangle(Vector2 position,
			Rectangle rectangle) {
		Vector2 res = new Vector2(position.x + rectangle.width / 2, position.y
				+ rectangle.height / 2);
		return res;
	}

	/**
	 * Calculate the position of the physic body regarding his bounding circle
	 * 
	 * @param position
	 * @param rectangle
	 * @return
	 */
	public static Vector2 getPhysicPositionByCircle(Vector2 position,
			Circle circle) {
		Vector2 res = new Vector2(position.x + circle.radius, position.y
				+ circle.radius);
		return res;
	}

	/* ========== CREATE BODIES ACTORS =========================== */

	public static Body createPlatformBody(World world, Vector2 position,
			Rectangle rectangle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new PlatformUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rectangle.width / 2, rectangle.height / 2);

		res.createFixture(shape, 1.0f);

		shape.dispose();

		return res;
	}

	public static Body createBouncePlatformBody(World world, Vector2 position,
			Rectangle rectangle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new BouncePlatformUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rectangle.width / 2, rectangle.height / 2);

		res.createFixture(shape, 1.0f);

		shape.dispose();
		return res;
	}

	public static Body createBall(World world, Circle circle, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.bullet = true;
		bodyDef.allowSleep = false;

		Body res = world.createBody(bodyDef);
		res.setUserData(new BallUserData());
		res.setLinearVelocity(Constants.BALL_LINEAR_VELOCITY,
				res.getLinearVelocity().y);

		CircleShape shape = new CircleShape();
		shape.setRadius(circle.radius);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.1f;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}

	public static Body createMortalObstacle(World world, Vector2 position,
			Rectangle rectangle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new MortalObstacleUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rectangle.width / 2, rectangle.height / 2);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}

	public static Body createFlag(World world, Vector2 position,
			Rectangle rectangle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new FlagUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rectangle.width / 2, rectangle.height / 2);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}

	public static Body createChainShape(World world, float[] vertices) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		Body res = world.createBody(bodyDef);

		ChainShape shape = new ChainShape();
		shape.createLoop(vertices);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);
		shape.dispose();
		return res;
	}

	public static Body createChainShape2(World world, float[] vertices) {

		Polygon pol = new Polygon(vertices);
		Polygon[] polygons = GeometryUtils.decompose(pol);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		Body res = world.createBody(bodyDef);

		for (Polygon p : polygons) {
			PolygonShape shape = new PolygonShape();
			float[] vertices_aux = p.getTransformedVertices();
			Array<Vector2> vAux = GeometryUtils.toVector2Array(new FloatArray(
					vertices_aux));

			FloatArray aux = GeometryUtils.toFloatArray(vAux);
			// aux.reverse();
			shape.set(aux.toArray());

			res.createFixture(shape, 1.0f);
			shape.dispose();
		}

		return res;
	}

	public static Body createButtonUp(World world, Vector2 position,
			Circle circle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new ButtonUpUserData());

		CircleShape shape = new CircleShape();
		shape.setRadius(circle.radius);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}

	public static Body createButtonDown(World world, Vector2 position,
			Circle circle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new ButtonDownUserData());

		CircleShape shape = new CircleShape();
		shape.setRadius(circle.radius);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}
}
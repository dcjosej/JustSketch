package com.tfg.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tfg.BodyEditorLoader;
import com.tfg.actors.Platform;
import com.tfg.actors.Stroke;
import com.tfg.box2d.BallUserData;
import com.tfg.box2d.FixtureStrokeUserData;
import com.tfg.box2d.FlagUserData;
import com.tfg.box2d.MortalObstacleUserData;
import com.tfg.box2d.PlatformUserData;
import com.tfg.box2d.StrokeUserData;

public class WorldUtils {

	public static World createWorld() {
		World world = new World(new Vector2(0f, -9.8f), true);
		return world;
	}

	public static Body createGround(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position
				.set(new Vector2(Constants.GROUND_X, Constants.GROUND_Y));

		Body body = world.createBody(bodyDef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(Constants.GROUND_WIDTH, Constants.GROUND_HEIGHT / 2);
		body.createFixture(shape, Constants.GROUND_DENSITY);
		shape.dispose();

		return body;
	}

	/* PROCESS STROKE FUNCTIONS */

	public static Stroke processStroke(Array<Vector2> input, World world) {
		Array<Vector2> points = new Array<Vector2>();
		Stroke res = null;
		if (input.size > 8) {
//			checkCirclesAndRectangles(input);
			points = simplify(input);

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

	private static void checkCirclesAndRectangles(Array<Vector2> input) {
		Vector2 firstPoint = input.get(0);
		Vector2 lastPoint = input.get(input.size - 1);

		if (firstPoint.dst(lastPoint) < 0.8f) {
			System.out.println("CIRCULO O CUADRADO");
			boolean isCollinear = true;
			for (int i = 0; i < input.size - 1; i++) {
				Vector2 previous = input.get(i);
				Vector2 next = input.get(i + 1);
				isCollinear = previous.isOnLine(next, 1f);
				if (!isCollinear) {
					System.out
							.println("======== FORMA --> CIRCULO ===========");
					break;
				}
			}
		}
	}

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

			// CircleShape shape = new CircleShape();
			// shape.setPosition(dir.cpy().add(point));
			// shape.setRadius(dir.len()/2);

			body.createFixture(shape, 1.0f).setUserData(fixtureStroke);
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

	// private static Array<Vector2> extrudePoints(Array<Vector2> input) {
	//
	// Array<Vector2> res = new Array<Vector2>();
	//
	// // res.add(input.get(0));
	//
	// Array<Vector2> upper = new Array<Vector2>();
	// Array<Vector2> lower = new Array<Vector2>();
	//
	// for (int i = 1; i < input.size; i++) {
	//
	// Vector2 a = input.get(i - 1);
	// Vector2 b = input.get(i);
	// Vector2 ndir = b.cpy().sub(a).nor();
	// Vector2 perp = new Vector2(-ndir.y, ndir.x);
	//
	// if (i == 1) {
	// Vector2 upperPoint = a.cpy().sub(
	// perp.cpy().scl(Constants.THICKNESS / 2));
	// upper.add(upperPoint);
	//
	// Vector2 lowerPoint = a.cpy().add(
	// perp.cpy().scl(Constants.THICKNESS / 2));
	// lower.add(lowerPoint);
	// }
	//
	// Vector2 upperPoint = b.cpy().sub(
	// perp.cpy().scl(Constants.THICKNESS / 2));
	// upper.add(upperPoint);
	//
	// Vector2 lowerPoint = b.cpy().add(
	// perp.cpy().scl(Constants.THICKNESS / 2));
	// lower.add(lowerPoint);
	// }
	//
	// res.addAll(upper);
	// lower.reverse();
	// res.addAll(lower);
	// res.add(input.get(0));
	//
	// return res;
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
			if (currentPoint.dst(lastPoint) > Constants.DST_TOLERANCE
					* Constants.DST_TOLERANCE) {
				res.add(currentPoint);
				lastPoint = currentPoint;
			}
		}

		if (!lastPoint.equals(currentPoint)) {
			res.add(currentPoint);
		}

		return res;
	}

	// public static void writeLog(String s) {
	// GameStage.fh.writeString(s, true);
	// }

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

	// private static boolean checkPolygons(Polygon[] polygons) {
	// boolean res = true;
	//
	// for (Polygon p : polygons) {
	//
	// /*
	// * p.getTransformedVertices().length < 6
	// * p.getTransformedVertices().length > 16
	// */
	//
	// if (p.area() < 0.008f) {
	// res = false;
	// break;
	// }
	// }
	// return res;
	// }

	/* ========== CREATE BODIES ACTORS =========================== */

	
	
	/*TODO PASAR POR PARAMETRO EL ANCHO Y EL ALTO*/
	
	public static Body createPlatformBody(World world, Vector2 position, Rectangle rectangle) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		Body res = world.createBody(bodyDef);
		res.setUserData(new PlatformUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rectangle.width/2, rectangle.height/2, position, 0f);

		res.createFixture(shape, 1.0f);

		shape.dispose();

		return res;
	}
	
	public static Body createGroundPlatformBody(World world, Vector2 position){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		Body res = world.createBody(bodyDef);
		res.setUserData(new PlatformUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(Constants.GROUND_WIDTH / 2, Constants.GROUND_HEIGHT / 2, position, 0f);

		res.createFixture(shape, 1.0f);

		shape.dispose();

		return res;
	}
	/*TODO QUITAR LA POSICION DEL SHAPE DE LA CREACION DE CUERPO Y PONER LA POSICION EN EL BODY DEF*/
	public static Body createBall(World world, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(position);
		bodyDef.bullet = true;

		Body res = world.createBody(bodyDef);
		res.setUserData(new BallUserData());
		res.setLinearVelocity(Constants.BALL_LINEAR_VELOCITY,
				res.getLinearVelocity().y);

		CircleShape shape = new CircleShape();
		shape.setRadius(0.3f);
		
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1.0f;
		fixtureDef.friction = 0.1f;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}

	public static Body createTestMortalObstacle(World world, Vector2 position) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;

		Body res = world.createBody(bodyDef);
		res.setUserData(new MortalObstacleUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f, position.scl(0.5f), 0f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}

	/*TODO Unir todos los metodos para crear plataformas sean como sean*/
	public static Body createPlatform1(World world, Vector2 position) {
		BodyEditorLoader loader = new BodyEditorLoader(
				Gdx.files.internal("plataforma3.json"));

		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.type = BodyType.StaticBody;

		FixtureDef fd = new FixtureDef();
		fd.friction = 0.1f;

		// 3. Create a Body, as usual.
		Body res = world.createBody(bodyDef);
		res.setUserData(new PlatformUserData());

		// 4. Create the body fixture automatically by using the loader.
		loader.attachFixture(res, "plataforma3", fd, 15);
		
		System.out.println(res.getFixtureList().size);
		
		return res;
	}
	
	public static Body createFlag(World world, Vector2 position){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(position);

		Body res = world.createBody(bodyDef);
		res.setUserData(new FlagUserData());

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.65f, 1f);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = shape;

		res.createFixture(fixtureDef);

		shape.dispose();
		return res;
	}
}
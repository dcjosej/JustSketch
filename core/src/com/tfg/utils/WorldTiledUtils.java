package com.tfg.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//Class for useful transformations between Tiled objects and World objects
public class WorldTiledUtils {

	public static Rectangle getWorldRectangle(MapObject mapObject) {
		RectangleMapObject tiledRectangle = (RectangleMapObject) mapObject;
		Rectangle res = transformToWorldCoordinates(tiledRectangle
				.getRectangle());
		return res;
	}

	public static Circle getCircleWorld(MapObject mapObject) {
		EllipseMapObject ellipseTiled = (EllipseMapObject) mapObject;
		Circle res = getCircleFromEllipse(ellipseTiled.getEllipse());
		res = transformToWorldCoordinates(res);
		return res;
	}

	public static Rectangle transformToWorldCoordinates(Rectangle rectangle) {
		Rectangle res = new Rectangle(rectangle.x * Constants.UNIT_SCALE,
				rectangle.y * Constants.UNIT_SCALE, rectangle.width
						* Constants.UNIT_SCALE, rectangle.height
						* Constants.UNIT_SCALE);
		return res;
	}

	public static Circle transformToWorldCoordinates(Circle circle) {
		Circle res = new Circle(circle.x * Constants.UNIT_SCALE, circle.y
				* Constants.UNIT_SCALE, circle.radius * Constants.UNIT_SCALE);
		return res;
	}

	public static Circle getCircleFromEllipse(Ellipse ellipse) {
		Circle res = new Circle(new Vector2(ellipse.x, ellipse.y),
				ellipse.width / 2);
		return res;
	}
}

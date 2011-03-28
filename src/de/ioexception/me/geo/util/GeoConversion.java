package de.ioexception.me.geo.util;

import de.ioexception.me.util.MathUtils;

/**
 * Class for conversions between real-world map locations and map image pixel
 * coordinates.
 * 
 * @author Tobias Schlecht
 */
public class GeoConversion
{
	private static final double LAT_MINIMUM = -85.05112878d;
	private static final double LAT_MAXIMUM = 85.05112878d;

	private static final double LNG_MINIMUM = -180.0d;
	private static final double LNG_MAXIMUM = 180.0d;

	/**
	 * Converts a {@link Wgs84Coordinate} value to {@link Point} pixel position.
	 * 
	 * @param wgs84Coordinate A WGS84 coordinate
	 * @param zoom A zoom level
	 * @return The corresponding pixel point
	 */
	public static Point latLngToPixel(Wgs84Coordinate wgs84Coordinate, int zoom)
	{
		double latitude = wgs84Coordinate.getLatitude();
		double longitude = wgs84Coordinate.getLongitude();

		latitude = Math.min(Math.max(latitude, LAT_MINIMUM), LAT_MAXIMUM);
		longitude = Math.min(Math.max(longitude, LNG_MINIMUM), LNG_MAXIMUM);

		double helpX = (longitude + 180.0d) / 360.0d;
		double helpY = 0.5d - MathUtils.log((1.0d + Math.sin(latitude * Math.PI / 180.0d)) / (1.0d - Math.sin(latitude * Math.PI / 180.0d))) / (4.0d * Math.PI);

		int size = (int) 256 << zoom;

		int x = (int) Math.min(Math.max(helpX * size + 0.5d, 0.0d), size - 1.0d);
		int y = (int) Math.min(Math.max(helpY * size + 0.5d, 0.0d), size - 1.0d);

		return new Point(x, y);
	}

	/**
	 * Converts {@link Point} pixel position to {@link Wgs84Coordinate} value.
	 * 
	 * @param pixelPoint A pixel point
	 * @param zoom A zoom level
	 * @return The corresponding WGS84 coordinate
	 */
	public static Wgs84Coordinate pixelToLatLng(Point pixelPoint, int zoom)
	{
		int x = pixelPoint.getX();
		int y = pixelPoint.getY();

		double size = (int) 256 << zoom;

		double helpX = (Math.min(Math.max(x, 0.0d), size - 1.0d) / size) - 0.5d;
		double helpY = 0.5d - (Math.min(Math.max(y, 0.0d), size - 1.0d) / size);

		double latitude = 90.0d - 360.0d * MathUtils.atan(MathUtils.exp(-helpY * 2.0d * Math.PI)) / Math.PI;
		double longitude = 360.0d * helpX;

		return new Wgs84Coordinate(latitude, longitude);
	}
}

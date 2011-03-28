package de.ioexception.me.geo.util;

/**
 * Class for encapsulated a two-dimensinal point coordinate.
 * 
 * @author Tobias Schlecht
 */
public class Point
{
	private final int x;
	private final int y;

	/**
	 * Create a new point with postion (x|y).
	 * 
	 * @param x
	 * @param y
	 */
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns X value.
	 * 
	 * @return
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns Y value.
	 * 
	 * @return
	 */
	public int getY()
	{
		return y;
	}
}

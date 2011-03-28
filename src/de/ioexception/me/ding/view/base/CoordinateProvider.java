package de.ioexception.me.ding.view.base;

import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * An interface for view components that provide a choice of a
 * {@link Wgs84Coordinate}. It defines a method that should be called in order
 * to retrieve the selected {@link Wgs84Coordinate}.
 * 
 * @author Benjamin Erb
 */
public interface CoordinateProvider
{
	/**
	 * Return the selected {@link Wgs84Coordinate}. This method will be called
	 * by the next view in order to get the selection.
	 * 
	 * @return
	 */
	public Wgs84Coordinate getSelectedCoordinate();
}

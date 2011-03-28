package de.ioexception.me.geo.util;

import java.util.Vector;

import de.ioexception.me.geo.services.geocoder.impl.GeoCodingResult;

/**
 * Filters the GeoCodingResults.</br></br>
 * 
 * Only geolocations lying within the box specified by TOP_LEFT and BOTTOM_RIGHT
 * are allowed.
 * 
 * @author Michael Mueller
 */
public class FilterGeoCodingResults
{
	/**
	 * Top left coordinate of bounding box
	 */
	private final static Wgs84Coordinate TOP_LEFT = new Wgs84Coordinate(48.659363, 9.624791);

	/**
	 * Bottom right coordinate of bounding box
	 */
	private final static Wgs84Coordinate BOTTOM_RIGHT = new Wgs84Coordinate(48.136840, 10.500367);

	/**
	 * Filter Vector, items have to lie in the bounding box.
	 * 
	 * @param resultList
	 * @return
	 */
	public static Vector filter(Vector resultList)
	{
		int i = 0;

		while(i < resultList.size())
		{
			GeoCodingResult item = (GeoCodingResult) resultList.elementAt(i);

			if(item.within(TOP_LEFT, BOTTOM_RIGHT) == false)
			{
				resultList.removeElementAt(i);
			}
			else
			{
				i++;
			}
		}
		
		return resultList;
	}
}

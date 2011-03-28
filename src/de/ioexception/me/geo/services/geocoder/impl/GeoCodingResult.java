package de.ioexception.me.geo.services.geocoder.impl;

import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * Wrapper class for geocoding result items.
 * 
 * @author Benjamin Erb
 * @author Michael Mueller
 */
public class GeoCodingResult
{
	private final String addressName;
	private final Wgs84Coordinate position;

	public GeoCodingResult(String addressName, Wgs84Coordinate position)
	{
		super();
		
		this.addressName = addressName;
		this.position = position;
	}

	/**
	 * Returns the formatted (canonical) address name.
	 * 
	 * @return
	 */
	public String getAddressName()
	{
		return addressName;
	}

	/**
	 * Returns the latitude value.
	 * 
	 * @return
	 */
	public double getLatitude()
	{
		return position.getLatitude();
	}

	/**
	 * Returns the longitude value.
	 * 
	 * @return
	 */
	public double getLongitude()
	{
		return position.getLongitude();

	}

	public Wgs84Coordinate getPosition()
	{
		return position;
	}

	/**
	 * Checks whether this item lies within the specified coordinates.
	 * 
	 * @param topLeft
	 * @param bottomRight
	 * @return
	 */
	public boolean within(Wgs84Coordinate topLeft, Wgs84Coordinate bottomRight)
	{
		return position.within(topLeft, bottomRight);
	}
}

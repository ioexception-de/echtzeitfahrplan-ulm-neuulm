package de.ioexception.me.ding.service;

import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * A search result item of a proximity search.
 * 
 * @author Benjamin Erb
 */
public class ProximitySearchResult
{
	private final BusStation station;
	private final double distance;

	public ProximitySearchResult(BusStation station, Wgs84Coordinate targetCoordinate)
	{
		super();

		this.station = station;
		this.distance = station.getPosition().getDistance(targetCoordinate);
	}

	/**
	 * Returns the station
	 * 
	 * @return
	 */
	public BusStation getStation()
	{
		return station;
	}

	/**
	 * Returns the computed distance in meters.
	 * 
	 * @return
	 */
	public double getDistance()
	{
		return distance;
	}

	/**
	 * Returns the rounded distance in meters.
	 * 
	 * @return
	 */
	public int getApproximateDistance()
	{
		return (int) Math.ceil(distance);
	}

	/**
	 * Compares two {@link ProximitySearchResult} by substracting the distances
	 * (this - other).
	 * 
	 * @param otherResult
	 * @return
	 */
	public double compare(ProximitySearchResult otherResult)
	{
		return(distance - otherResult.distance);
	}
}

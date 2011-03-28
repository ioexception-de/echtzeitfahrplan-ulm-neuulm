package de.ioexception.me.geo.services.geocoder.impl;

import java.util.Vector;

import de.ioexception.me.geo.services.GeoCodingService;

/**
 * Callback interface for handling {@link GeoCodingResult} vectors from
 * {@link GeoCodingService} results.
 * 
 * @author Benjamin Erb
 */
public interface GeoCodingCallback
{
	/**
	 * Returns a vector of {@link GeoCodingResult} items.
	 * 
	 * @param results GeoCoderResult vector
	 */
	public void handleResults(Vector results);
}

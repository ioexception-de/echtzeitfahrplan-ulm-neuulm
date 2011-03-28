package de.ioexception.me.geo.services;

import de.ioexception.me.geo.services.geocoder.impl.GeoCodingCallback;

/**
 * @author Benjamin Erb
 */
public interface GeoCodingService
{
	/**
	 * @param address
	 * @param callback
	 */
	public void geocode(String address, GeoCodingCallback callback);
}

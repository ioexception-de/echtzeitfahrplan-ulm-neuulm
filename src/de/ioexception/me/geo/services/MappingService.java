package de.ioexception.me.geo.services;

import de.ioexception.me.geo.services.maps.MapsCallback;

/**
 * @author Benajmin Erb
 */
public interface MappingService
{
	/**
	 * Requests an image tile.
	 * 
	 * @param centerLat center latitude position
	 * @param centerLng center longitude position
	 * @param zoom zoom level
	 * @param width  tile width
	 * @param height  tile height
	 * @param callback callback for handling the tile response
	 */
	public void getMap(double centerLat, double centerLng, int zoom, int width, int height, MapsCallback callback);
}

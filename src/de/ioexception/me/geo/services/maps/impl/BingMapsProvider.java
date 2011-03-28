package de.ioexception.me.geo.services.maps.impl;

import de.ioexception.me.geo.services.MappingService;
import de.ioexception.me.geo.services.maps.MapsCallback;
import de.ioexception.me.http.HttpManager;

/**
 * A {@link MappingService} implementation based on Microsoft Bing Maps REST
 * API.
 * 
 * @author Benjamin Erb
 */
public class BingMapsProvider implements MappingService
{
	private static final String BaseURI = "http://dev.virtualearth.net/REST/V1/Imagery/Map";

	/**
	 * Please use your own key when forking or extending this application or
	 * parts of it.
	 */
	private static final String API_KEY = "";

	private final HttpManager http;

	public BingMapsProvider()
	{
		this.http = HttpManager.getInstance();
	}

	public void getMap(double centerLat, double centerLng, int zoom, int width, int height, MapsCallback callback)
	{
		http.get(getUri(centerLat, centerLng, zoom, width, height), new BingMapsResponseListener(callback));
	}

	private final String getUri(double lat, double lon, int zoom, int width, int height)
	{
		return BaseURI + "/" + "Road/" + lat + "," + lon + "/" + zoom + "?mapVersion=v1" + "&mapSize=" + width + "," + height + "&key=" + API_KEY;
	}
}

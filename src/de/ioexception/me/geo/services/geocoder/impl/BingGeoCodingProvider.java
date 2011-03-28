package de.ioexception.me.geo.services.geocoder.impl;

import de.ioexception.me.geo.services.GeoCodingService;
import de.ioexception.me.geo.services.maps.impl.BingMapsResponseListener;
import de.ioexception.me.http.HttpManager;
import de.ioexception.me.util.UrlEncoder;

/**
 * This Class provides a geocoding service to Bing, it generates a HTTP request
 * according to the Bing REST API specified at
 * http://msdn.microsoft.com/en-us/library/ff701711.aspx
 * 
 * The results of the request are further processed by
 * {@link BingMapsResponseListener}, which displays different address options to
 * the user who then selects the best fitting address.
 * 
 * @author Michael Mueller
 */
public class BingGeoCodingProvider implements GeoCodingService
{
	/**
	 * URL for accessing the Bing REST API
	 */
	private static final String URI = "http://dev.virtualearth.net/REST/v1/Locations/";

	/**
	 * Please use your own key when forking or extending this application or
	 * parts of it.
	 */
	private static final String KEY = "";

	private final HttpManager http;

	public BingGeoCodingProvider()
	{
		this.http = HttpManager.getInstance();
	}

	public void geocode(String address, GeoCodingCallback callback)
	{
		geocode(address, callback);
	}

	public void geocode(String address, String language, GeoCodingCallback callback)
	{
		http.get(generateUri(address), new BingGeoCodingResponseListener(callback));
	}

	private String generateUri(String address)
	{
		return URI + UrlEncoder.encode(address.trim()) + "?o=json&key=" + UrlEncoder.encode(KEY);
	}
}

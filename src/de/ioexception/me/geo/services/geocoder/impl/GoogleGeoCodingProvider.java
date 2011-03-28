package de.ioexception.me.geo.services.geocoder.impl;

import de.ioexception.me.geo.services.GeoCodingService;
import de.ioexception.me.http.HttpManager;
import de.ioexception.me.util.UrlEncoder;

/**
 * A {@link GeoCodingService} implementation that uses the Google Maps API. Due
 * to legal issues (see below) this implementation has neither been used nor
 * tested by us.
 * 
 * <b>Warning:</b> Please note that the Google Maps API Terms of Service
 * currently does not allow the usage of the service without displaying the
 * result on a Google Map.
 * 
 * @author Benjamin Erb
 */
public class GoogleGeoCodingProvider implements GeoCodingService
{
	private static final String URI = "http://maps.google.com/maps/api/geocode/json";
	private static final String DEFAULT_LANGUAGE = "en";

	private final HttpManager http;

	public GoogleGeoCodingProvider()
	{
		this.http = HttpManager.getInstance();
	}

	public void geocode(String address, GeoCodingCallback callback)
	{
		geocode(address, DEFAULT_LANGUAGE, callback);
	}

	/**
	 * Geocodes an address using the given language preference.
	 * 
	 * @param address
	 * @param language
	 * @param callback
	 */
	public void geocode(String address, String language, GeoCodingCallback callback)
	{
		http.get(generateUri(address, language), new GoogleGeoCodingResponseListener(callback));
	}

	private String generateUri(String address, String language)
	{
		return URI + "?address=" + UrlEncoder.encode(address) + "&language=" + UrlEncoder.encode(language) + "&sensor=false";
	}
}

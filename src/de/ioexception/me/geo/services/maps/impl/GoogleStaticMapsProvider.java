package de.ioexception.me.geo.services.maps.impl;

import de.ioexception.me.geo.services.MappingService;
import de.ioexception.me.geo.services.maps.MapsCallback;
import de.ioexception.me.http.HttpManager;

/**
 * A {@link MappingService} implementation based on Google Maps Static API. Due
 * to legal issues (see below) this implementation has neither been used nor
 * tested by us.
 * 
 * <b>Warning:</b> Please note that the Google Maps API Terms of Service
 * currently does not allow the usage of Static Map tiles outside the browser.
 * Thus, usage of this implementation inside a Java ME application is not
 * compliant to the Termos of Service.
 * 
 * @author Benjamin Erb
 * @author Tobias Schlecht
 */
public class GoogleStaticMapsProvider implements MappingService
{
	public static final String MAP_TYPE = "terrain";

	private static final String URI = "http://maps.google.com/maps/api/staticmap";

	private static final String MAP_STYLE =	"&style=" + 
											"feature:road%7C" + 
											"element:all%7C" + 
											"hue:0x2b80be%7C" + 
											"saturation:10%7C" + 
											"lightness:-20" +

											"&style=" + 
											"feature:landscape%7C" + 
											"element:geometry%7C" + 
											"hue:0x000000%7C" + 
											"saturation:-100%7C" + 
											"lightness:-100";

	private final HttpManager http;

	public GoogleStaticMapsProvider()
	{
		this.http = HttpManager.getInstance();
	}

	/**
	 * Requests an image tile.
	 * 
	 * @param centerLat center latitude position
	 * @param centerLng center longitude position
	 * @param zoom zoom level
	 * @param width tile width
	 * @param height tile height
	 * @param callback callback for handling the tile response
	 */
	public void getMap(double centerLat, double centerLng, int zoom, int width, int height, MapsCallback callback)
	{
		if(width > 640 || height > 640)
		{
			throw new IllegalArgumentException("Tile size is too big. Maximum is 640x640 pixels.");
		}

		http.get(URI + "?center=" + centerLat + "," + centerLng + "&zoom=" + zoom + "&size=" + width + "x" + height + "&maptype=" + MAP_TYPE + "&mobile=true" + MAP_STYLE + "&sensor=false", new GoogleStaticMapsResponseListener(callback));
	}
}

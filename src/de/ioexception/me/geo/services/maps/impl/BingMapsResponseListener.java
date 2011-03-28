package de.ioexception.me.geo.services.maps.impl;

import javax.microedition.lcdui.Image;

import de.ioexception.me.geo.services.maps.MapsCallback;
import de.ioexception.me.http.HttpResponse;
import de.ioexception.me.http.HttpResponseListener;

/**
 * Adapter class for handling Bing map results.
 * 
 * @author Benjamin Erb
 */
public class BingMapsResponseListener implements HttpResponseListener
{
	private final MapsCallback mapsCallback;

	public BingMapsResponseListener(MapsCallback mapsCallback)
	{
		this.mapsCallback = mapsCallback;
	}

	public void responseReceived(HttpResponse response)
	{
		if(response.getStatusCode() == 200)
		{
			Image image = Image.createImage(response.getEntity(), 0, response.getEntity().length);
			
			mapsCallback.handleMap(image);
		}
	}
}

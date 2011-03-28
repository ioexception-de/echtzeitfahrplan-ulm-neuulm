package de.ioexception.me.geo.services.maps.impl;

import javax.microedition.lcdui.Image;

import de.ioexception.me.geo.services.maps.MapsCallback;
import de.ioexception.me.http.HttpResponse;
import de.ioexception.me.http.HttpResponseListener;

/**
 * Internal adapter class for mapping {@link HttpResponseListener} to
 * {@link MapsCallback}.
 * 
 * @author Benjamin Erb
 */
class GoogleStaticMapsResponseListener implements HttpResponseListener
{
	private final MapsCallback callback;

	public GoogleStaticMapsResponseListener(MapsCallback callback)
	{
		this.callback = callback;
	}

	public void responseReceived(HttpResponse response)
	{
		if(response.getStatusCode() == 200)
		{
			Image image = Image.createImage(response.getEntity(), 0, response.getEntity().length);
			
			callback.handleMap(image);
		}
	}
}

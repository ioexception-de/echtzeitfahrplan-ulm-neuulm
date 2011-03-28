package de.ioexception.me.geo.services.maps;

import javax.microedition.lcdui.Image;

/**
 * Callback interface for handling retrieved map images.
 * 
 * @author Benjamin Erb
 */
public interface MapsCallback
{
	/**
	 * Returns the requested map tile as {@link Image}.
	 * 
	 * @param mapImage
	 */
	public void handleMap(Image mapImage);
}

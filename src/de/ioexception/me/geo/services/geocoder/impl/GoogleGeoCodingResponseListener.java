package de.ioexception.me.geo.services.geocoder.impl;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import de.ioexception.me.geo.util.FilterGeoCodingResults;
import de.ioexception.me.geo.util.Wgs84Coordinate;
import de.ioexception.me.http.HttpResponse;
import de.ioexception.me.http.HttpResponseListener;

/**
 * Adapter class for handling responses of {@link GoogleGeoCodingProvider}
 * results.
 * 
 * @author Benjamin Erb
 */
class GoogleGeoCodingResponseListener implements HttpResponseListener
{
	private final GeoCodingCallback callback;

	private final static String STATUS = "status";
	private final static String OK = "OK";

	private final static String RESULTS = "results";
	private final static String FORMATTED_ADDRESS = "formatted_address";
	private final static String GEOMETRY = "geometry";
	private final static String LOCATION = "location";
	private final static String LAT = "lat";
	private final static String LON = "lng";

	GoogleGeoCodingResponseListener(GeoCodingCallback callback)
	{
		this.callback = callback;
	}

	public void responseReceived(HttpResponse response)
	{
		Vector results = new Vector();

		if(response.getStatusCode() == 200)
		{
			try
			{
				JSONObject jsonResponse = new JSONObject(new String(response.getEntity(), "UTF8"));
				
				if(jsonResponse.has(STATUS) && jsonResponse.getString(STATUS).equals(OK))
				{
					JSONArray resultArray = jsonResponse.getJSONArray(RESULTS);
					
					for(int i = 0; i < resultArray.length(); i++)
					{
						JSONObject result = resultArray.getJSONObject(i);

						String address = null;
						double[] coordinates = new double[2];

						if(result.has(FORMATTED_ADDRESS))
						{
							address = result.getString(FORMATTED_ADDRESS);
						}

						if(result.has(GEOMETRY))
						{
							JSONObject geometry = result.getJSONObject(GEOMETRY);
							
							if(geometry.has(LOCATION))
							{
								JSONObject location = geometry.getJSONObject(LOCATION);
								
								if(location.has(LAT))
								{
									coordinates[0] = Double.parseDouble(location.getString(LAT));
								}
								
								if(location.has(LON))
								{
									coordinates[1] = Double.parseDouble(location.getString(LON));
								}
							}
						}

						results.addElement(new GeoCodingResult(address, new Wgs84Coordinate(coordinates[0], coordinates[1])));

					}
					
					results = FilterGeoCodingResults.filter(results);
					callback.handleResults(results);
				}
			}
			catch(JSONException e)
			{
				e.printStackTrace();
			}
			catch(UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
	}
}

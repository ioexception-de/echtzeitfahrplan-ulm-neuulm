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
 * Processes the JSON response for the geocoding, from Bing. Filters the
 * resultset so that all locations lie within a bounding box surrounding the
 * region for which the apps busstations area avilable.</br></br>
 * 
 * The boundingbox and the filtering is provided by {@link Wgs84Coordinate}.
 * 
 * @author Michael Mueller
 */
class BingGeoCodingResponseListener implements HttpResponseListener
{
	private final GeoCodingCallback callback;

	private final static String STATUS = "statusCode";
	private final static String OK = "200";
	private final static String ADDRESS = "address";

	private final static String RESOURCES = "resources";
	private final static String RESOURCE_SETS = "resourceSets";
	private final static String COORDINATES = "coordinates";
	private final static String POINT = "point";
	private final static String FORMATTED_ADDRESS = "formattedAddress";

	BingGeoCodingResponseListener(GeoCodingCallback callback)
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
					JSONArray resultArray = jsonResponse.getJSONArray(RESOURCE_SETS);
					JSONObject obj = resultArray.getJSONObject(0);

					resultArray = obj.getJSONArray(RESOURCES);

					for(int i = 0; i < resultArray.length(); i++)
					{
						JSONObject result = resultArray.getJSONObject(i);

						String address = "";
						double[] coordinates = new double[2];

						if(result.has(POINT))
						{
							JSONObject location = result.getJSONObject(POINT);
							
							if(location.has(COORDINATES))
							{
								JSONArray _location = location.getJSONArray(COORDINATES);
								coordinates[0] = Double.parseDouble(_location.getString(0));
								coordinates[1] = Double.parseDouble(_location.getString(1));
							}
						}

						if(result.has(ADDRESS))
						{
							JSONObject _address = result.getJSONObject(ADDRESS);
							
							if(_address.has(FORMATTED_ADDRESS))
							{
								address = _address.getString(FORMATTED_ADDRESS);
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

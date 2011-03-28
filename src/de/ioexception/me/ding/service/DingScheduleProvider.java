package de.ioexception.me.ding.service;

import java.util.Date;

import de.ioexception.me.http.Http;
import de.ioexception.me.http.HttpManager;
import de.ioexception.me.util.UrlEncoder;

/**
 * A provider of data fetched from the SWU webpage. The code is currently
 * designed to fit the webpage structure available in March 2011 and will
 * eventually not work anymore when changed.
 * 
 * @author Benjamin Erb
 */
public class DingScheduleProvider
{
	private static final String URI = "http://www.swu.de/privatkunden/nahverkehr/fahr-und-stadtplan/echtzeitdaten.html";

	private final HttpManager http;

	public DingScheduleProvider()
	{
		this.http = HttpManager.getInstance();
	}

	/**
	 * Dispatches a request against the SWU website fetching the Ajax response.
	 * 
	 * @param station
	 * @param callback
	 */
	public void getSchedule(BusStation station, DingScheduleCallback callback)
	{
		long timestamp = new Date().getTime();

		String data = "xajax=processFormData&xajaxr=" + timestamp + "&xajaxargs[]=%3Cxjxquery%3E%3Cq%3Etx_z7departuremonitor_pi1%5Bstations%5D%3D" + UrlEncoder.encode(station.getAjaxString()) + "%26tx_z7departuremonitor_pi1%5Bperiods%5D%3D0%26tx_z7departuremonitor_pi1%5Bperiods_limit%5D%3D60%26tx_z7departuremonitor_pi1%5BbtSend%5D%3DAktualisieren%26tx_z7departuremonitor_pi1%5Bno_cache%5D%3D1%3C%2Fq%3E%3C%2Fxjxquery%3E";

		http.request(Http.POST, URI).header(Http.REFERER, "http://www.swu.de/privatkunden/nahverkehr/fahr-und-stadtplan/echtzeitdaten.html").header(Http.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8").header(Http.PRAGMA, "no-cache").header(Http.CACHE_CONTROL, "no-cache").entity(data.getBytes()).send(new DingScheduleResponseListener(callback));
	}
}

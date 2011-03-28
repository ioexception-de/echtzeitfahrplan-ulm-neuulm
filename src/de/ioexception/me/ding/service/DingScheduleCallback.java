package de.ioexception.me.ding.service;

import java.util.Vector;

/**
 * Callback interface for schedule results.
 * 
 * @author Benjamin Erb
 */
public interface DingScheduleCallback
{
	/**
	 * Handle the result list of {@link DingScheduleResult}s.
	 * 
	 * @param results
	 */
	public void handleSchedule(Vector results);
}

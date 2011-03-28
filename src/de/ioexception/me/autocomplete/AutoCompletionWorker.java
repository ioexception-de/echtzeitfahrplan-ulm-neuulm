package de.ioexception.me.autocomplete;

import java.util.Vector;

import de.ioexception.me.ding.service.DingStations;
import de.ioexception.me.ding.view.StationListView;
import de.ioexception.me.util.BlockingQueue;

/**
 * A worker that processes enqueued requests through a shared
 * {@link BlockingQueue}.
 * 
 * @author Michael Mueller
 */
public class AutoCompletionWorker implements AutoCompletionHandler
{

	private final BlockingQueue requestQueue;
	private boolean running = true;
	private StationListView stationSelection;

	public AutoCompletionWorker(BlockingQueue requestQueue, StationListView stationSelection)
	{
		this.requestQueue = requestQueue;
		this.stationSelection = stationSelection;
	}

	public void run()
	{
		String request;
		try
		{
			while(running && (request = ((String) requestQueue.take())) != null)
			{
				handle(request);
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void cancel()
	{
		running = false;
	}

	public String handle(String term)
	{
		Vector results = DingStations.getStationsByPrefix(term, 10);
		stationSelection.fill(results);

		return null;
	}
}

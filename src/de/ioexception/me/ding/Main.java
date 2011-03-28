package de.ioexception.me.ding;

import java.util.Stack;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.ioexception.me.ding.service.BusStation;
import de.ioexception.me.ding.service.DingStations;
import de.ioexception.me.ding.view.FavoriteListView;
import de.ioexception.me.ding.view.GeoResultListView;
import de.ioexception.me.ding.view.GeocodingSearchView;
import de.ioexception.me.ding.view.HomeView;
import de.ioexception.me.ding.view.InfoView;
import de.ioexception.me.ding.view.MapSearchView;
import de.ioexception.me.ding.view.SplashView;
import de.ioexception.me.ding.view.StationDetailsView;
import de.ioexception.me.ding.view.StationListView;
import de.ioexception.me.ding.view.base.BusStationProvider;
import de.ioexception.me.ding.view.base.CoordinateProvider;
import de.ioexception.me.ding.view.base.Refreshable;
import de.ioexception.me.geo.util.Wgs84Coordinate;

/**
 * Main {@link MIDlet} class. Provides access to different views that maintain
 * their state.</br></br>
 * 
 * Administrates the application workflow and initiates the different views.
 * 
 * @author Tobias Schlecht
 * @author Benjamin Erb
 * @author Michael Mueller
 */
public final class Main extends MIDlet
{
	private static final int INIT_STATION_ID = 9001008;

	public static final int HOME_VIEW = 0;
	public static final int STATION_LIST_VIEW = 1;
	public static final int FAVORITE_LIST_VIEW = 2;
	public static final int MAP_SEARCH_VIEW = 3;
	public static final int GEOCODING_SEARCH_VIEW = 4;
	public static final int GEO_RESULT_LIST_VIEW = 5;
	public static final int STATION_DETAILS_VIEW = 6;
	public static final int INFO_VIEW = 7;

	public static final String HOME_VIEW_TITLE = "Echtzeit-Fahrplan";
	public static final String STATION_LIST_VIEW_TITLE = "Haltestellen";
	public static final String FAVORITE_LIST_VIEW_TITLE = "Favoriten";
	public static final String MAP_SEARCH_VIEW_TITLE = "Kartensuche";
	public static final String GEOCODING_SEARCH_VIEW_TITLE = "Ortsabfrage";
	public static final String GEO_RESULT_LIST_VIEW_TITLE = "Haltestellen";
	public static final String STATION_DETAILS_VIEW_TITLE = "Abfahrtszeiten";
	public static final String INFO_VIEW_TITLE = "Informationen";

	private SplashView splashView;
	private HomeView homeView;
	private StationListView stationListView;
	private FavoriteListView favoriteListView;
	private MapSearchView mapSearchView;
	private GeocodingSearchView geocodingSearchView;
	private GeoResultListView geoResultListView;
	private StationDetailsView stationDetailsView;
	private InfoView infoView;

	private Stack viewHistory;

	public Main()
	{
		viewHistory = new Stack();
		splashView = new SplashView(this);
	}

	public void startApp() throws MIDletStateChangeException
	{
		Displayable current = Display.getDisplay(this).getCurrent();

		if(current == null)
		{
			Display.getDisplay(this).setCurrent(splashView);

			homeView = new HomeView(this);
			stationListView = new StationListView(this);
			favoriteListView = new FavoriteListView(this);
			mapSearchView = new MapSearchView(this, DingStations.getStation(INIT_STATION_ID).getPosition());
			geocodingSearchView = new GeocodingSearchView(this);
			geoResultListView = new GeoResultListView(this);
			stationDetailsView = new StationDetailsView(this);
			infoView = new InfoView(this);
		}
	}

	public void pauseApp()
	{

	}

	public void destroyApp(boolean unconditional)
	{

	}

	public Wgs84Coordinate getPreviousViewCoordinate()
	{
		Displayable prev = getView(getPreviousView());

		if(prev instanceof CoordinateProvider)
		{
			return ((CoordinateProvider) prev).getSelectedCoordinate();
		}
		else
		{
			throw new RuntimeException("Invalid State");
		}
	}

	public BusStation getPreviousViewBusStation()
	{
		Displayable prev = getView(getPreviousView());

		if(prev instanceof BusStationProvider)
		{
			return ((BusStationProvider) prev).getSelectedStation();
		}
		else
		{
			throw new RuntimeException("Invalid State");
		}
	}

	public Displayable getView(int viewCode)
	{
		switch(viewCode)
		{
			case HOME_VIEW: 				return homeView;
			case STATION_LIST_VIEW: 		return stationListView;
			case FAVORITE_LIST_VIEW: 		return favoriteListView;
			case MAP_SEARCH_VIEW: 			return mapSearchView;
			case GEOCODING_SEARCH_VIEW: 	return geocodingSearchView;
			case GEO_RESULT_LIST_VIEW: 		return geoResultListView;
			case STATION_DETAILS_VIEW: 		return stationDetailsView;
			case INFO_VIEW: 				return infoView;
			default: 						return homeView;
		}
	}

	public void setView(int viewCode)
	{
		addToViewHistory(viewCode);

		Displayable view = getView(viewCode);

		getDisplay().setCurrent(view);

		if(view instanceof Refreshable)
		{
			try
			{
				((Refreshable) view).refresh();
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
		}
	}

	public int getPreviousView()
	{
		if(viewHistory.size() > 1)
		{
			return ((Integer) viewHistory.elementAt(viewHistory.size() - 2)).intValue();
		}
		else
		{
			return HOME_VIEW;
		}
	}

	public void goBack()
	{
		if(viewHistory.size() == 1)
		{
			setView(Main.HOME_VIEW);
		}
		else if(viewHistory.size() == 2)
		{
			setView(((Integer) viewHistory.elementAt(0)).intValue());
		}
		else if(viewHistory.size() == 3)
		{
			setView(((Integer) viewHistory.elementAt(1)).intValue());
		}
	}

	private void addToViewHistory(int viewCode)
	{
		if(viewCode >= 1 && viewCode <= 4)
		{
			viewHistory.removeAllElements();
			viewHistory.push(new Integer(viewCode));
		}
		else if(viewCode == 5)
		{
			if(viewHistory.size() == 1)
			{
				viewHistory.push(new Integer(viewCode));
			}
			else if(viewHistory.size() == 3)
			{
				viewHistory.pop();
			}
		}
		else if(viewCode == 6)
		{
			viewHistory.push(new Integer(viewCode));
		}
	}

	public Display getDisplay()
	{
		return Display.getDisplay(this);
	}

	public HomeView getHome()
	{
		return homeView;
	}

	public StationListView getStationSelection()
	{
		return stationListView;
	}

	public FavoriteListView getFavoriteList()
	{
		return favoriteListView;
	}

	public MapSearchView getLocationSearch()
	{
		return mapSearchView;
	}

	public GeocodingSearchView getGeocodingSearch()
	{
		return geocodingSearchView;
	}

	public GeoResultListView getGeoResultList()
	{
		return geoResultListView;
	}

	public StationDetailsView getStationList()
	{
		return stationDetailsView;
	}
}

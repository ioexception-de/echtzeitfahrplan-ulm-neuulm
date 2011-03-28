package de.ioexception.me.ding.view;

import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;

import de.ioexception.me.ding.Main;
import de.ioexception.me.ding.service.BusStation;
import de.ioexception.me.ding.service.DingScheduleCallback;
import de.ioexception.me.ding.service.DingScheduleProvider;
import de.ioexception.me.ding.service.DingScheduleResult;
import de.ioexception.me.ding.view.base.Refreshable;
import de.ioexception.me.ding.view.items.HeadingItem;
import de.ioexception.me.ding.view.items.LineItem;
import de.ioexception.me.ding.view.items.LoadItem;
import de.ioexception.me.ding.view.items.StationItem;
import de.ioexception.me.ding.view.items.StationMapItem;
import de.ioexception.me.geo.services.MappingService;
import de.ioexception.me.geo.services.maps.MapsCallback;
import de.ioexception.me.geo.services.maps.impl.BingMapsProvider;
import de.ioexception.me.util.Barrier;

/**
 * @author Tobias Schlecht
 * @author Michael Mueller
 */
public final class StationDetailsView extends Form implements CommandListener, Refreshable, ItemStateListener
{
	protected static final int MAX_RESULTS = 10;

	private final Main midlet;

	private final Command screenCommand;
	private final Command addFavoriteCommand;
	private final Command removeFavoriteCommand;
	private final Command exitCommand;
	private final Command refreshCommand;

	private BusStation currentStation;

	private StationMapItem stationMapItem = null;
	private final Vector stationItems = new Vector();

	private LineItem noResultItem = null;
	private HeadingItem headingItem = null;

	private final DingScheduleProvider dingScheduleProvider;
	private final MappingService mappingService;

	public StationDetailsView(Main midlet)
	{
		super(Main.STATION_DETAILS_VIEW_TITLE);

		this.midlet = midlet;

		screenCommand = new Command("Zurück", Command.ITEM, 1);
		removeFavoriteCommand = new Command("Favorit entfernen", Command.HELP, 1);
		addFavoriteCommand = new Command("Zu Favoriten", Command.HELP, 1);
		refreshCommand = new Command("Aktualisieren", Command.HELP, 1);
		exitCommand = new Command("Ende", Command.EXIT, 1);

		addCommand(screenCommand);
		addCommand(addFavoriteCommand);
		addCommand(refreshCommand);
		addCommand(exitCommand);
		setCommandListener(this);
		setItemStateListener(this);

		dingScheduleProvider = new DingScheduleProvider();
		mappingService = new BingMapsProvider();
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == screenCommand)
		{
			midlet.goBack();
		}
		else if(c == refreshCommand)
		{
			refresh(false);
		}
		else if(c == addFavoriteCommand)
		{
			midlet.getFavoriteList().addFavorite(midlet.getPreviousViewBusStation());

			Alert alert = new Alert("Info", "Die Haltestelle wurde zu den Favoriten hinzugefügt!", null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			midlet.getDisplay().setCurrent(alert);

			showFavoriteCommand(removeFavoriteCommand);
		}
		else if(c == removeFavoriteCommand)
		{
			midlet.getFavoriteList().removeFavorite(midlet.getPreviousViewBusStation());

			Alert alert = new Alert("Info", "Die Haltestelle wurde aus den Favoriten entfernt!", null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			midlet.getDisplay().setCurrent(alert);

			showFavoriteCommand(addFavoriteCommand);
		}
		else if(c == removeFavoriteCommand)
		{
			midlet.getFavoriteList().removeFavorite(midlet.getPreviousViewBusStation());
		}
		else if(c == exitCommand)
		{
			midlet.destroyApp(false);
			midlet.notifyDestroyed();
		}
	}

	public void showFavoriteCommand(Command cmd)
	{
		if(cmd == addFavoriteCommand)
		{
			removeCommand(removeFavoriteCommand);
			addCommand(addFavoriteCommand);
		}
		else
		{
			removeCommand(addFavoriteCommand);
			addCommand(removeFavoriteCommand);
		}
	}

	private void showResults()
	{
		final Vector resultList = stationItems;
		final HeadingItem header = headingItem;
		final Form that = this;
		final LineItem noResults = noResultItem;
		final StationMapItem mapItem = stationMapItem;

		Display d = Display.getDisplay(midlet);
		
		d.callSerially(new Runnable()
		{
			public void run()
			{
				that.deleteAll();
				
				if(header != null)
				{
					that.append(header);
				}

				if(mapItem != null)
				{
					that.append(mapItem);
				}

				if(resultList != null && resultList.size() > 0)
				{
					int maxSize = resultList.size();
					
					if(maxSize > MAX_RESULTS)
					{
						maxSize = MAX_RESULTS;
					}

					for(int i = 0; i < maxSize; i++)
					{
						that.append((Item) resultList.elementAt(i));
					}
				}
				else
				{
					that.append(noResults);
				}
			}
		});
	}

	public void refresh()
	{
		refresh(true);
	}

	public void refresh(boolean updateStation)
	{
		if(updateStation || currentStation == null)
		{
			currentStation = midlet.getPreviousViewBusStation();
		}

		deleteAll();

		append(new LoadItem());

		stationItems.removeAllElements();
		stationMapItem = null;

		final Vector resultList = stationItems;

		if(midlet.getFavoriteList().isFavorite(currentStation.getId()))
		{
			showFavoriteCommand(removeFavoriteCommand);
		}
		else
		{
			showFavoriteCommand(addFavoriteCommand);
		}

		headingItem = new HeadingItem(currentStation.getName(), currentStation.getPlace());
		noResultItem = new LineItem("Keine Treffer!");

		final Barrier b = new Barrier(2, new Runnable()
		{
			public void run()
			{
				showResults();
			}
		});

		dingScheduleProvider.getSchedule(currentStation, new DingScheduleCallback()
		{
			public void handleSchedule(Vector results)
			{
				if(results.size() > 1)
				{
					for(int i = 0; i < results.size(); i++)
					{
						DingScheduleResult result = (DingScheduleResult) results.elementAt(i);
						resultList.addElement(new StationItem(result.getLine(), result.getDestination(), result.getDeparture()));
					}
				}
				
				b.submit();
			}
		});

		mappingService.getMap(currentStation.getPosition().getLatitude(), currentStation.getPosition().getLongitude(), 15, 225, 94, new MapsCallback()
		{
			public void handleMap(Image mapImage)
			{
				stationMapItem = new StationMapItem(mapImage);
				
				b.submit();
			}
		});
	}

	public void itemStateChanged(Item arg0)
	{
		
	}
}

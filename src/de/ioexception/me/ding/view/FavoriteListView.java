package de.ioexception.me.ding.view;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.rms.RecordStore;

import de.ioexception.me.ding.Main;
import de.ioexception.me.ding.service.BusStation;
import de.ioexception.me.ding.service.DingStations;
import de.ioexception.me.ding.view.base.BusStationProvider;
import de.ioexception.me.util.Strings;

/**
 * @author Tobias Schlecht
 * @author Michael Mueller
 */
public final class FavoriteListView extends Form implements CommandListener, ItemCommandListener, ItemStateListener, BusStationProvider
{
	private final Main midlet;

	private final Command screenCommand;
	private final Command itemCommand;
	private final Command exitCommand;
	private final Command deleteCommand;
	private final Command choiceGrpClick;

	private ChoiceGroup choiceGrp;
	private RecordStore rsFavs = null;
	private Vector favoriteStations = new Vector();

	public FavoriteListView(Main midlet)
	{
		super(Main.FAVORITE_LIST_VIEW_TITLE);

		this.midlet = midlet;

		screenCommand = new Command("Zurück", Command.SCREEN, 1);
		itemCommand = new Command("OK", Command.ITEM, 1);
		exitCommand = new Command("Ende", Command.EXIT, 1);
		deleteCommand = new Command("Löschen", Command.EXIT, 1);
		choiceGrpClick = new Command("OK", Command.SCREEN, 1);

		addCommand(screenCommand);
		addCommand(itemCommand);
		addCommand(exitCommand);
		setCommandListener(this);
		setItemStateListener(this);

		choiceGrp = new ChoiceGroup(null, Choice.EXCLUSIVE, new String[] {}, null);
		choiceGrp.setItemCommandListener(this);
		choiceGrp.addCommand(choiceGrpClick);
		append(choiceGrp);

		loadFromRecordStore();
	}

	/**
	 * Writes the favoriteStations to favorites.db.
	 */
	private void writeToRecordStore()
	{
		try
		{
			RecordStore.deleteRecordStore("favorites.db");
			rsFavs = RecordStore.openRecordStore("favorites.db", true);
			
			String output = "";
			
			if(favoriteStations != null)
			{
				// join busstations to one comma-separated string
				
				for(int i = 0; i < favoriteStations.size(); i++)
				{
					BusStation el = (BusStation) favoriteStations.elementAt(i);
					output += ";" + el.getId();
				}
			}

			byte[] emptyAsBytes = output.getBytes();
			rsFavs.addRecord(emptyAsBytes, 0, output.length());
			rsFavs.closeRecordStore();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Load favorites from favorites.db to favoriteStations
	 */
	private void loadFromRecordStore()
	{
		try
		{
			// open rs, if not available: create rs
			
			rsFavs = RecordStore.openRecordStore("favorites.db", true);
			favoriteStations = new Vector();

			choiceGrp.deleteAll();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		try
		{
			if(rsFavs.getNumRecords() > 0)
			{
				byte[] firstRecord = rsFavs.getRecord(1);
				
				if(firstRecord != null)
				{
					String favsFromRS = new String(firstRecord);
					String[] favIds = Strings.split(favsFromRS, ";");
					
					for(int i = 0; i < favIds.length; i++)
					{
						if(favIds[i] != null && favIds[i].equals("") == false)
						{
							int stopId = Integer.parseInt(favIds[i]);
							BusStation el = DingStations.getStation(stopId);

							favoriteStations.addElement(el);
							int n = choiceGrp.append(el.getName() + " (" + el.getPlace() + ")", null);
							choiceGrp.setFont(n, Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
						}
					}

					// set remove button, because list now has elements that can be removed
					
					removeCommand(exitCommand);
					addCommand(deleteCommand);
				}
			}
			
			rsFavs.closeRecordStore();
		}
		catch(Exception e)
		{
			return;
		}
	}

	public void commandAction(Command c, Displayable d)
	{
		if(c == screenCommand)
		{
			midlet.goBack();
		}
		else if(c == itemCommand)
		{
			gotoStationList();
		}
		else if(c == deleteCommand)
		{
			removeFavorite(getSelectedStation());
		}
		else if(c == exitCommand)
		{
			midlet.destroyApp(false);
			midlet.notifyDestroyed();
		}
	}

	public void removeFavorite(BusStation station)
	{
		// ensure that favorites have been loaded
		
		loadFromRecordStore();

		// which favorite is busStation
		
		for(int i = 0; i < favoriteStations.size(); i++)
		{
			BusStation el = (BusStation) favoriteStations.elementAt(i);
			
			if(el.getId() == station.getId())
			{
				favoriteStations.removeElementAt(i);
				writeToRecordStore();
				choiceGrp.delete(i);

				if(favoriteStations.size() == 0)
				{
					// nothing can be deleted if empty
					
					removeCommand(deleteCommand);
					addCommand(exitCommand);
				}
				
				return;
			}
		}
	}

	public void removeAllFavorites()
	{
		try
		{
			RecordStore.deleteRecordStore("favorites.db");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(choiceGrp != null)
		{
			choiceGrp.deleteAll();
		}
	}

	public void addFavorite(BusStation busStation)
	{
		loadFromRecordStore();

		// is the favorite alread in there
		
		for(int i = 0; i < favoriteStations.size(); i++)
		{
			BusStation el = (BusStation) favoriteStations.elementAt(i);
			if(el.getId() == busStation.getId())
			{
				return;
			}
		}

		if(favoriteStations.size() == 0)
		{
			// set remove button, because list now has elements that can be removed
			
			removeCommand(exitCommand);
			addCommand(deleteCommand);
		}

		favoriteStations.addElement(busStation);
		writeToRecordStore();

		// display result to user at last
		
		int n = choiceGrp.append(busStation.getName() + " (" + busStation.getPlace() + ")", null);
		choiceGrp.setFont(n, Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
	}

	public void gotoStationList()
	{
		if(favoriteStations.size() > 0)
		{
			midlet.setView(Main.STATION_DETAILS_VIEW);
		}
	}

	public void itemStateChanged(Item c)
	{
		if(c == choiceGrp)
		{
			gotoStationList();
		}
	}

	public BusStation getSelectedStation()
	{
		return (BusStation) favoriteStations.elementAt(choiceGrp.getSelectedIndex());
	}

	public void commandAction(Command arg0, Item arg1)
	{
		
	}

	public boolean isFavorite(int stopId)
	{
		loadFromRecordStore();
		
		if(favoriteStations.size() > 0)
		{
			for(int i = 0; i < favoriteStations.size(); i++)
			{
				BusStation el = (BusStation) favoriteStations.elementAt(i);
				
				if(el.getId() == stopId)
				{
					return true;
				}
			}
		}

		return false;
	}
}
